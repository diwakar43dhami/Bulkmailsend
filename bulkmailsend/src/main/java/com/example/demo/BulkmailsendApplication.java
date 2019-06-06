package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@SpringBootApplication
public class BulkmailsendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BulkmailsendApplication.class, args);
	}

	@Value("${spring.mail.username}")
	String username;
	@Value("${spring.mail.password}")
	String password;
	@Value("${spring.mail.host}")
	String host;
	@Value("${spring.mail.port}")
	String port;
	@Value("${spring.mail.properties.mail.smtp.auth}")
	String smtpAuth;
	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	String sslEnable;





	@Bean
	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(host);
		mailSender.setPort(Integer.parseInt(port));
		mailSender.setUsername(username);
		mailSender.setPassword(password);
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.auth", smtpAuth);
		properties.put("mail.smtp.ssl.enable", sslEnable);
		properties.put("mail.smtp.starttls.enable", true);
		mailSender.setJavaMailProperties(properties);
		return mailSender;
	}

}
