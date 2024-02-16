package com.alex.service.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alex.service.BalanceService;
import com.alex.user.Balance;
import com.alex.user.BalanceRepository;

@Service
public class BalanceServiceImpl implements BalanceService{
	@Autowired
	BalanceRepository balanceRepo;

	@Override
	public List<Balance> findByAmountAndTimeAndExness(double amount, long time, String exness) {
		// TODO Auto-generated method stub
		return balanceRepo.findByAmountAndTimeAndExness(amount, time, exness);
	}

	@Override
	public List<Balance> findAmountOfBalanceByTime(long time) {
		// TODO Auto-generated method stub
		return balanceRepo.findAmountOfBalancesByTime(time);
	}

}
