package br.com.bytebank.notification.application.usecase;

import br.com.bytebank.notification.infrastructure.messaging.event.FraudNotificationEvent;

public interface NotificationSenderUseCase {

    void execute(FraudNotificationEvent event);
}
