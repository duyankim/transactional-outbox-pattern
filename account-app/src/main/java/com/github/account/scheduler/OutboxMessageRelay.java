package com.github.account.scheduler;

import com.github.account.adapter.out.persistence.outbox.OutboxJpaEntity;
import com.github.account.adapter.out.persistence.outbox.OutboxJpaRepository;
import com.github.account.application.port.out.SendMoneyPublishPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Transactional
@RequiredArgsConstructor
public class OutboxMessageRelay {

    private static Logger logger = LoggerFactory.getLogger(OutboxMessageRelay.class);
    private final OutboxJpaRepository outboxJpaRepository;
    private final SendMoneyPublishPort sendMoneyPublishPort;

    @Scheduled(fixedDelay = 5000)
    public void publishOutboxMessages() {
        logger.info("Publishing outbox messages");
        List<OutboxJpaEntity> entities = outboxJpaRepository.findAllByOrderByIdAsc(Pageable.ofSize(10)).toList();
        for (OutboxJpaEntity entity : entities) {
            sendMoneyPublishPort.publishSendMoneyCreatedEvent(entity.getAggregateId());
        }
        outboxJpaRepository.deleteAllInBatch();
    }
}
