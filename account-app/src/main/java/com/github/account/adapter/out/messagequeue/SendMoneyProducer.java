package com.github.account.adapter.out.messagequeue;

import com.github.account.application.port.out.SendMoneyPublishPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SendMoneyProducer implements SendMoneyPublishPort {

    private static final Logger logger = LoggerFactory.getLogger(SendMoneyProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void publishSendMoneyCreatedEvent(String accountId) {
        logger.info("SendMoney created event published: {}", accountId);
        kafkaTemplate.send("money-transfer-created", accountId);
    }
}
