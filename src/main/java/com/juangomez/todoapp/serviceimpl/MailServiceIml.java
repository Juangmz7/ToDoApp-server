package com.juangomez.todoapp.serviceimpl;

import com.juangomez.todoapp.config.exception.user.InvalidEmailException;
import com.juangomez.todoapp.service.MailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailServiceIml implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendForgotPasswordMail(String token, String email, String link) {
        if (token.isEmpty() || token.trim().isEmpty()) {
            return;
        }
        if (email.isEmpty() || email.trim().isEmpty()) {
            return;
        }

        String mailBody = """
                <p>Hi!</p>
                <p>Click the link below to change your password:</p>
                <p><a href="%s">Reset your password</a></p>
                <p>If it wasn't you, please contact us at 666 66 66 66</p>
                <p>Cheers!</p>
        """.formatted(link);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;

        try {
            helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Reset your password");
            helper.setText(mailBody, true);
        } catch (Exception e) {
            throw new InvalidEmailException("Wrong email");
        }

        System.out.println(mailBody);

        mailSender.send(message);
    }
}
