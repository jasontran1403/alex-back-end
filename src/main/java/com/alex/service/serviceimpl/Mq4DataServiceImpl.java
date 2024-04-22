package com.alex.service.serviceimpl;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alex.dto.RealtimeDto;
import com.alex.dto.UpcomingCandleDto;
import com.alex.service.Mq4DataService;
import com.alex.user.Mq4Data;
import com.alex.user.Mq4DataRepository;

@Service
public class Mq4DataServiceImpl implements Mq4DataService {
	@Autowired
	Mq4DataRepository mq4Repo;

	@Override
	public void saveData(RealtimeDto realtimeDto) {
		// TODO Auto-generated method stub
		Optional<Mq4Data> data = mq4Repo.findExistedData(realtimeDto.getExnessId(), realtimeDto.getCurrencyName());

		Date currentDateTime = new Date();
		// Lấy ngày hiện tại
		TimeZone timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTime(currentDateTime);
		// Lấy timestamp sau khi đặt thời gian
		long time = calendar.getTimeInMillis() / 1000;

		if (data.isPresent()) {
			data.get().setTotalEquity(realtimeDto.getTotalEquity());
			data.get().setCurrencyEquity(realtimeDto.getCurrencyEquity());
			data.get().setCurrentBalance(realtimeDto.getCurrentBalance());
			data.get().setCurrentCandle(realtimeDto.getCurrentCandle());
			data.get().setLot(realtimeDto.getLot());
			data.get().setLastestUpdated(time);
			data.get().setInitLot(realtimeDto.getInitLot());
			data.get().setInitSpread(realtimeDto.getInitSpread());
			data.get().setMagic1(realtimeDto.getMagic1());
			data.get().setMagic2(realtimeDto.getMagic2());
			
			boolean isActivated = realtimeDto.getIsActived() == 1 ? true : false;
			boolean isRunning = realtimeDto.getIsRunning() == 1 ? true : false;
			data.get().setActived(isActivated);
			data.get().setRunning(isRunning);
			mq4Repo.save(data.get());
		} else {
			Mq4Data newData = new Mq4Data();
			newData.setExnessId(realtimeDto.getExnessId());
			newData.setCurrencyName(realtimeDto.getCurrencyName());
			newData.setTotalEquity(realtimeDto.getTotalEquity());
			newData.setCurrencyEquity(realtimeDto.getCurrencyEquity());
			newData.setCurrentBalance(realtimeDto.getCurrentBalance());
			newData.setCurrentCandle(realtimeDto.getCurrentCandle());
			newData.setLot(realtimeDto.getLot());
			newData.setLastestUpdated(time);
			newData.setInitLot(realtimeDto.getInitLot());
			newData.setInitSpread(realtimeDto.getInitSpread());
			newData.setMagic1(realtimeDto.getMagic1());
			newData.setMagic2(realtimeDto.getMagic2());
			boolean isActivated = realtimeDto.getIsActived() == 1 ? true : false;
			boolean isRunning = realtimeDto.getIsRunning() == 1 ? true : false;
			newData.setActived(isActivated);
			newData.setRunning(isRunning);
			mq4Repo.save(newData);
		}
	}



	@Override
	public String getRealtimeCandle(String exnessId, String currencyName) {
		// TODO Auto-generated method stub
		Optional<Mq4Data> data = mq4Repo.findExistedData(exnessId, currencyName);
		if (data.isPresent()) {
			String result = "";
			int number = Integer.parseInt(data.get().getCurrentCandle());
			if (number == 30) {
				result = "M30";
			} else if (number == 60) {
				result = "H1";
			} else if (number == 240) {
				result = "H4";
			}else if (number == 1440) {
				result = "D1";
			}
			
			return result;
		}
		throw new RuntimeException("Không có dữ liệu của cặp " + currencyName + " từ ExnessId#" + exnessId);
	}

	@Override
	public void saveUpcomingCandle(UpcomingCandleDto data) {
		// TODO Auto-generated method stub
		Optional<Mq4Data> result = mq4Repo.findExistedData(data.getExnessId(), data.getCurrencyName());
		if (result.isPresent()) {
			result.get().setUpcomingCandle(data.getUpcomingCandle());
			mq4Repo.save(result.get());
		} else {
			throw new RuntimeException("Không có dữ liệu của cặp " + data.getCurrencyName() + " từ ExnessId#" + data.getExnessId());
		}
	}

	@Override
	public String getUpcomingCandle(String exnessId, String currencyName) {
		// TODO Auto-generated method stub
		Optional<Mq4Data> result = mq4Repo.findExistedData(exnessId, currencyName);
		if (result.isPresent()) {
			return result.get().getUpcomingCandle();
		}
		throw new RuntimeException("Không có dữ liệu của cặp " + currencyName + " từ ExnessId#" + exnessId);
	}

	@Override
	public long getLatestTransaction() {
		// TODO Auto-generated method stub
		return mq4Repo.getLatestRealtimeData();
	}

}
