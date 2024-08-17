package com.alex.service.serviceimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alex.dto.OrderDto;
import com.alex.dto.OrderListDto;
import com.alex.dto.RealtimeDto;
import com.alex.dto.UpcomingCandleDto;
import com.alex.service.Mq4DataService;
import com.alex.user.Mq4Data;
import com.alex.user.Mq4DataRepository;
import com.alex.user.OrderDetail;
import com.alex.user.OrderDetailRepository;

@Service
public class Mq4DataServiceImpl implements Mq4DataService {
	@Autowired
	Mq4DataRepository mq4Repo;
	@Autowired
	OrderDetailRepository orderDetailRepo;

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
			data.get().setLotBuy(realtimeDto.getLotBuy());
			data.get().setLotSell(realtimeDto.getLotSell());
			data.get().setLargestLotBuy(realtimeDto.getLargestLotBuy());
			data.get().setLargestLotSell(realtimeDto.getLargestLotSell());
			data.get().setLotBuyDefault(realtimeDto.getLotBuyDefault());
			data.get().setLotSellDefault(realtimeDto.getLotSellDefault());
			data.get().setVersion(realtimeDto.getVersion());
			data.get().setNewMagic(realtimeDto.getNewMagic());
			data.get().setHedgMagic(realtimeDto.getHedgMagic());
			
			if (realtimeDto.getCurrentMagicBuy() > 0) {
				data.get().setCurrentMagicBuy(realtimeDto.getCurrentMagicBuy());
			}
			if (realtimeDto.getCurrentMagicSell() > 0) {
				data.get().setCurrentMagicSell(realtimeDto.getCurrentMagicSell());
			}
			data.get().setOldMagics(realtimeDto.getOldMagics());
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
			newData.setLotBuy(realtimeDto.getLotBuy());
			newData.setLotSell(realtimeDto.getLotSell());
			newData.setLargestLotBuy(realtimeDto.getLargestLotBuy());
			newData.setLargestLotSell(realtimeDto.getLargestLotSell());
			newData.setLotBuyDefault(realtimeDto.getLotBuyDefault());
			newData.setLotSellDefault(realtimeDto.getLotSellDefault());
			newData.setVersion(realtimeDto.getVersion());
			newData.setNewMagic(realtimeDto.getNewMagic());
			newData.setHedgMagic(realtimeDto.getHedgMagic());
			newData.setCurrentMagicBuy(realtimeDto.getCurrentMagicBuy());
			newData.setCurrentMagicSell(realtimeDto.getCurrentMagicSell());
			newData.setOldMagics(realtimeDto.getOldMagics());
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



	@Override
	public double getAccumulateProfitByType(String exnessId, String currencyName, int type) {
		Optional<Mq4Data> resultData = mq4Repo.findExistedData(exnessId, currencyName);
		if (resultData.isPresent()) {
			if (type == 1) {
				// new magic
				return resultData.get().getNewMagic();
			} else if (type == 2) {
				// hedg magic
				return resultData.get().getHedgMagic();
			}
		}
		throw new RuntimeException("");
	}

	@Override
	public int getCurrentMagic(String exnessId, String currencyName, int type) {
		Optional<Mq4Data> resultData = mq4Repo.findExistedData(exnessId, currencyName);
		if (resultData.isPresent()) {
			if (type == 1) {
				// new magic
				return resultData.get().getCurrentMagicBuy();
			} else if (type == 2) {
				// hedg magic
				return resultData.get().getCurrentMagicSell();
			}
		} 
		return 0;
	}

	@Override
	public String getOldMagics(String exnessId, String currencyName) {
		Optional<Mq4Data> resultData = mq4Repo.findExistedData(exnessId, currencyName);
		if (resultData.isPresent()) {
			return resultData.get().getOldMagics();
		} else {
			return "";
		}
	}



	@Override
	public void saveOrders(OrderListDto request) {
		// TODO Auto-generated method stub
		long start = System.currentTimeMillis();
		StringBuilder operation = new StringBuilder();
		
		List<OrderDetail> orderDetails = new ArrayList<>();
		
//		System.out.println(request.getAccountName());
//		System.out.println(request.getAccountServer());
//		System.out.println(request.getSymbol());
		
		Optional<OrderDetail> lastestOrderDetail = orderDetailRepo.findLastestOrderByExnessIdAndSymbol(request.getExnessId(), request.getSymbol());
		boolean isReadyToSave = false;
		if (lastestOrderDetail.isPresent()) {
			long current = System.currentTimeMillis()/1000;
			long lastOrderTime = lastestOrderDetail.get().getServerTime();
			long diff = current - lastOrderTime;
			if (current > lastOrderTime && diff >= 60*30) {
				isReadyToSave = true;
				operation.append("add");
			} else {
				isReadyToSave = false;
				operation.append("check");
			}
		} else {
			isReadyToSave = true;
			operation.append("add");
		} 
		
		if (isReadyToSave) {
			for (OrderDto item : request.getListOrders()) {
				OrderDetail order = new OrderDetail();
				order.setServerTime(convertTimeStringToUnix(request.getTime()));
				order.setExnessId(request.getExnessId());
				order.setTicket(item.getTicket());
				order.setTime(item.getTime());
				order.setType(item.getType());
				order.setLot(item.getLot());
				order.setSymbol(item.getSymbol());
				order.setOpenPrice(item.getOpenPrice());
				order.setCurrentPrice(item.getCurrentPrice());
				order.setCommission(item.getCommission());
				order.setSwap(item.getSwap());
				order.setProfit(item.getProfit());
				orderDetails.add(order);
			}
		}
		
		if (orderDetails.size() > 0) orderDetailRepo.saveAll(orderDetails);
		
		long end = System.currentTimeMillis();
		System.out.println("Exness: " + request.getExnessId() + " symbol: " + request.getSymbol() + " count: " + request.getListOrders().size() + " time: " + convertTimeStringToUnix(request.getTime()) + " operation: " + operation.toString() + " take: " + (end-start) + "ms");
	}

	long convertTimeStringToUnix(String input) {
		long result = 0;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Set timezone to UTC
        
        try {
            // Parse the date string to a Date object
            Date date = dateFormat.parse(input);
            
            // Convert the Date object to a long representing the Unix timestamp
            long unixTimestamp = date.getTime() / 1000; // Divide by 1000 to convert milliseconds to seconds
            result = unixTimestamp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return result;

	}
}
