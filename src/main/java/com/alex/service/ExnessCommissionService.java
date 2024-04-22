package com.alex.service;

import java.util.List;

import com.alex.user.ExnessCommission;

public interface ExnessCommissionService {
	void saveExnessCommission(ExnessCommission ex);
	
	List<ExnessCommission> getExnessCommissionByTimeRange(long fromDate, long toDate);
}
