package br.com.bytebank.notification.infrastructure.messaging.event;

import java.util.UUID;

public record FraudNotificationEvent(
        UUID transactionId,

        String name,

        String phone,

        String email
) {
}
