package com.github.account.application.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrichedDomainEvent<T extends DomainEvent> {
    private String aggregateType;
    private String aggregateId;
    private T domainEvent;

}
