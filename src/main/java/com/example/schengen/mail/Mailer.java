package com.example.schengen.mail;

public interface Mailer {
    void send(String to, String subject, String body);
}
