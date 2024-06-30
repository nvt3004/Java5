package com.fpoly.thainv.config;

import java.time.LocalDate;
import java.time.YearMonth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.fpoly.thainv.services.EmailCronService;
import com.fpoly.thainv.services.EmailService;


@Configuration
@EnableScheduling
public class CronJobsConfig {

    private static final Logger logger = LoggerFactory.getLogger(CronJobsConfig.class);
    @Autowired
    private EmailCronService emailService;

    @Scheduled(cron = "59 59 23 * * ?")
    public void run() {

        System.out.println("Cron đang chạy");

        LocalDate today = LocalDate.now();
        YearMonth yearMonth = YearMonth.of(today.getYear(), today.getMonth());
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        if (today.equals(lastDayOfMonth)) {
            logger.info("Cron job started on the last day of the month");

            try {
                boolean send = emailService.sendMail();
                if (send) {
                    logger.info("Email sent successfully");
                } else {
                    logger.error("Failed to send email");
                }
            } catch (Exception e) {
                logger.error("Exception occurred while sending email", e);
            }
        } else {
            logger.info("Today is not the last day of the month. Cron job will not run.");
        }
    }
}
