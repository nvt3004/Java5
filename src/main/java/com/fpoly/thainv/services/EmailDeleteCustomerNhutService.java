package com.fpoly.thainv.services;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fpoly.thainv.entities.Users;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailDeleteCustomerNhutService {
	@Autowired
	private JavaMailSender mailSender;

	public void sendEmail(Users users) throws MessagingException, IOException {
		sendEmail(new Users[] { users });
	}

	public void sendEmail(Users[] users) throws MessagingException, IOException {
		String subject = "Account Deletion Notice";
		for (Users user : users) {
			String content = buildEmailContent(user);
			sendEmail(user.getEmail(), subject, content, null);
		}
	}

	public void sendEmail(List<Users> users) throws MessagingException, IOException {
		String subject = "Account Deletion Notice";
		for (Users user : users) {
			String content = buildEmailContent(user);
			sendEmail(user.getEmail(), subject, content, null);
		}
	}

	private void sendEmail(String toEmail, String subject, String content, MultipartFile[] attachments)
			throws MessagingException, IOException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

		helper.setTo(toEmail);
		helper.setSubject(subject);
		helper.setText(content, true);

		if (attachments != null) {
			for (MultipartFile file : attachments) {
				if (!file.isEmpty()) {
					InputStreamSource source = new ByteArrayResource(file.getBytes());
					helper.addAttachment(file.getOriginalFilename(), source);
				}
			}
		}
		mailSender.send(message);
	}

	private String buildEmailContent(Users user) {
		return "<!DOCTYPE html>" + "<html lang=\"en\">" + "<head>" + "    <meta charset=\"UTF-8\">"
				+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
				+ "    <title>Important: Account Deletion Notification</title>" + "    <style>" + "        body {"
				+ "            font-family: Arial, sans-serif;" + "            background-color: #f8f9fa;"
				+ "            padding: 20px;" + "            margin: 0;" + "        }" + "        .container {"
				+ "            width: 80%;" + "            margin: auto;" + "        }" + "        .card {"
				+ "            background-color: #fff;" + "            border-radius: 10px;"
				+ "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);" + "            margin-top: 50px;" + "        }"
				+ "        .card-header {" + "            background-color: #dc3545;" + "            color: #fff;"
				+ "            padding: 20px;" + "            border-top-left-radius: 10px;"
				+ "            border-top-right-radius: 10px;" + "        }" + "        .card-body {"
				+ "            padding: 20px;" + "        }" + "        .card-footer {"
				+ "            background-color: #f8f9fa;" + "            text-align: center;"
				+ "            padding: 20px;" + "            border-bottom-left-radius: 10px;"
				+ "            border-bottom-right-radius: 10px;" + "        }" + "        .footer-text {"
				+ "            color: #6c757d;" + "        }" + "        p {" + "            margin-bottom: 10px;"
				+ "        }" + "    </style>" + "</head>" + "<body>" + "    <div class=\"container\">"
				+ "        <div class=\"card\">" + "            <div class=\"card-header\">"
				+ "                <h4>Account Deletion Notice</h4>" + "            </div>"
				+ "            <div class=\"card-body\">" + "                <p>Dear " + user.getFirstName() + " "
				+ user.getLastName() + ",</p>"
				+ "                <p>We regret to inform you that your account has been deleted due to policy violations. Please contact our support team for further assistance.</p>"
				+ "                <p>If you believe this was a mistake, or if you have any questions, please do not hesitate to contact our support team at <a href=\"mailto:nhutnmpc06411@fpt.edu.vn\">nhutnmpc06411@fpt.edu.vn</a>.</p>"
				+ "                <p>Thank you for your understanding.</p>" + "                <p>Best regards,</p>"
				+ "                <p><strong>Steps to Future</strong></p>" + "            </div>"
				+ "            <div class=\"card-footer\">"
				+ "                <small class=\"footer-text\">&copy; 2024 Steps to Future. All rights reserved.</small>"
				+ "            </div>" + "        </div>" + "    </div>" + "</body>" + "</html>";
	}

}
