package com.github.account.application.port.out;

public interface SendMoneyPublishPort {
    void publishSendMoneyCreatedEvent(String message);
}
