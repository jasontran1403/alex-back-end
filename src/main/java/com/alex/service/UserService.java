package com.alex.service;

import com.alex.dto.InfoResponse;
import com.alex.user.Balance;
import com.alex.user.Commission;
import com.alex.user.Exness;
import com.alex.user.Profit;
import com.alex.user.Transaction;

public interface UserService {
	InfoResponse getInfoByExnessId(String exnessId, long from, long to);
	InfoResponse getAllInfoByEmail(String email, long from, long to);
	Profit saveProfit(String exnessId, double amount, long time);
	Commission saveCommission(String exnessId, double amount, long time);
	Balance saveBalance(String exnessId, double amount, long time);
	Transaction saveTransaction(String exnessId, double amount, long time);
	Exness updateBalanceExness(String exness, double amount);
	void updateTotalProfit(String exnessId, double amount);
}
