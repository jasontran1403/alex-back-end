package com.alex.service;

import com.alex.dto.DailyDataRequest;
import com.alex.user.DailyData;

public interface DailyDataService {
	DailyData findByExnessId(String exnessId);
	void setDailyDataStatus(DailyDataRequest request);
}
