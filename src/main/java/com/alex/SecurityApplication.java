package com.alex;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import com.alex.auth.AuthenticationService;
import com.alex.dto.AuthResponse;
import com.alex.dto.DataItem;
import com.alex.dto.LoginRequest;
import com.alex.service.CommissionService;
import com.alex.service.ExnessService;
import com.alex.service.PrevService;
import com.alex.service.ProfitService;
import com.alex.service.TransactionService;
import com.alex.service.UserService;
import com.alex.user.Commission;
import com.alex.user.Exness;
import com.alex.user.ExnessRepository;
import com.alex.user.UserRepository;
import com.alex.utils.ExnessUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RequiredArgsConstructor
@EnableScheduling
public class SecurityApplication {
	@Autowired
	ExnessService exService;
	@Autowired
	UserService userService;
	@Autowired
	CommissionService commissService;

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(

			AuthenticationService service) {
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

	@Scheduled(cron = "0 5 7 * * *", zone = "GMT+7:00")
	public void cronJob() throws JsonMappingException, JsonProcessingException {
		Date currentDateTime = new Date();

		// Lấy ngày hiện tại
		TimeZone timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTime(currentDateTime);

		// Đặt thời gian thành 00:00:01
		calendar.set(Calendar.HOUR_OF_DAY, 7);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 1);

		// Lấy timestamp sau khi đặt thời gian
		long timestamp = calendar.getTimeInMillis() / 1000 - 86400;

		
		// Tạo đối tượng SimpleDateFormat với định dạng "yyyy-MM-dd"
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		// Chuyển đổi timestamp thành đối tượng Date
		Date date = new Date(timestamp * 1000); // *1000 để đổi về milliseconds

		// Chuyển đối tượng Date thành chuỗi với định dạng "yyyy-MM-dd"
		String formattedDate = dateFormat.format(date);

		String url = "https://my.exnessaffiliates.com/api/v2/auth/";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		headers.set("Accept", "application/json");

		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setLogin("Long_phan@ymail.com");
		loginRequest.setPassword("Xitrum11");

		HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

		ResponseEntity<AuthResponse> responseEntity = new RestTemplate().exchange(url, HttpMethod.POST, request,
				AuthResponse.class);
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			AuthResponse authResponse = responseEntity.getBody();
			String token = authResponse.getToken();

			// Gọi API khác với token
			// Ví dụ: Gửi yêu cầu GET đến một API sử dụng token
			String apiUrl = "https://my.exaffiliates.com/api/reports/rewards/?reward_date_from=" + formattedDate
					+ "&reward_date_to=" + formattedDate;

			HttpHeaders headersWithToken = new HttpHeaders();
			headersWithToken.set("Authorization", "JWT " + token);

			HttpEntity<String> requestWithToken = new HttpEntity<>(headersWithToken);

			ResponseEntity<String> apiResponse = new RestTemplate().exchange(apiUrl, HttpMethod.GET, requestWithToken,
					String.class);
			List<DataItem> dataItems = new ArrayList<>();
			String json = apiResponse.getBody();

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(json); // Chuyển JSON thành một đối tượng JsonNode

			if (rootNode.has("data")) {
				JsonNode dataNode = rootNode.get("data");
				if (dataNode.isArray()) {
					dataItems = objectMapper.readValue(dataNode.toString(), new TypeReference<List<DataItem>>() {
					});
				}
			}

			System.out.println(dataItems);

			for (DataItem item : dataItems) {
				Long clientAccount = item.getClient_account();
				Optional<Exness> exness = exService.findByExnessId(String.valueOf(clientAccount));
				if (exness.isEmpty()) {
					continue;
				}
				Double rewardUsd = Double.parseDouble(item.getReward_usd());

				Commission commission = new Commission();
				commission.setAmount(rewardUsd);
				commission.setExnessId(exness.get().getExness());
				commission.setTransactionId(item.getClient_uid());
				commission.setTime(timestamp);
				try {
					commissService.saveCommission(commission);
				} catch (Exception e) {
					e.printStackTrace();
				}

				userService.updateTotalCommission(exness.get().getUser(), rewardUsd);
			}
		}
	}

	// Chuyen status ve inactive
//	@Scheduled(cron = "0 57 14 * * *", zone = "GMT+7:00")
//	public void toggle() {
//		userService.toggleStatusAllBranch("ALEX");
//	}
}
