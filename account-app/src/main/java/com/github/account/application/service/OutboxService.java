package com.github.account.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.account.adapter.out.persistence.outbox.OutboxJpaEntity;
import com.github.account.adapter.out.persistence.outbox.OutboxJpaRepository;
import com.github.account.application.event.DomainEvent;
import com.github.account.application.event.EnrichedDomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxJpaRepository outboxJpaRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @EventListener
    public void handleSendMoneyDomainEvent(EnrichedDomainEvent<? extends DomainEvent> enrichedDomainEvent) {
        outboxJpaRepository.save(
                OutboxJpaEntity.builder()
                        .aggregateType(enrichedDomainEvent.getAggregateType())
                        .aggregateId(enrichedDomainEvent.getAggregateId())
                        .eventType("money-transfer-created")
                        .payload(objectMapper.convertValue(enrichedDomainEvent.getDomainEvent(), JsonNode.class))
                        .build()
        );
    }
}
