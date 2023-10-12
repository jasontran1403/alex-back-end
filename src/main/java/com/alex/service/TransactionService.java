package com.alex.service;

import java.util.List;

import com.alex.user.Transaction;

public interface TransactionService {
	List<Transaction> findTransactionByEmail(String email);

}
