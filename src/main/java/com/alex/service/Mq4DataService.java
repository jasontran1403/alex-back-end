package com.alex.service;

import com.alex.dto.OrderListDto;
import com.alex.dto.RealtimeDto;
import com.alex.dto.UpcomingCandleDto;

public interface Mq4DataService {
	void saveData(RealtimeDto realtimeDto);
	
	String getRealtimeCandle(String exnessId, String currencyName);
	String getUpcomingCandle(String exnessId, String currencyName);
	double getAccumulateProfitByType(String exnessId, String currencyName, int type);
	int getCurrentMagic(String exnessId, String currencyName, int type);
	String getOldMagics(String exnessId, String currencyName);
	void saveUpcomingCandle(UpcomingCandleDto data);
	long getLatestTransaction();
	void saveOrders(OrderListDto request);
}
