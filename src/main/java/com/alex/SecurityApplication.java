package com.alex;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import com.alex.dto.AuthResponse;
import com.alex.dto.DataItem;
import com.alex.dto.LoginRequest;
import com.alex.service.BalanceService;
import com.alex.service.CommissionService;
import com.alex.service.ExnessCommissionService;
import com.alex.service.ExnessService;
import com.alex.service.HistoryService;
import com.alex.service.ProfitService;
import com.alex.service.TransactionService;
import com.alex.service.UserService;
import com.alex.user.AdminPixiu;
import com.alex.user.AdminPixiuRepository;
import com.alex.user.Balance;
import com.alex.user.Commission;
import com.alex.user.Exness;
import com.alex.user.ExnessCommission;
import com.alex.user.ExnessRepository;
import com.alex.user.ExnessTransactionRepository;
import com.alex.user.ManagerPixiu;
import com.alex.user.ManagerPixiuRepository;
import com.alex.user.Profit;
import com.alex.user.User;
import com.alex.user.UserRepository;
import com.alex.utils.CalibrateBracketIB;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RequiredArgsConstructor
@EnableScheduling
public class SecurityApplication {
	@Autowired
	ExnessService exService;
	@Autowired
	ExnessRepository exRepo;
	@Autowired
	UserService userService;
	@Autowired
	CommissionService commissService;
	@Autowired
	HistoryService hisService;
	@Autowired
	ProfitService proService;
	@Autowired
	BalanceService balanceService;
	@Autowired
	UserRepository userRepo;
	@Autowired
	ExnessTransactionRepository exTranRepo;
	@Autowired
	TransactionService tranService;
	@Autowired
	CalibrateBracketIB utils;
	@Autowired
	ManagerPixiuRepository managerRepo;
	@Autowired
	AdminPixiuRepository adminRepo;
	@Autowired
	ExnessCommissionService exnessCommissionService;

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

	@Scheduled(cron = "0 0 16 * * *", zone = "GMT+7:00")
	public void cronJob() throws JsonMappingException, JsonProcessingException {
		List<DataItem> results = new ArrayList<>();
		Date currentDateTime = new Date();
		long testTime = 0;

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
					+ "&reward_date_to=" + formattedDate + "&limit=1000";

			HttpHeaders headersWithToken = new HttpHeaders();
			headersWithToken.set("Authorization", "JWT " + token);

			HttpEntity<String> requestWithToken = new HttpEntity<>(headersWithToken);

			ResponseEntity<String> apiResponse = new RestTemplate().exchange(apiUrl, HttpMethod.GET, requestWithToken,
					String.class);
			
			String json = apiResponse.getBody();

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(json); // Chuyển JSON thành một đối tượng JsonNode

			if (rootNode.has("data")) {
				JsonNode dataNode = rootNode.get("data");
				if (dataNode.isArray()) {
					results = objectMapper.readValue(dataNode.toString(), new TypeReference<List<DataItem>>() {
					});
				}
			}

			for (DataItem item : results) {
				ExnessCommission itemToWrite = new ExnessCommission();
				long unixTimestamp = convertToUnixTimestamp(item.getReward_date(), "GMT+7");
				testTime = unixTimestamp;
				itemToWrite.setTransactionId(String.valueOf(item.getId()));
				itemToWrite.setDate(unixTimestamp);
				itemToWrite.setAmount(Double.parseDouble(item.getReward()));
				itemToWrite.setCurrency(item.getCurrency());
				itemToWrite.setClientAccount(String.valueOf(item.getClient_account()));
				exnessCommissionService.saveExnessCommission(itemToWrite);
			}
		}
		
		System.out.println(formattedDate + " = " + results.size());
		System.out.println(testTime);
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
            return unixTimestamp + 86400;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Trả về giá trị âm để chỉ ra lỗi
        }
    }

	@Scheduled(cron = "0 0 8 * * *", zone = "GMT+7:00")
	public void updateTotalProfit() {
		Date currentDate = new Date();

		// Lấy ngày hiện tại
		TimeZone timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTime(currentDate);

		// Đặt thời gian thành 00:00:01
		calendar.set(Calendar.HOUR_OF_DAY, 7);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 1);

		String msg = "";
		// Lấy timestamp sau khi đặt thời gian
		long timestamp = calendar.getTimeInMillis() / 1000 - 86400;
		List<Profit> profits = proService.findAmountOfProfitsByTime(timestamp);
		msg += ">> Profits " + profits.size() + "\n";
		for (Profit profit : profits) {
			if (profit.getAmount() == 0) {
				msg += ">> ID " + profit.getExnessId() + " = 0\n";
			}
		}
		List<Balance> balances = balanceService.findAmountOfBalanceByTime(timestamp);
		msg += ">> Balances " + balances.size();
		for (Balance balance : balances) {
			if (balance.getAmount() == 0) {
				msg += ">> ID " + balance.getExnessId() + " = 0\n";
			}
		}

		List<Exness> listExness = exService.findAllExness();
		for (Exness exness : listExness) {
			double sumAmountProfit = proService.sumTotalProfit(exness.getExness());
			exService.fixTotalProfit(exness.getExness(), sumAmountProfit);
		}
		System.out.println(msg);
	}
	
	@Scheduled(cron = "0 30 16 * * *", zone = "GMT+7:00")
	@Transactional
	public void runData() throws JsonMappingException, JsonProcessingException {
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
		long timestamp = calendar.getTimeInMillis() / 1000 - 86400;

		List<DataItem> listData2 = getPixiuIB(timestamp);

		double totalIBSubBranch1 = 0, totalIBSubBranch2 = 0;

		double totalCapitalFromPixiu = tranService.getTotalDepositFromPixiu(timestamp) / 100;

		List<Exness> listExnessFromPixiu = exService.getListExnessByBranchName("PixiuGroup");
		User rootUserSubBranch1 = userRepo.getByEmail("pixiu@gmail.com");
		List<Exness> listExnessFromPixiuSub1 = utils.filterForSubBranch1(listExnessFromPixiu, rootUserSubBranch1);

		User rootUserSubBranch2 = userRepo.getByEmail("admin_dn@gmail.com");
		List<Exness> listExnessFromPixiuSub2 = utils.filterForSubBranch1(listExnessFromPixiu, rootUserSubBranch2);

		for (DataItem item : listData2) {
			for (Exness exness : listExnessFromPixiuSub1) {
				if (exness.getExness().equalsIgnoreCase(String.valueOf(item.getClient_account()))) {
					totalIBSubBranch1 += Double.parseDouble(item.getReward());
				}
			}

			for (Exness exness : listExnessFromPixiuSub2) {
				if (exness.getExness().equalsIgnoreCase(String.valueOf(item.getClient_account()))) {
					double amount = Double.parseDouble(item.getReward());
					if (amount > 0) {
						String messageForAdmin = "";
						messageForAdmin += "Exness ID#" + exness.getExness() + " được hưởng phần chia= " + amount * 0.6
								+ " từ tổng IB= " + amount * 0.8;

						AdminPixiu adminShareIB = new AdminPixiu();
						adminShareIB.setAmount(amount * 0.6);
						adminShareIB.setExnessId(exness.getExness());
						adminShareIB.setTime(timestamp);
						adminShareIB.setMessage(messageForAdmin);

						adminRepo.save(adminShareIB);
						System.out.println(adminShareIB);
					}
				}
			}

			if (Double.parseDouble(item.getReward()) > 0) {
				Commission commissionSubBranch1 = new Commission();
				commissionSubBranch1.setAmount(Double.parseDouble(item.getReward()) * 0.8);
				commissionSubBranch1.setExnessId(String.valueOf(item.getClient_account()));
				commissionSubBranch1.setTime(timestamp);
				commissionSubBranch1.setTransactionId(item.getClient_uid());
				commissionSubBranch1.setMessage("20% của " + Double.parseDouble(item.getReward()) + " chia cho LP="
						+ Double.parseDouble(item.getReward()) * 0.2);

				commissService.saveCommission(commissionSubBranch1);
				System.out.println(commissionSubBranch1);
			}
		}

		for (Exness item : listExnessFromPixiuSub1) {
			double accountCapital = tranService.getTotalDepositByExnessId(item.getExness()) / 100;
			if (item.getUser().getRole().name() == "MANAGER") {

				Map<String, List<Double>> resultForLeader = utils.calculateDistributedIB(item.getExness(),
						totalCapitalFromPixiu, accountCapital, totalIBSubBranch1, "MANAGER");
				Map<String, List<Double>> resultForAdmin = utils.calculateDistributedIB(item.getExness(),
						totalCapitalFromPixiu, accountCapital, totalIBSubBranch1, "ADMIN");
				if (resultForLeader.get(item.getExness()).size() > 0) {
					double amount = resultForLeader.get(item.getExness()).stream().reduce(0.0, Double::sum);
					String messageForLeader = "";
					messageForLeader += "IB= " + totalIBSubBranch1 + " - Tổng DS= " + totalCapitalFromPixiu
							+ " - DSCN= " + accountCapital;
					messageForLeader += "IB mốc 1 = " + resultForLeader.get(item.getExness()).get(0);
					messageForLeader += "IB mốc 2 (" + 0.2 + ")= " + resultForLeader.get(item.getExness()).get(1);
					messageForLeader += "IB mốc 3 (" + 0.4 + ")= " + resultForLeader.get(item.getExness()).get(2);
					messageForLeader += "IB mốc 4 (" + 0.6 + ")= " + resultForLeader.get(item.getExness()).get(3);

					if (amount > 0) {
						ManagerPixiu managerShareIB = new ManagerPixiu();
						managerShareIB.setAmount(amount);
						managerShareIB.setExnessId(item.getExness());
						managerShareIB.setTime(timestamp);
						managerShareIB.setMessage(messageForLeader);

						System.out.println(managerShareIB);
						managerRepo.save(managerShareIB);

						Exness exnessToUpdate = exService.findByExnessId(item.getExness()).get();
						User user = exnessToUpdate.getUser();
						user.setCommission(user.getCommission() + amount);
						userRepo.save(user);
					}
				}

				if (resultForAdmin.get(item.getExness()).size() > 0) {
					double amount = resultForAdmin.get(item.getExness()).stream().reduce(0.0, Double::sum);
					String messageForAdmin = "";
					messageForAdmin += "IB= " + totalIBSubBranch1 + " - Tổng DS= " + totalCapitalFromPixiu + " - DSCN= "
							+ accountCapital;
					messageForAdmin += "IB mốc 1 = " + resultForAdmin.get(item.getExness()).get(0);
					messageForAdmin += "IB mốc 2 (" + 0.3 + ")= " + resultForAdmin.get(item.getExness()).get(1);
					messageForAdmin += "IB mốc 3 (" + 0.5 + ")= " + resultForLeader.get(item.getExness()).get(2);
					messageForAdmin += "IB mốc 4 (" + 0.7 + ")= " + resultForAdmin.get(item.getExness()).get(3);

					if (amount > 0) {
						AdminPixiu adminShareIB = new AdminPixiu();
						adminShareIB.setAmount(amount);
						adminShareIB.setExnessId(item.getExness());
						adminShareIB.setTime(timestamp);
						adminShareIB.setMessage(messageForAdmin);

						adminRepo.save(adminShareIB);
						System.out.println(adminShareIB);

						Exness exnessToUpdate = exService.findByExnessId(item.getExness()).get();
						User user = exnessToUpdate.getUser();
						user.setCommission(user.getCommission() + amount);
						userRepo.save(user);
					}
				}
			}
		}
	}
	
	public List<DataItem> getPixiuIB(long dateTime) throws JsonMappingException, JsonProcessingException {
		List<DataItem> results = new ArrayList<>();

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

		// Tạo đối tượng SimpleDateFormat với định dạng "yyyy-MM-dd"
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		// Chuyển đổi timestamp thành đối tượng Date
		Date date = new Date(dateTime * 1000); // *1000 để đổi về milliseconds

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
			String apiUrl = "https://my.exaffiliates.com/api/reports/rewards/?limit=1000&reward_date_from="
					+ formattedDate + "&reward_date_to=" + formattedDate + "&limit=1000";

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

			for (DataItem item : dataItems) {
				Optional<Exness> exness = exService.findByExnessId(String.valueOf(item.getClient_account()));
				if (exness.isPresent()) {
					if (exness.get().getUser().getBranchName().equals("PixiuGroup")) {
						results.add(item);
					}
				}

			}

		}

		return results;
	}
}
