package com.github.account.adapter.out.persistence.account;

import com.github.account.domain.Account;
import com.github.account.domain.Activity;
import com.github.account.domain.ActivityWindow;
import com.github.account.domain.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
class AccountMapper {

    Account mapToDomainEntity(
            AccountJpaEntity account,
            List<ActivityJpaEntity> activities,
            BigDecimal withdrawalBalance,
            BigDecimal depositBalance) {

        Money baselineBalance = Money.subtract(
                Money.of(depositBalance),
                Money.of(withdrawalBalance));

        return Account.withId(
                new Account.AccountId(account.getId()),
                baselineBalance,
                mapToActivityWindow(activities));

    }

    ActivityWindow mapToActivityWindow(List<ActivityJpaEntity> activities) {
        List<Activity> mappedActivities = new ArrayList<>();

        for (ActivityJpaEntity activity : activities) {
            mappedActivities.add(new Activity(
                    new Activity.ActivityId(activity.getId()),
                    new Account.AccountId(activity.getOwnerAccountId()),
                    new Account.AccountId(activity.getSourceAccountId()),
                    new Account.AccountId(activity.getTargetAccountId()),
                    activity.getTimestamp(),
                    Money.of(activity.getAmount())));
        }

        return new ActivityWindow(mappedActivities);
    }

    ActivityJpaEntity mapToJpaEntity(Activity activity) {
        return new ActivityJpaEntity(
                activity.getId() == null ? null : activity.getId().value(),
                activity.getTimestamp(),
                activity.getOwnerAccountId().value(),
                activity.getSourceAccountId().value(),
                activity.getTargetAccountId().value(),
                activity.getMoney().getAmount());
    }

    public Activity mapToDomainEntity(ActivityJpaEntity savedEntity) {
        return new Activity(
                new Activity.ActivityId(savedEntity.getId()),
                new Account.AccountId(savedEntity.getOwnerAccountId()),
                new Account.AccountId(savedEntity.getSourceAccountId()),
                new Account.AccountId(savedEntity.getTargetAccountId()),
                savedEntity.getTimestamp(),
                Money.of(savedEntity.getAmount()));
    }
}
