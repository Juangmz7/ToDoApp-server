package com.juangomez.todoapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfiguration {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("smtp.gmail.com");
        sender.setPort(587);
        sender.setUsername("juanjuangmo@gmail.com");
        sender.setPassword("zygbwprfkfeyqbna"); // Mail password

        Properties props = sender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");            // The application must authenticate in smtp
        props.put("mail.smtp.starttls.enable", "true"); // Communication encryption protocol

        return sender;
    }
}
