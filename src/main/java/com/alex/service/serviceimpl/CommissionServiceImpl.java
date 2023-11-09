package com.alex.service.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alex.service.CommissionService;
import com.alex.user.Commission;
import com.alex.user.CommissionRepository;
import com.alex.user.Exness;
import com.alex.user.ExnessRepository;

@Service
public class CommissionServiceImpl implements CommissionService{
	@Autowired
	CommissionRepository commissRepo;
	
	@Autowired
	ExnessRepository exRepo;

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
			Optional<Exness> exness = exRepo.findByExness(commission.getExnessId());
			if (exness.isPresent()) {
				if (exness.get().getUser().getBranchName().equals("ALEX")) {
					totalCommissions += commission.getAmount();
				}
			}
		}
		
		return totalCommissions;
	}

}
