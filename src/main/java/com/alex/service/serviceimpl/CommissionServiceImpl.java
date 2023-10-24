package com.alex.service.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alex.service.CommissionService;
import com.alex.user.Commission;
import com.alex.user.CommissionRepository;

@Service
public class CommissionServiceImpl implements CommissionService{
	@Autowired
	CommissionRepository commissRepo;

	@Override
	public Commission saveCommission(Commission commission) {
		// TODO Auto-generated method stub
		return commissRepo.save(commission);
	}

	@Override
	public double getTotalCommission() {
		// TODO Auto-generated method stub
		double totalCommissions = 0.0;
		List<Commission> commissions = commissRepo.findAll();
		for (Commission commission : commissions) {
			totalCommissions += commission.getAmount();
		}
		
		return totalCommissions;
	}

}
