package com.github.account.application.service;

import com.github.account.application.event.EnrichedDomainEvent;
import com.github.account.application.port.in.SendMoneyCommand;
import com.github.account.application.port.in.SendMoneyUseCase;
import com.github.account.application.port.out.AccountLock;
import com.github.account.application.port.out.LoadAccountPort;
import com.github.account.application.port.out.UpdateAccountStatePort;
import com.github.account.common.UseCase;
import com.github.account.domain.Account;
import com.github.account.domain.Activity;
import com.github.account.domain.SendMoneyDomainEvent;
import com.github.account.scheduler.OutboxMessageRelay;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@UseCase
@Transactional
public class SendMoneyService implements SendMoneyUseCase {

    private static Logger logger = LoggerFactory.getLogger(SendMoneyService.class);
    private final LoadAccountPort loadAccountPort;
    private final AccountLock accountLock;
    private final UpdateAccountStatePort updateAccountStatePort;
    private final MoneyTransferProperties moneyTransferProperties;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean sendMoney(SendMoneyCommand command) {

        checkThreshold(command);

        LocalDateTime baselineDate = LocalDateTime.now().minusDays(10);

        Account sourceAccount = loadAccountPort.loadAccount(
                command.getSourceAccountId(),
                baselineDate);

        Account targetAccount = loadAccountPort.loadAccount(
                command.getTargetAccountId(),
                baselineDate);

        Account.AccountId sourceAccountId = sourceAccount.getId()
                .orElseThrow(() -> new IllegalStateException("expected source account ID not to be empty"));
        Account.AccountId targetAccountId = targetAccount.getId()
                .orElseThrow(() -> new IllegalStateException("expected target account ID not to be empty"));

        logger.info("Starting send money business event");

        accountLock.lockAccount(sourceAccountId);
        if (!sourceAccount.withdraw(command.getMoney(), targetAccountId)) {
            accountLock.releaseAccount(sourceAccountId);
            return false;
        }

        accountLock.lockAccount(targetAccountId);
        if (!targetAccount.deposit(command.getMoney(), sourceAccountId)) {
            accountLock.releaseAccount(sourceAccountId);
            accountLock.releaseAccount(targetAccountId);
            return false;
        }

        List<Activity> sourceAccountActivities = updateAccountStatePort.updateActivities(sourceAccount);
        List<Activity> targetAccountActivities = updateAccountStatePort.updateActivities(targetAccount);

        Activity withdrawalActivity = sourceAccountActivities.stream()
                .filter(activity -> activity.getSourceAccountId().equals(sourceAccountId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Withdrawal activity not found"));

        accountLock.releaseAccount(sourceAccountId);
        accountLock.releaseAccount(targetAccountId);

        logger.info("Publishing event messages - activity Id: {}", withdrawalActivity.getId().value());

        eventPublisher.publishEvent(
                EnrichedDomainEvent.builder()
                        .aggregateType("account")
                        .aggregateId(sourceAccountId.value())
                        .domainEvent(
                                SendMoneyDomainEvent.builder()
                                        .activityId(withdrawalActivity.getId().value())
                                        .sourceAccountId(sourceAccountId.value())
                                        .targetAccountId(targetAccountId.value())
                                        .amount(command.getMoney().getAmount())
                                        .timestamp(withdrawalActivity.getTimestamp())
                                        .build()
                        )
        );
        return true;
    }

    private void checkThreshold(SendMoneyCommand command) {
        if(command.getMoney().isGreaterThan(moneyTransferProperties.getMaximumTransferThreshold())){
            throw new ThresholdExceededException(moneyTransferProperties.getMaximumTransferThreshold(), command.getMoney());
        }
    }

}
