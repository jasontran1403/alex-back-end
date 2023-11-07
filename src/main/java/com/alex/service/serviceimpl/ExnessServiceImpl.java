package com.alex.service.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.alex.dto.PreviousMonthResponse;
import com.alex.service.ExnessService;
import com.alex.user.Exness;
import com.alex.user.ExnessRepository;
import com.alex.user.Transaction;
import com.alex.user.TransactionRepository;
import com.alex.user.User;
import com.alex.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExnessServiceImpl implements ExnessService {
	private final ExnessRepository exRepo;
	private final UserRepository userRepo;
	private final TransactionRepository tranRepo;

	@Override
	public User findUserByExness(String exnessId) {
		// TODO Auto-generated method stub
		return exRepo.findByExness(exnessId).get().getUser();
	}

	@Override
	public PreviousMonthResponse findByEmail(String email) {
		// TODO Auto-generated method stub
		User user = userRepo.findByEmail(email).get();
		PreviousMonthResponse result = new PreviousMonthResponse();
		double withdrawAmounts = 0.0, depositAmounts = 0.0, profit = 0.0;
		
		for (Exness exness : user.getExnessList()) {
			result.setBalance(result.getBalance() + exness.getPrevBalance());
			profit += exness.getTotalProfit();
			List<Transaction> transactions = tranRepo.findTransactionByExnessId(exness.getExness());
			for (Transaction tran : transactions) {
				if (tran.getType().equals("Deposit")) {
					depositAmounts += tran.getAmount();
				} else if (tran.getType().equals("Withdraw")) {
					withdrawAmounts += tran.getAmount();
				}
			}
		}
		result.setProfit(profit);
		result.setDeposit(depositAmounts);
		result.setWithdraw(withdrawAmounts);
		return result;
	}

	@Override
	public PreviousMonthResponse findByExness(String exness) {
		// TODO Auto-generated method stub
		PreviousMonthResponse result = new PreviousMonthResponse();
		Exness item = exRepo.findByExness(exness).get();
		double withdrawAmounts = 0.0, depositAmounts = 0.0, profit = 0.0;
		profit += item.getTotalProfit();
		List<Transaction> transactions = tranRepo.findTransactionByExnessId(item.getExness());
		for (Transaction tran : transactions) {
			if (tran.getType().equals("Deposit")) {
				depositAmounts += tran.getAmount();
			} else if (tran.getType().equals("Withdraw")) {
				withdrawAmounts += tran.getAmount();
			}
		}
		result.setProfit(profit);
		result.setDeposit(depositAmounts);
		result.setWithdraw(withdrawAmounts);
		result.setBalance(result.getBalance() + item.getPrevBalance());
		return result;
	}

	@Override
	public void updateTotalProfit(String exnessId, double amount) {
		// TODO Auto-generated method stub
		Exness exness = exRepo.findByExness(exnessId).get();
		exness.setTotalProfit(exness.getTotalProfit() + amount);
		exRepo.save(exness);
	}

	@Override
	public Optional<Exness> findByExnessId(String exnessId) {
		// TODO Auto-generated method stub
		Optional<Exness> exness = exRepo.findByExness(exnessId);
		return exness;
	}

	@Override
	public double getBalanceByEmail(String email) {
		// TODO Auto-generated method stub
		User user = userRepo.getByEmail(email);
		List<Exness> exnesses = exRepo.findByUser(user);
		double balance = 0;
		for (Exness item : exnesses) {
			balance += item.getBalance();
		}
		return balance;
	}

	@Override
	public double getProfitByEmail(String email) {
		// TODO Auto-generated method stub
		User user = userRepo.getByEmail(email);
		List<Exness> exnesses = exRepo.findByUser(user);
		double profit = 0;
		for (Exness item : exnesses) {
			profit += item.getTotalProfit();
		}
		return profit;
	}

}
