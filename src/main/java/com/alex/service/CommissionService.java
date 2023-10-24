package com.alex.service;

import com.alex.user.Commission;

public interface CommissionService {
	Commission saveCommission(Commission commission); 
	double getTotalCommission();
}
