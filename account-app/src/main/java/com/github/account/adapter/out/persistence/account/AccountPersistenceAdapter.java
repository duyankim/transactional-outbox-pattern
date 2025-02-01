package com.github.account.adapter.out.persistence.account;

import com.github.account.domain.Activity;
import jakarta.persistence.EntityNotFoundException;
import com.github.account.application.port.out.LoadAccountPort;
import com.github.account.application.port.out.UpdateAccountStatePort;
import com.github.account.common.PersistenceAdapter;
import com.github.account.domain.Account;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@PersistenceAdapter
@Repository
@Transactional
public class AccountPersistenceAdapter implements LoadAccountPort, UpdateAccountStatePort {

    private final SpringDataAccountRepository accountRepository;
    private final ActivityRepository activityRepository;
    private final AccountMapper accountMapper;

    @Override
    public Account loadAccount(Account.AccountId accountId, LocalDateTime baselineDate) {
        AccountJpaEntity account = accountRepository.findById(accountId.value())
                .orElseThrow(EntityNotFoundException::new);

        List<ActivityJpaEntity> activities = activityRepository.findByOwnerSince(accountId.value(), baselineDate);

        BigDecimal withdrawalBalance = orZero(activityRepository.getWithdrawalBalanceUntil(accountId.value(), baselineDate));

        BigDecimal depositBalance = orZero(activityRepository.getDepositBalanceUntil(accountId.value(), baselineDate));

        return accountMapper.mapToDomainEntity(
                account,
                activities,
                withdrawalBalance,
                depositBalance);
    }

    private BigDecimal orZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    @Override
    public List<Activity> updateActivities(Account account) {
        List<Activity> savedActivities = new ArrayList<>();
        for (Activity activity : account.getActivityWindow().getActivities()) {
            if (activity.getId() == null) {
                activityRepository.save(accountMapper.mapToJpaEntity(activity));
                ActivityJpaEntity savedEntity = activityRepository.save(accountMapper.mapToJpaEntity(activity));
                savedActivities.add(accountMapper.mapToDomainEntity(savedEntity));
            }
        }
        return savedActivities;
    }
}
