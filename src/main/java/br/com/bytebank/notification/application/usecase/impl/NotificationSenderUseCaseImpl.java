package br.com.bytebank.notification.application.usecase.impl;

import br.com.bytebank.notification.application.usecase.NotificationSenderUseCase;
import br.com.bytebank.notification.domain.WhatsAppContract;
import br.com.bytebank.notification.infrastructure.messaging.event.FraudNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSenderUseCaseImpl implements NotificationSenderUseCase {

    private final JavaMailSender mailSender;
    private final WhatsAppContract whatsAppContract;

    @Override
    public void execute(FraudNotificationEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.email());
        message.setSubject("Fraud Alert Notification");
        message.setText(String.format(
                "Hello %s,\n\n we've received a suspicious transaction in your behalf, we suggest to check your WhatsApp to aprove or block the transaction\nTransactionID: %s\n\nByteBank",
                event.name(), event.transactionId()
        ));
        mailSender.send(message);

        whatsAppContract.sendMessage(event.phone(), "Our System received a suspicious transaction, please answer: \n Y to continue or N to block it");

    }
}
