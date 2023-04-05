package com.ensi.elearning.repositories;

public interface EmailSender {
    void send(String to, String email);
}
