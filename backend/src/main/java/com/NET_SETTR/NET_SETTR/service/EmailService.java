package com.NET_SETTR.NET_SETTR.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);

        mailSender.send(msg);
    }


    public void sendOtpEmail(String to, String otp) {
        String subject = "Your NET-SETTR OTP Code";
        String text =
                "Your OTP is: " + otp + "\n\n" +
                        "This OTP is valid for 5 minutes.\n" +
                        "Do not share this code with anyone.\n\n" +
                        "– NET-SETTR Team";

        sendEmail(to, subject, text);
    }
}
