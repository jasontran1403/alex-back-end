package com.alex.service;

import com.alex.dto.PreviousMonthResponse;
import com.alex.user.User;

public interface ExnessService {
	User findUserByExness(String exnessId);
	PreviousMonthResponse findByEmail(String email);
	PreviousMonthResponse findByExness(String exness);
	void updateTotalProfit(String exnessId, double amount);
}
