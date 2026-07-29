package br.com.bytebank.notification.infrastructure.dto;

public record WahaMessageRequest(
        String chatId,

        String message,

        String session
) {
}
