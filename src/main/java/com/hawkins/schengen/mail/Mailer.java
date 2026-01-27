package com.hawkins.schengen.mail;

public interface Mailer {
    void send(String to, String subject, String body);
}
