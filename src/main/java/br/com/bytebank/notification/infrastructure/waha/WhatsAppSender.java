package br.com.bytebank.notification.infrastructure.waha;

import br.com.bytebank.notification.domain.WhatsAppContract;
import br.com.bytebank.notification.infrastructure.dto.WahaMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
@RequiredArgsConstructor
public class WhatsAppSender implements WhatsAppContract {

    private final RestClient restClient;

    @Override
    public void sendMessage(String phone, String message) {
        log.info("Sending WhatsApp message");
        String chatId = (phone.startsWith("55") ? phone : "55" + phone) + "@c.us";

       restClient.post()
               .uri("/api/sendText")
               .contentType(MediaType.APPLICATION_JSON)
               .body(new WahaMessageRequest(chatId, message, "default"))
               .retrieve()
               .toBodilessEntity();

        log.info("WhatsApp message sent successfully to chatId={}", chatId);
    }
}
