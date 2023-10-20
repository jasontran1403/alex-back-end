package com.alex.service.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alex.service.TransactionService;
import com.alex.user.Exness;
import com.alex.user.Transaction;
import com.alex.user.TransactionRepository;
import com.alex.user.User;
import com.alex.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService{
	private final TransactionRepository transactionRepo;
	private final UserRepository userRepo;

	@Override
	public List<Transaction> findTransactionByEmail(String email) {
		// TODO Auto-generated method stub
		User user = userRepo.findByEmail(email).get();
		List<Exness> listExness = user.getExnessList();
		List<Transaction> listResult = new ArrayList<>();
		for (Exness exness : listExness) {
			List<Transaction> listTransactionByExness = transactionRepo.findTransactionByExnessId(exness.getExness());
			if (listTransactionByExness.size() > 0) {
				listResult.addAll(listTransactionByExness);
			}
		}
		
		listResult.sort((transaction1, transaction2) -> Long.compare(transaction2.getTime(), transaction1.getTime()));

		
		return listResult;
	}

	@Override
	public Transaction saveTransaction(Transaction transaction) {
		// TODO Auto-generated method stub
		return transactionRepo.save(transaction);
	}

	@Override
	public List<Transaction> findByAmountAndTimeAndExness(double amount, long time, String exness) {
		// TODO Auto-generated method stub
		return transactionRepo.findTransactionByAmountAndTimeAndExness(amount, time, exness);
	}

}
