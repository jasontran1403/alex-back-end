package com.alex.auth;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alex.dto.AuthResponse;
import com.alex.dto.DataItem;
import com.alex.dto.LoginRequest;
import com.alex.dto.MessageRequest;
import com.alex.exception.ExistedException;
import com.alex.exception.NotFoundException;
import com.alex.service.BalanceService;
import com.alex.service.CommissionService;
import com.alex.service.ExnessService;
import com.alex.service.HistoryService;
import com.alex.service.MessageService;
import com.alex.service.PrevService;
import com.alex.service.ProfitService;
import com.alex.service.TransactionService;
import com.alex.service.UserService;
import com.alex.user.Commission;
import com.alex.user.Exness;
import com.alex.user.ExnessRepository;
import com.alex.user.ExnessTransaction;
import com.alex.user.ExnessTransactionRepository;
import com.alex.user.History;
import com.alex.user.Transaction;
import com.alex.user.User;
import com.alex.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AuthenticationController {
	private final UserRepository userRepo;
	private final AuthenticationService service;
	private final HistoryService hisService;
	private final PrevService prevService;
	private final ExnessTransactionRepository exTranRepo;
	private final UserService userService;
	private final ExnessService exService;
	private final MessageService messService;
	private final ExnessRepository exRepo;
	private final TransactionService tranService;
	private final ProfitService proService;
	private final BalanceService balanceService;
	private final CommissionService commissService;

	@PostMapping("/test-mess")
	public ResponseEntity<String> testMessage(@RequestBody MessageRequest request) {
		messService.saveMessage(request);
		return ResponseEntity.ok("OK");
	}

	@GetMapping("/test/{branchName}")
	public ResponseEntity<List<User>> test(@PathVariable("branchName") String branchName) {
		List<User> listUsers = userService.getUsersByBranchName(branchName);
		if (listUsers.size() == 0) {
			throw new NotFoundException("This branch name: " + branchName + " is not existed!");
		}
		return ResponseEntity.ok(listUsers);
	}

	// check co trong he thong hay
	// curl -X GET --header 'Accept: application/json' --header 'Authorization: JWT
	// eyJhbGciOiJSUzI1NiIsImtpZCI6InVzZXIiLCJ0eXAiOiJKV1QifQ.eyJqdGkiOiIyNGNiZDE0OTk0ZDg0ZjRkODk3OGE2YjY3YmQ4YTFmMiIsImV4cCI6MTY5ODY3MDAyNywiaXNzIjoiQXV0aGVudGljYXRpb24iLCJpYXQiOjE2OTg2NDg0MjcsInN1YiI6IjViYjhhYWE5MjExYTQwMTRiOGZiYjViNjNmYmY5NDA1IiwiYXVkIjpbInBhcnRuZXJzaGlwIl0sImFkZGl0aW9uYWxfcGFyYW1zIjp7IndsX2lkIjoiODcxMWI4YWEtY2M2OC00MTNhLTgwMzQtYzI3MTZhMmNlMTRhIn19.BrCE3O2ZoOllnX_ee5gxOynzxvZQLBZA5c9nQqP0EO8mSym3GLGU4wb_asJba1BshZT78jaTxEeIbttsxPN_-o_MMmDw41kNAvLnYxbESr9K4kXLY64UUUAGxGQt0szzZStNZXjj_a3ze5VReiE6zSg59apox-fgOFnepUhBW-dv7ah1STMw-4bvE-0JvqD0Fss_9_Yx7s5ElVrzpSJPV2dMaGcUh_A7eWxa_DdDBvQOJ7fXaQ8_jGsWxtcpFDCK1iW6pGVJAQL_5kWTAsP_Qx_JHr0UYI8FokyDXuZ7qJXRQcK-UQdbwy6PNqL-wKi1xe5s74iY4OOKsXfAiSch4AbTIa6JTRJXkegx78vZ0GzFIj5SntszY6kQ5PjPmjTm4P35hVWIKhoFAKPOpt23MjaD0g2PkSQRD8sVNhO0AKSA4Z1k-0h6ec94FaA9iR1Kz0bpdgzV6vZB702gcijm-fxLp0_xDTRhFJffOWrNP7JAA3MpFZMdsps3HHMTfc2TVG1w6BBdCw-pGHqyUOaId54riFskhK__4JLB4uRDnKy0Gn_liiHHCrYSbYYWuGv9ZLh0zwA1m8pBi8IlXd0YC03RLtRY0AOdeN9Km1lvCCrmzm8ZrmJlthk30wlud4KbJlOzogkgq2ULhU0gLFaujguHuiBrEYue64R-lDCBh-E'
	// 'https://my.exnessaffiliates.com/api/reports/clients/?client_account=117057472'

	@GetMapping("/exaffiliates/{day}")
	public ResponseEntity<String> retrieveData(@PathVariable("day") int day) throws JsonMappingException, JsonProcessingException {
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
		long timestamp = calendar.getTimeInMillis() / 1000 - (86400*day);

		
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

		return ResponseEntity.ok("OK");
	}

	@GetMapping("/share-ib")
//	@Scheduled(cron = "0 5 7 * * *", zone="GMT+7:00")
	public ResponseEntity<List<DataItem>> shareIb() throws JsonMappingException, JsonProcessingException {
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
		List<DataItem> dataItems = new ArrayList<>();

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
		}

		double[] totalAmount = { 0.0, 0.0, 0.0 };

		List<History> toAdmin = new ArrayList<>();
		List<History> toUser = new ArrayList<>();
		List<History> toLisa = new ArrayList<>();
		List<String> listExness = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		for (DataItem item : dataItems) {
			long exnessTransaction = item.getId();
			long exnessId = item.getClient_account();
			double amount = Double.parseDouble(item.getReward_usd());
			double originalAmountPayToNetwork = amount * 0.8;
			double remainingAmountPayToNetwork = originalAmountPayToNetwork;
			double amountToAdmin = amount - originalAmountPayToNetwork;
			// Kiem tra khoan hoa hong do da tra hay chua
			Optional<ExnessTransaction> exTran = exTranRepo.findByTransactionExness(String.valueOf(exnessTransaction));
			if (exTran.isPresent()) {
				sb.append(exnessTransaction + " đã được chi trả.\n");
				continue;
			} else {
				totalAmount[0] += amount;
				totalAmount[1] += amountToAdmin;
				// Chi cho system 20% IB
				History historyToAdmin = new History();
				User userAdmin = userRepo.getByEmail("admin@gmail.com");
				historyToAdmin.setAmount(amountToAdmin);
				historyToAdmin.setReceiver(userAdmin.getEmail());
				historyToAdmin.setSender(String.valueOf(exnessId));
				historyToAdmin.setTransaction(String.valueOf(exnessTransaction));
				historyToAdmin.setTime(String.valueOf(System.currentTimeMillis() / 1000));
				historyToAdmin.setMessage("20% từ số IB=" + amount + " của ExnessID=" + exnessId);
				toAdmin.add(historyToAdmin);

				HashMap<Integer, String> listToPayIB = getNetWorkToLisa(String.valueOf(exnessId));
				for (HashMap.Entry<Integer, String> entry : listToPayIB.entrySet()) {
					String recipientEmail = entry.getValue();
					double amountToPay = 0.0;

					if (recipientEmail.equals("lisa@gmail.com")) {
						// Nếu người nhận là lisa@gmail, gửi toàn bộ số remainingAmountPayToNetwork (số
						// IB chia còn lại khi gặp lisa@gmail.com) cho họ
						amountToPay = remainingAmountPayToNetwork;
						History historyToLisa = new History();
						User userLisa = userRepo.findByEmail("lisa@gmail.com").get();
						historyToLisa.setAmount(amountToPay);
						historyToLisa.setReceiver(userLisa.getEmail());
						historyToLisa.setSender(String.valueOf(exnessId));
						historyToLisa.setTransaction(String.valueOf(exnessTransaction));
						historyToLisa.setTime(String.valueOf(System.currentTimeMillis() / 1000));
						historyToLisa.setMessage(
								"Tìm thấy Lisa, chi hết số IB=" + amount + " còn lại của ExnessID=" + exnessId);

						toLisa.add(historyToLisa);

						totalAmount[2] += amountToPay;
						remainingAmountPayToNetwork -= amountToPay;
						break; // Dừng vòng lặp vì đã gửi hết số tiền
					} else {
						if (recipientEmail.equals("admin@gmail.com")) {
							// Không chia cho tài khoản
							continue;
						} else {
							// Ngược lại, gửi 50% của remainingAmountPayToNetwork cho người nhận
							amountToPay = remainingAmountPayToNetwork / 2;
							History historyToSystem = new History();
							Optional<User> userTemp = userRepo.findByEmail(recipientEmail);
							if (userTemp.isEmpty()) {
								continue;
							}

							User userInSystem = userTemp.get();
							double amountOfUser = exService.getBalanceByEmail(userInSystem.getEmail());
							if (amountOfUser < 100_000) {
								break;
							}
							historyToSystem.setAmount(amountToPay);
							historyToSystem.setReceiver(userInSystem.getEmail());
							historyToSystem.setSender(String.valueOf(exnessId));
							historyToSystem.setTransaction(String.valueOf(exnessTransaction));
							historyToSystem.setTime(String.valueOf(System.currentTimeMillis() / 1000));
							historyToSystem.setMessage("Hoa hồng từ khoản IB=" + amount + " của ExnessID=" + exnessId);
							toUser.add(historyToSystem);

							totalAmount[2] += amountToPay;
							remainingAmountPayToNetwork -= amountToPay; // Giảm số tiền còn lại
						}
					}
				}
				if (remainingAmountPayToNetwork > 0) {
					History historyToLisa = new History();
					User userLisa = userRepo.findByEmail("lisa@gmail.com").get();
					historyToLisa.setAmount(remainingAmountPayToNetwork);
					historyToLisa.setReceiver(userLisa.getEmail());
					historyToLisa.setSender(String.valueOf(exnessId));
					historyToLisa.setTransaction(String.valueOf(exnessTransaction));
					historyToLisa.setTime(String.valueOf(System.currentTimeMillis() / 1000));
					historyToLisa.setMessage("Số còn lại từ khoản IB=" + amount + " của ExnessID=" + exnessId);

					toLisa.add(historyToLisa);
				}

				listExness.add(String.valueOf(exnessTransaction));
			}
		}

		Thread thread1 = new Thread() {
			public void run() {
				for (String item : listExness) {
					ExnessTransaction exnessTransactionFromExcel = new ExnessTransaction();
					exnessTransactionFromExcel.setTime(String.valueOf(System.currentTimeMillis()));
					exnessTransactionFromExcel.setTransactionExness(item);
					exTranRepo.save(exnessTransactionFromExcel);
				}
			}
		};

		Thread thread2 = new Thread() {
			public void run() {
				for (History item : toAdmin) {
					hisService.saveHistory(item);
					User user = userRepo.findByEmail(item.getReceiver()).get();
					user.setCommission(user.getCommission() + item.getAmount());
					userRepo.save(user);

				}
			}
		};

		Thread thread3 = new Thread() {
			public void run() {
				for (History item : toLisa) {
					hisService.saveHistory(item);
					User user = userRepo.findByEmail(item.getReceiver()).get();
					user.setCommission(user.getCommission() + item.getAmount());
					userRepo.save(user);
				}
			}
		};

		Thread thread4 = new Thread() {
			public void run() {
				for (History item : toUser) {
					hisService.saveHistory(item);
					User user = userRepo.findByEmail(item.getReceiver()).get();
					user.setCommission(user.getCommission() + item.getAmount());
					userRepo.save(user);
				}
			}
		};

		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();

		System.out.println(sb.toString());
		return ResponseEntity.ok(dataItems);
	}

	@GetMapping("/transfer-transaction/exnessId={exnessId}&transaction={type}&amount={amount}&time={time}")
	public ResponseEntity<String> insertData(@PathVariable("exnessId") String exnessId, @PathVariable("type") int type,
			@PathVariable("amount") double amount, @PathVariable("time") long time) {
		Optional<Exness> exness = exRepo.findByExness(exnessId);
		if (exness.isEmpty()) {
			System.out.println("Exness " + exnessId + " is not existed!");
			throw new NotFoundException("This exness " + exnessId + " is not existed!");
		}

		String transactionType;
		if (type == 0) {
			transactionType = "Withdraw";
		} else {
			transactionType = "Deposit";
		}

		try {
			Transaction transaction = new Transaction();
			transaction.setExnessId(exnessId);
			transaction.setAmount(Math.abs(amount));
			transaction.setType(transactionType);
			transaction.setTime(time);
			tranService.saveTransaction(transaction);

			System.out.println("ExnessId= " + exnessId + " type= " + type + " amount= " + amount + " time= " + time);
		} catch (Exception e) {
			System.out.println("Exness ID " + exnessId + " has already saved");
			throw new ExistedException("Exness ID " + exnessId + " has already saved");
		}

		return ResponseEntity.ok("OK");
	}

	@GetMapping("/transfer-data/exnessId={exnessId}&balance={balance}&profit={profit}")
	public ResponseEntity<String> insertData(@PathVariable("exnessId") String exnessId,
			@PathVariable("balance") double balance, @PathVariable("profit") double profit) {
		Optional<Exness> exness = exRepo.findByExness(exnessId);
		if (exness.isEmpty()) {
			System.out.println("Exness " + exnessId + " is not existed!");
			throw new NotFoundException("This exness " + exnessId + " is not existed!");
		}

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
		
		System.out.println(timestamp);

		// 1) Lưu profit của ngày trước đó
		boolean checkProfit = userService.saveProfit(exnessId, profit, timestamp);
		// 2) Lưu balance của ngày trước đó
		boolean checkBalance = userService.saveBalance(exnessId, balance, timestamp);

		if (checkProfit && checkBalance) {
			userService.updateBalanceExness(exnessId, balance);
			// 4) Cập nhật tổng profit
			exService.updateTotalProfit(exnessId, profit);
		} else {
			throw new ExistedException("Exness ID " + exnessId + " on " + calendar.getTime() + " has already saved");
		}

		return ResponseEntity.ok(String.valueOf(timestamp));
	}

	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
		return ResponseEntity.ok(service.register(request));
	}
	
	@PostMapping("/registerLisa")
	public ResponseEntity<AuthenticationResponse> registerLisa(@RequestBody RegisterLisaRequest request) {
		return ResponseEntity.ok(service.registerLisa(request));
	}

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
		return ResponseEntity.ok(service.authenticate(request));
	}
	
	@PostMapping("/authenticateLisa")
	public ResponseEntity<AuthenticationResponse> authenticateLisa(@RequestBody AuthenticationRequest request) {
		return ResponseEntity.ok(service.authenticateLisa(request));
	}

	@PostMapping("/getCode")
	public ResponseEntity<String> getCode(@RequestBody RefferalRequest request) {
		return ResponseEntity.ok(service.generateCode(request.getEmail()));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
		return ResponseEntity.ok(service.forgotPassword(request));
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(@RequestBody LogoutRequest request) {
		return ResponseEntity.ok(service.logout(request.getAccess_token()));
	}

	@PostMapping("/refresh-token")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		service.refreshToken(request, response);
	}

	private HashMap<Integer, String> getNetWorkToLisa(String exness) {
		HashMap<Integer, String> listNetWorks = new HashMap<>();
		try {
			Optional<Exness> exnessF0 = exRepo.findByExness(exness);
			int level = 1;

			String userF1 = exnessF0.get().getUser().getRefferal();
			listNetWorks.put(level, userF1);
			level++;
			String userPointer = userF1;

			do {
				Optional<User> user = userRepo.findByEmail(userPointer);
				if (user.isEmpty()) {
					break;
				}
				if (!user.get().getRefferal().equals("")) {
					listNetWorks.put(level, user.get().getRefferal());
				}

				userPointer = user.get().getRefferal();
				level++;
			} while (!userPointer.equals("lisa@gmail.com") && level <= 5);
		} catch (Exception e) {
			return new HashMap<>();
		}

		return listNetWorks;
	}
}
