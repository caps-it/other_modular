package com.zdmail.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @Author zd
 * @Date 2020/7/22 23:39
 **/
@Service
@Slf4j
public class MailService {

    /**
     * 邮件发送配置
     */
    private static JavaMailSenderImpl initJavaMailSender(
            String host,
            String username,
            String password) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        javaMailSender.setPort(465);
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);
        Properties properties = new Properties();
        properties.setProperty("mail.host", host);
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.port", "465");
        properties.setProperty("mail.smtp.socketFactory.port", "465");

        javaMailSender.setJavaMailProperties(properties);
        return javaMailSender;
    }

    /**
     * 简单文本邮件
     *
     * @param host     smtp服务器地址
     * @param username 邮件发送者
     * @param password 邮件发送者授权码
     * @param to       邮件接收者
     * @param subject  邮件主题
     * @param content  邮件内容
     */
    @Async
    public void sendSimpleMail(String host, String username, String password,
                               String to, String subject, String content) {
        //发送邮件需要的配置信息
        JavaMailSenderImpl mailSender = initJavaMailSender(host, username, password);
        //初始化发送邮件信息
        MimeMessage mailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(mailMessage, true, "utf-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content);
            helper.setFrom(username);
            //发送邮件
            mailSender.send(mailMessage);
        } catch (Exception e) {
            try {
                log.error("邮件发送发生异常");
                mailSender = initJavaMailSender(
                        "smtp.163.com", "13670377865@163.com", "zhangd1998");
                mailMessage = mailSender.createMimeMessage();
                helper = new MimeMessageHelper(mailMessage, "utf-8");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(content);
                helper.setFrom("13670377865@163.com");
                mailSender.send(mailMessage);
                log.info("邮件发送成功");
            } catch (Exception ex) {
                log.error("邮件发送发生异常:" + ex.getMessage(), ex);
            }
        }
    }
}