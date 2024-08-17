package com.alex.auth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alex.dto.DailyDataRequest;
import com.alex.dto.OrderListDto;
import com.alex.dto.RealtimeDto;
import com.alex.dto.UpcomingCandleDto;
import com.alex.service.DailyDataService;
import com.alex.service.Mq4DataService;
import com.alex.user.DailyData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AuthenticationController {
	private final Mq4DataService mq4Service;
	private final DailyDataService dailyDataService;
	
	@GetMapping("/dailyData/{exnessId}")
	public ResponseEntity<Integer> isDailyDataSent(@PathVariable("exnessId") String exnessId) {
		DailyData result = dailyDataService.findByExnessId(exnessId);
		int response = -1;
		if (result.isTodayHasData()) {
			response = 1; 
		} else {
			response = 0;
		}
		return ResponseEntity.ok(response);
		
	}
	
	@PostMapping("/dailyData")
	public ResponseEntity<String> setDailyDataStatus(@RequestBody DailyDataRequest request) {
		dailyDataService.setDailyDataStatus(request);
		return ResponseEntity.ok("ok");
		
	}
	
	@GetMapping("/testdate")
	public ResponseEntity<String> testdate() {
		Date currentDateTime = new Date();

		// Lấy ngày hiện tại
		TimeZone timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTime(currentDateTime);

		// Đặt thời gian thành 00:00:01
		calendar.set(Calendar.HOUR_OF_DAY, 7);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		// Lấy timestamp sau khi đặt thời gian
		long timestamp = calendar.getTimeInMillis() / 1000;
		
		long unixTimestamp = convertToUnixTimestamp("2024-02-28", "GMT+7");
		
		return ResponseEntity.ok(currentDateTime + " - " + timestamp + " - " + unixTimestamp);
	}
	
	public static long convertToUnixTimestamp(String dateString, String timeZoneId) {
        try {
            // Định dạng của chuỗi ngày
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            // Đặt múi giờ cho đối tượng DateFormat
            dateFormat.setTimeZone(TimeZone.getTimeZone(timeZoneId));
            
            // Chuyển đổi chuỗi ngày thành đối tượng Date
            Date date = dateFormat.parse(dateString);
            
            // Sử dụng Calendar để thiết lập giờ, phút và giây
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            
            // Lấy giá trị Unix timestamp
            long unixTimestamp = calendar.getTimeInMillis() / 1000; // Chia cho 1000 để chuyển từ mili giây sang giây
            return unixTimestamp;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Trả về giá trị âm để chỉ ra lỗi
        }
    }

	@GetMapping("/real-time-data/latest")
	public ResponseEntity<String> getLatestData() {
		long latestTransaction = mq4Service.getLatestTransaction();
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");

		// Chuyển đổi timestamp thành đối tượng Date
		Date date = new Date(latestTransaction * 1000); // *1000 để đổi về milliseconds

		// Chuyển đối tượng Date thành chuỗi với định dạng "yyyy-MM-dd"
		String formattedDate = dateFormat.format(date);
		return ResponseEntity.ok(formattedDate);
	}

	@GetMapping("/real-time-data/{exnessId}/{currencyName}")
	public ResponseEntity<String> realtimeDataCandle(@PathVariable("exnessId") String exnessId,
			@PathVariable("currencyName") String currencyName) {
		return ResponseEntity.ok(mq4Service.getRealtimeCandle(exnessId, currencyName));
	}

	@GetMapping("/real-time-candle/{exnessId}/{currencyName}")
	public ResponseEntity<String> realtimeDataCandleUpcoming(@PathVariable("exnessId") String exnessId,
			@PathVariable("currencyName") String currencyName) {
		return ResponseEntity.ok(mq4Service.getUpcomingCandle(exnessId, currencyName));
	}
	
	@GetMapping("/getCurrentMagic/{exnessId}/{currencyName}/{type}")
	public ResponseEntity<Integer> getCurrentMagic(@PathVariable("exnessId") String exnessId,
			@PathVariable("currencyName") String currencyName, @PathVariable("type") int type) {
		return ResponseEntity.ok(mq4Service.getCurrentMagic(exnessId, currencyName, type));
	}
	
	@GetMapping("/getOldMagics/{exnessId}/{currencyName}")
	public ResponseEntity<String> getOldMAgics(@PathVariable("exnessId") String exnessId,
			@PathVariable("currencyName") String currencyName) {
		return ResponseEntity.ok(mq4Service.getOldMagics(exnessId, currencyName));
	}
	
	@GetMapping("/accumulate/{exnessId}/{currencyName}/{type}")
	public ResponseEntity<Double> accumulateByType(@PathVariable("exnessId") String exnessId,
			@PathVariable("currencyName") String currencyName, @PathVariable("type") int type) {
		return ResponseEntity.ok(mq4Service.getAccumulateProfitByType(exnessId, currencyName, type));
	}

	@PostMapping("/real-time-candle")
	public ResponseEntity<String> realtimeCandleHandling(@RequestBody UpcomingCandleDto upcomingCandle) {
		mq4Service.saveUpcomingCandle(upcomingCandle);
		return ResponseEntity.ok("ok");
	}
	
	@PostMapping("/order-list")
	public ResponseEntity<String> saveOrders(@RequestBody OrderListDto request) {
		
		mq4Service.saveOrders(request);
		
		return ResponseEntity.ok("ok");
	}

	@PostMapping("/real-time-data")
	public ResponseEntity<String> realtimeHandling(@RequestBody RealtimeDto realtimeDto) {
		mq4Service.saveData(realtimeDto);
		return ResponseEntity.ok("ok");
	}

	
}
