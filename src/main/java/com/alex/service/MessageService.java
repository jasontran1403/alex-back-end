package com.alex.service;

import java.util.List;

import com.alex.dto.MessageRequest;
import com.alex.user.Message;

public interface MessageService {
	Message saveMessage(MessageRequest message);
	List<Message> findMessagesByEmail(String email);
	void toggleMessageStatus(long id);
}
