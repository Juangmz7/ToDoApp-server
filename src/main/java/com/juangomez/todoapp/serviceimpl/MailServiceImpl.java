package com.juangomez.todoapp.serviceimpl;

import com.juangomez.todoapp.config.exception.user.InvalidEmailException;
import com.juangomez.todoapp.service.MailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    public static String generateNumericCode(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10)); // Números del 0 al 9
        }
        return code.toString();
    }


    @Override
    public void sendForgotPasswordMail(String token, String email) {
        if (token.isEmpty() || token.trim().isEmpty()) {
            return;
        }
        if (email.isEmpty() || email.trim().isEmpty()) {
            return;
        }

        String mailBody = """
            <table style="width: 100%; font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
              <tr>
                <td>
                  <table style="max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                    <tr>
                      <td style="background-color: #4A90E2; padding: 20px; color: white; text-align: center;">
                        <h1 style="margin: 0; font-size: 24px;">Your Verification Code</h1>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding: 30px;">
                        <p style="font-size: 16px; color: #333333;">Hi there!</p>
                        <p style="font-size: 16px; color: #333333;">Use the code below to reset your password. This code is valid for one use only:</p>
                       \s
                        <p style="font-size: 32px; font-weight: bold; color: #4A90E2; text-align: center; margin: 40px 0;">
                          %s
                        </p>
           \s
                        <p style="font-size: 14px; color: #333333; text-align: center;">
                          If you didn’t request this code, please contact us immediately at <strong>666 66 66 66</strong>.
                        </p>
           \s
                        <p style="font-size: 16px; color: #333333;">Best regards,<br>The Support Team</p>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
           \s""".replaceFirst("%s", token);


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
