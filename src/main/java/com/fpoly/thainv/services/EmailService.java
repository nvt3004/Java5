package com.fpoly.thainv.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String email, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("nhanntpc06420@fpt.edu.vn");
            helper.setTo(email);
            helper.setSubject("Xác nhận địa chỉ email của bạn");
            String emailContent = "<html>" +
                    "<body>" +
                    "<div style='font-family: Arial, sans-serif; text-align: center;'>" +
                    "<h2 style='color: #333;'>Xác nhận Email</h2>" +
                    "<p>Vui lòng sử dụng mã dưới đây để hoàn tất xác minh:</p>" +
                    "<div style='font-size: 18px; margin: 10px auto; padding: 10px; background-color: #e9e9e9; display: inline-block; border-radius: 5px;'>" +
                    otp + "</div>" +
                    "<p>Nếu bạn không yêu cầu mã này, bạn có thể bỏ qua email này.</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
            helper.setText(emailContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendNewPasswordEmail(String email, String newPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("nhanntpc06420@fpt.edu.vn");
            helper.setTo(email);
            helper.setSubject("Mật khẩu mới của bạn");
            String emailContent = "<html>" +
                    "<body>" +
                    "<div style='font-family: Arial, sans-serif; text-align: center;'>" +
                    "<h2 style='color: #333;'>Mật khẩu mới của bạn</h2>" +
                    "<p>Mật khẩu mới của bạn là:</p>" +
                    "<div style='font-size: 18px; margin: 10px auto; padding: 10px; background-color: #e9e9e9; display: inline-block; border-radius: 5px;'>" +
                    newPassword + "</div>" +
                    "<p>Hãy đăng nhập và thay đổi mật khẩu này ngay lập tức vì lý do bảo mật.</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
            helper.setText(emailContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
