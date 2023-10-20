package com.alex.service;

import java.util.List;

import com.alex.user.Transaction;

public interface TransactionService {
	List<Transaction> findTransactionByEmail(String email);
	Transaction saveTransaction(Transaction transaction);
	List<Transaction> findByAmountAndTimeAndExness(double amount, long time, String exness);
}
