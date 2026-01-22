package com.example.schengen.mail;

import com.example.schengen.app.AppProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class SmtpMailer implements Mailer {
    private final JavaMailSender sender;
    private final AppProperties props;

    public SmtpMailer(JavaMailSender sender, AppProperties props) {
        this.sender = sender;
        this.props = props;
    }

    @Override
    public void send(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(props.getMail().getFrom());
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        sender.send(msg);
    }
}
