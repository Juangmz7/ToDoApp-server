package com.juangomez.todoapp.service;

import org.springframework.stereotype.Service;

@Service
public interface MailService {

    // Sends an email with a body template for forgot-password
    void sendForgotPasswordMail(String token, String email);

}
