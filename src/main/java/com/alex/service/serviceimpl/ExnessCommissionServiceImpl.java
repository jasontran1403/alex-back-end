package com.alex.service.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alex.service.ExnessCommissionService;
import com.alex.user.ExnessCommission;
import com.alex.user.ExnessCommissionRepository;

@Service
public class ExnessCommissionServiceImpl implements ExnessCommissionService{
	@Autowired
	ExnessCommissionRepository exCommissionRepo;

	@Override
	public void saveExnessCommission(ExnessCommission ex) {
		// TODO Auto-generated method stub
		exCommissionRepo.save(ex);
	}

	@Override
	public List<ExnessCommission> getExnessCommissionByTimeRange(long fromDate, long toDate) {
		// TODO Auto-generated method stub
		return exCommissionRepo.getByTimeRange(fromDate, toDate);
	}

}
