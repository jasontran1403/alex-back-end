package com.alex.service;

import com.alex.dto.RealtimeDto;
import com.alex.dto.UpcomingCandleDto;

public interface Mq4DataService {
	void saveData(RealtimeDto realtimeDto);
	
	String getRealtimeCandle(String exnessId, String currencyName);
	String getUpcomingCandle(String exnessId, String currencyName);
	void saveUpcomingCandle(UpcomingCandleDto data);
	long getLatestTransaction();
}
