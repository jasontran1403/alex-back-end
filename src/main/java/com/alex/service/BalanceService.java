package com.alex.service;

import java.util.List;

import com.alex.user.Balance;

public interface BalanceService {
	List<Balance> findByAmountAndTimeAndExness(double amount, long time, String exness);
}
