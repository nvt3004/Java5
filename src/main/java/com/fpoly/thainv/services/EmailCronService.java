package com.fpoly.thainv.services;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailCronService {
	@Autowired
	JavaMailSender mailSender;
	public boolean sendMail() {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            
            mimeMessageHelper.setTo("ngothai3004@gmail.com");
            mimeMessageHelper.setSubject("Thống kê doanh thu tháng vừa qua");
            mimeMessageHelper.setText("<h1>Kính gửi sếp</h1>", true);
            
            try {
                // Đính kèm tệp
            File file1 = new File("C:\\Users\\ASUS\\Downloads\\Dashboard.pdf");
            mimeMessageHelper.addAttachment("Thống kê tháng trước.pdf", file1);
    
            File file2 = new File("C:\\Users\\ASUS\\Downloads\\Dashboard.csv");
            mimeMessageHelper.addAttachment("Báo cáo tháng trước.csv", file2);
            mailSender.send(mimeMessage);
            
            // Sau khi gửi email thành công, xóa các tệp đính kèm
            file1.delete();
            file2.delete();
            } catch (Exception e) {
                System.out.println("Vui lòng xuất file");
            }
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
}
