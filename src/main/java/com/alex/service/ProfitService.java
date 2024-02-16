package com.alex.service;

import java.util.List;

import com.alex.user.Profit;

public interface ProfitService {

	List<Profit> findByAmountAndTimeAndExness(double amount, long time, String exness);
	List<Profit> findAmountOfProfitsByTime(long time);
	double sumTotalProfit(String exnessId);
}
