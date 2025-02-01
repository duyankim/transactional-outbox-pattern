package com.github.account.domain;

import com.github.account.application.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@ToString
@Builder
public class SendMoneyDomainEvent extends DomainEvent {

    private Long activityId;
    private String sourceAccountId;
    private String targetAccountId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}
