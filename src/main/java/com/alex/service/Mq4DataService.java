package com.alex.service;

import java.util.List;

import com.alex.dto.RealtimeDataDto;
import com.alex.dto.RealtimeDataProjection;
import com.alex.dto.RealtimeDto;
import com.alex.dto.UpcomingCandleDto;

public interface Mq4DataService {
	void saveData(RealtimeDto realtimeDto);
	
	List<RealtimeDataProjection> getRealtimeData();
	String getRealtimeCandle(String exnessId, String currencyName);
	String getUpcomingCandle(String exnessId, String currencyName);
	RealtimeDataDto getRealtimeDataByExnessId(String exnessId);
	void saveUpcomingCandle(UpcomingCandleDto data);
	long getLatestTransaction();
}
