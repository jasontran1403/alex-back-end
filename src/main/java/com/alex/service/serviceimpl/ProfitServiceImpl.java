package com.alex.service.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alex.service.ProfitService;
import com.alex.user.Profit;
import com.alex.user.ProfitRepository;

@Service
public class ProfitServiceImpl implements ProfitService {
	@Autowired
	ProfitRepository proRepo;

	@Override
	public List<Profit> findByAmountAndTimeAndExness(double amount, long time, String exness) {
		// TODO Auto-generated method stub
		return proRepo.findByAmountAndTimeAndExness(amount, time, exness);
	}

}
