package br.com.bytebank.notification.infrastructure.messaging.kafka;

import br.com.bytebank.notification.application.usecase.NotificationSenderUseCase;
import br.com.bytebank.notification.infrastructure.messaging.event.FraudNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class FraudNotificationConsumer {

    private final NotificationSenderUseCase notificationSenderUseCase;

    @KafkaListener(topics = "fraud.notification", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(@Payload FraudNotificationEvent event,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                        @Header(KafkaHeaders.OFFSET) long offset,
                        Acknowledgment ack){

        log.info("Event received from Kafka. Partition={} offset={} transactionId={}", partition, offset, event.transactionId());

        try {
            notificationSenderUseCase.execute(event);
            ack.acknowledge();
            log.info("Notification sent after Kafka event. transactionId={}", event.transactionId());
        } catch (Exception e) {
            log.error("Failed to send notification. transactionId={} error={}",
                    event.transactionId(), e.getMessage(), e);
            ack.acknowledge();
        }
    }

}
