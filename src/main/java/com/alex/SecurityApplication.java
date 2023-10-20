package com.alex;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.alex.auth.AuthenticationService;
import com.alex.exception.ExistedException;
import com.alex.exception.NotFoundException;
import com.alex.service.ExnessService;
import com.alex.service.PrevService;
import com.alex.service.ProfitService;
import com.alex.service.TransactionService;
import com.alex.service.UserService;
import com.alex.user.Exness;
import com.alex.user.ExnessRepository;
import com.alex.user.Profit;
import com.alex.user.UserRepository;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RequiredArgsConstructor
@EnableScheduling
public class SecurityApplication {
	private final UserRepository userRepo;
	private final AuthenticationService service;
	private final PrevService prevService;
	private final UserService userService;
	private final ExnessService exService;
	private final ExnessRepository exRepo;
	private final TransactionService tranService;
	private final ProfitService proService;

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(

			AuthenticationService service
	) {
		return args -> {
//			var admin = RegisterRequest.builder()
//					.firstname("Admin")
//					.lastname("Admin")
//					.email("admin@gmail.com")
//					.password("123")
//					.role(ADMIN)
//					.build();
//			System.out.println("Admin token: " + service.register(admin).getAccessToken());
//
//			var manager = RegisterRequest.builder()
//					.firstname("Admin")
//					.lastname("Admin")
//					.email("manager@gmail.com")
//					.password("password")
//					.role(MANAGER)
//					.build();
//			System.out.println("Manager token: " + service.register(manager).getAccessToken());

		};
	}
	
//	@Scheduled(cron = "0 * * ? * *")
	//@Scheduled(cron = "* * * * * *")
//	public void test() {
//		authenticate();
//		authenticate();
//	}
	public void authenticate() {
		double profit = 3.123;
		double balance = 5343.123;
		String exnessId = "69339942";
		Optional<Exness> exness = exRepo.findByExness(exnessId);
		if (exness.isEmpty()) {
			System.out.println("Exness " + exnessId + " is not existed!");
			throw new NotFoundException("This exness " + exnessId + " is not existed!");
		}
		
		System.out.println("ExnessId= " + exnessId + " - Balance=" + balance + " - Profit=" + profit);

		Date currentDate = new Date();

		// Lấy ngày hiện tại
		TimeZone timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
        Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTime(currentDate);

		// Đặt thời gian thành 00:00:01
		calendar.set(Calendar.HOUR_OF_DAY, 7);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 1);

		// Lấy timestamp sau khi đặt thời gian
		long timestamp = calendar.getTimeInMillis() / 1000 - 86400;

		List<Profit> profits = proService.findByAmountAndTimeAndExness(profit, timestamp, exnessId);
		if (profits.size() > 0) {
			System.out.println("Exness ID " + exnessId + " has already saved");
			throw new ExistedException("Exness ID " + exnessId + " has already saved");
		}
		
        // 1) luu profit cua ngay truoc do
		userService.saveProfit(exnessId, profit, timestamp);
		// 2) luu balance cua ngay truoc do
		userService.saveBalance(exnessId, balance, timestamp);
		// 3) cap nhat balance
		userService.updateBalanceExness(exnessId, balance);
		// 4) cap nhat tong profit
		exService.updateTotalProfit(exnessId, profit);
	}
}
