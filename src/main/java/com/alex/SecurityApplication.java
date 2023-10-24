package com.alex;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.alex.auth.AuthenticationService;
import com.alex.service.ExnessService;
import com.alex.service.PrevService;
import com.alex.service.ProfitService;
import com.alex.service.TransactionService;
import com.alex.service.UserService;
import com.alex.user.ExnessRepository;
import com.alex.user.UserRepository;
import com.alex.utils.ExnessUtils;

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
	
	@Scheduled(cron = "0 5 7 * * *", zone="GMT+7:00")
//	@Scheduled(cron = "0 28 11 * * *", zone="GMT+7:00")
	public static void cronJob() {
		try {
			ExnessUtils.getIB();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
