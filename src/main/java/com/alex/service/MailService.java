package com.alex.service;

import com.alex.dto.EmailDto;

import jakarta.mail.MessagingException;

public interface MailService {
	void send(EmailDto mail) throws MessagingException;
}
