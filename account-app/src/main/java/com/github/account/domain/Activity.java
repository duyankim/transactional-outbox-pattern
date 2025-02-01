package com.github.account.domain;

import java.time.LocalDateTime;

import com.github.account.application.event.DomainEvent;
import lombok.*;

/**
 * {@link Account} 간의 돈 이체 활동입니다.
 */
@EqualsAndHashCode(callSuper = true)
@Value
@RequiredArgsConstructor
public class Activity extends DomainEvent {

    @Getter
    private ActivityId id;

    /**
     * 이 활동을 소유한 계좌입니다.
     */
    @Getter
    @NonNull
    private final Account.AccountId ownerAccountId;

    /**
     * 인출된 계좌입니다.
     */
    @Getter
    @NonNull
    private final Account.AccountId sourceAccountId;

    /**
     * 입금된 계좌입니다.
     */
    @Getter
    @NonNull
    private final Account.AccountId targetAccountId;

    /**
     * 활동의 타임스탬프입니다.
     */
    @Getter
    @NonNull
    private final LocalDateTime timestamp;

    /**
     * 계좌 간에 이체된 돈입니다.
     */
    @Getter
    @NonNull
    private final Money money;

    public Activity(
            @NonNull Account.AccountId ownerAccountId,
            @NonNull Account.AccountId sourceAccountId,
            @NonNull Account.AccountId targetAccountId,
            @NonNull LocalDateTime timestamp,
            @NonNull Money money) {
        this.id = null;
        this.ownerAccountId = ownerAccountId;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.timestamp = timestamp;
        this.money = money;
    }

    public record ActivityId(Long value) {
    }

}
