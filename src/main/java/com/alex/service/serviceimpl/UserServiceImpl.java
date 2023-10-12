package com.alex.service.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alex.dto.InfoResponse;
import com.alex.exception.NotFoundException;
import com.alex.service.UserService;
import com.alex.user.Balance;
import com.alex.user.BalanceRepository;
import com.alex.user.Commission;
import com.alex.user.CommissionRepository;
import com.alex.user.Exness;
import com.alex.user.ExnessRepository;
import com.alex.user.Profit;
import com.alex.user.ProfitRepository;
import com.alex.user.Transaction;
import com.alex.user.TransactionRepository;
import com.alex.user.User;
import com.alex.user.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	UserRepository userRepo;

	@Autowired
	ProfitRepository proRepo;

	@Autowired
	CommissionRepository commissRepo;

	@Autowired
	ExnessRepository exRepo;

	@Autowired
	BalanceRepository balanceRepo;

	@Autowired
	TransactionRepository transRepo;

	@Override
	public InfoResponse getInfoByExnessId(String exnessId, long from, long to) {
		Optional<Exness> exness = exRepo.findByExness(exnessId);
		if (exness.isEmpty()) {
			throw new NotFoundException("This ExnessID " + exnessId + " is not existed!");
		}

		List<Profit> profits = new ArrayList<>();
		List<Commission> commissions = new ArrayList<>();

		if (from > 0 && to > 0) {
			profits = proRepo.getCommissionByExnessIdAndTime(exnessId, from, to);
			commissions = commissRepo.getCommissionByExnessIdAndTime(exnessId, from, to);
		} else {
			profits = proRepo.getCommissionByExnessId(exnessId);
			commissions = commissRepo.getCommissionByExnessId(exnessId);
		}
		InfoResponse result = new InfoResponse();
		result.setProfit(exness.get().getUser().getProfit());
		result.setCommission(exness.get().getUser().getCommission());
		result.setProfits(profits);
		result.setCommissions(commissions);
		List<Balance> balances = balanceRepo.findByUserByTime(exness.get().getUser().getId(), from, to);
		result.setBalances(balances);

		// TODO Auto-generated method stub
		return result;
	}

	@Override
	public Profit saveProfit(String exnessId, double amount, long time) {
		// TODO Auto-generated method stub
		Profit profit = new Profit();
		profit.setExnessId(exnessId);
		profit.setAmount(amount);
		profit.setTime(time);
		return proRepo.save(profit);
	}

	@Override
	public Commission saveCommission(String exnessId, double amount, long time) {
		// TODO Auto-generated method stub
		Commission commission = new Commission();
		commission.setExnessId(exnessId);
		commission.setAmount(amount);
		commission.setTime(time);
		return commissRepo.save(commission);
	}

	@Override
	public Balance saveBalance(String exnessId, double amount, long time) {
		// TODO Auto-generated method stub
		Balance balance = new Balance();
		Exness exness = exRepo.findByExness(exnessId).get();
		balance.setUser(exness.getUser());
		balance.setAmount(amount);
		balance.setTime(time);
		return balanceRepo.save(balance);
	}

	@Override
	public Transaction saveTransaction(String exnessId, double amount, long time) {
		// TODO Auto-generated method stub
		String type = "";
		if (amount > 0) {
			type = "Deposit";
		} else {
			type = "Withdraw";
		}
		Transaction transaction = new Transaction();
		transaction.setExnessId(exnessId);
		transaction.setAmount(amount);
		transaction.setType(type);
		transaction.setTime(time);
		return transRepo.save(transaction);
	}

	@Override
	public InfoResponse getAllInfoByEmail(String email, long from, long to) {
		// TODO Auto-generated method stub
		Optional<User> user = userRepo.findByEmail(email);
		if (user.isEmpty()) {
			throw new NotFoundException("This user with email " + email + " is not existed!");
		}

		List<Profit> profits = new ArrayList<>();
		List<Commission> commissions = new ArrayList<>();

		if (from > 0 && to > 0) {
			for (Exness exness : user.get().getExnessList()) {
				List<Profit> profitsFromCriteria = proRepo.getCommissionByExnessIdAndTime(exness.getExness(), from, to);
				if (profitsFromCriteria.size() > 0) {
					for (Profit profit : profitsFromCriteria) {
						profits.add(profit);
					}
				}

				List<Commission> commissionsFromCriteria = commissRepo
						.getCommissionByExnessIdAndTime(exness.getExness(), from, to);
				if (commissionsFromCriteria.size() > 0) {
					for (Commission commission : commissionsFromCriteria) {
						commissions.add(commission);
					}
				}
			}

		} else {
//			profits = proRepo.getCommissionByExnessId(exnessId);
//			commissions = commissRepo.getCommissionByExnessId(exnessId);
		}
		InfoResponse result = new InfoResponse();
		result.setProfit(user.get().getProfit());
		result.setCommission(user.get().getCommission());
		result.setProfits(profits);
		result.setCommissions(commissions);
		List<Balance> balances = balanceRepo.findByUser(user.get().getId());
		result.setBalances(balances);

		// TODO Auto-generated method stub
		return result;
	}

}
