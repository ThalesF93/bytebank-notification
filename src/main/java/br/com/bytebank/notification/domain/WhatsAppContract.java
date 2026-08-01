package br.com.bytebank.notification.domain;

public interface WhatsAppContract {
    void sendMessage(String phone, String message);
}
