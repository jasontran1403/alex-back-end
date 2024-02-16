package com.alex.service.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alex.service.ExnessService;
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
	private final ExnessService exService;

	@Override
	public List<Transaction> findTransactionByEmail(String email) {
		// TODO Auto-generated method stub
		
		if (email.equalsIgnoreCase("all")) {
			List<Exness> listExness = new ArrayList<>();
			List<User> users = userRepo.getUsersByBranchName("PixiuGroup");
			for (User userItem : users) {
				List<Exness> listExnessNew = userItem.getExnessList();
				for (Exness exness : listExnessNew) {
					listExness.add(exness);
				}
			}
			
			List<Transaction> listResult = new ArrayList<>();
			for (Exness exness : listExness) {
				List<Transaction> listTransactionByExness = transactionRepo.findTransactionByExnessId(exness.getExness());
				if (listTransactionByExness.size() > 0) {
					listResult.addAll(listTransactionByExness);
				}
			}
			
			listResult.sort((transaction1, transaction2) -> Long.compare(transaction2.getTime(), transaction1.getTime()));

			
			return listResult;
		} else {
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

	@Override
	public double getTotalDepositByExnessId(String exnessId) {
		// TODO Auto-generated method stub
		return transactionRepo.getTotalDepositByExnessId(exnessId);
	}

	@Override
	public double getTotalDepositFromPixiu() {
		// TODO Auto-generated method stub
		List<Exness> listExnessFromPixiu = exService.findAllByBranchName("PixiuGroup");
		
		double result = 0.0;
		
		for (Exness exness : listExnessFromPixiu) {
			result += transactionRepo.getTotalDepositByExnessId(exness.getExness());
		}
		
		return result;
	}

}
