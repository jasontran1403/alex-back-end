package com.alex.service;

import java.util.List;

import com.alex.user.AdminPixiu;
import com.alex.user.Commission;
import com.alex.user.ManagerPixiu;

public interface CommissionService {
	Commission saveCommission(Commission commission); 
	double getTotalCommission(String branchName);
	List<Commission> getAllCommissionByBranchName(String branchName);
	List<AdminPixiu> getAllCommissionByBranchNameAdmin(String branchName, String rootEmail);
	List<ManagerPixiu> getAllCommissionByBranchNameManager(String branchName);
	void deleteAllCommission();
}
