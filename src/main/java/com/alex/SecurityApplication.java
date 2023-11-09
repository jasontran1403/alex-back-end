package com.alex;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.alex.service.HistoryService;
import com.alex.service.UserService;
import com.alex.user.Commission;
import com.alex.user.Exness;
import com.alex.user.ExnessRepository;
import com.alex.user.ExnessTransaction;
import com.alex.user.ExnessTransactionRepository;
import com.alex.user.History;
import com.alex.user.User;
import com.alex.user.UserRepository;
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
	ExnessRepository exRepo;
	@Autowired
	UserService userService;
	@Autowired
	CommissionService commissService;
	@Autowired
	HistoryService hisService;
	@Autowired
	UserRepository userRepo;
	@Autowired
	ExnessTransactionRepository exTranRepo;

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner commandLineRunner(
//
//			AuthenticationService service) {
//		return args -> {
//			String filePath = "/Users/jason/Desktop/test.xlsx"; // Đường dẫn đến file Excel trên máy Mac của bạn.
//			try {
//				FileInputStream fis = new FileInputStream(filePath);
//				@SuppressWarnings("resource")
//				Workbook workbook = new XSSFWorkbook(fis);
//
//				for (int i = 0; i < 44; i++) {
//
//					Sheet sheet = workbook.getSheetAt(i); // Giả sử bạn đang xử lý sheet đầu tiên.
//					String exness_id = sheet.getSheetName();
//					Map<String, Double> dailyTotalMap = new HashMap<>();
//					Map<String, Double> dailyTotalMap2 = new HashMap<>();
//					DecimalFormat df = new DecimalFormat("#.00");
//					double profit = 0.0;
//					double balance = 0.0;
//					double totalProfits = 0.0;
//					Cell cellProfit = sheet.getRow(0).getCell(14);
//					if (cellProfit != null) {
//						profit = cellProfit.getNumericCellValue();
//					}
//					for (Row row : sheet) {
//						Cell cell3 = row.getCell(2); // Giả sử cột thứ 3 chứa giá trị "buy" hoặc "sell".
//
//						if (cell3 != null) {
//							if (cell3.getCellType() == CellType.STRING) {
//								String transactionType = cell3.getStringCellValue();
//								if (!"balance".equals(transactionType)) {
//									Cell cell8 = row.getCell(8);
//									Cell cell13 = row.getCell(12); // Giả sử cột thứ 13 chứa giá trị cần tính tổng.
//									Cell cell14 = row.getCell(13); // Giả sử cột thứ 14 chứa giá trị cần tính tổng.
//									Cell cell10 = row.getCell(10);
//									if (!cell10.getStringCellValue().equals("cancelled")
//											&& !cell10.getStringCellValue().equals("illiquidity")) {
//										double valueDateTime = cell8.getNumericCellValue();
//										Date dateAsDate = DateUtil.getJavaDate(valueDateTime);
//										LocalDateTime date = dateAsDate.toInstant().atZone(ZoneId.systemDefault())
//												.toLocalDateTime();
//										String dateFromExcel = date.toString();
//										String dateFormatted = dateFromExcel.split("T")[0]; // Trích xuất phần ngày.
//
//										double value13 = cell13.getNumericCellValue();
//										double value14 = cell14.getNumericCellValue();
//
//										balance += value13;
//										balance += value14;
//										if (dailyTotalMap.containsKey(dateFormatted)) {
//											dailyTotalMap.put(dateFormatted,
//													dailyTotalMap.get(dateFormatted) + value13 + value14);
//										} else {
//											dailyTotalMap.put(dateFormatted, value13 + value14);
//										}
//
//									}
//								} else {
//									Cell cell1 = row.getCell(1);
//									Cell cell13 = row.getCell(13);
//
//									double valueDateTime = cell1.getNumericCellValue();
//									Date dateAsDate = DateUtil.getJavaDate(valueDateTime);
//									LocalDateTime date = dateAsDate.toInstant().atZone(ZoneId.systemDefault())
//											.toLocalDateTime();
//									String dateFromExcel = date.toString();
//									double value13 = cell13.getNumericCellValue();
//
//									balance += value13;
//									dailyTotalMap2.put(dateFromExcel, value13);
//								}
//							}
//						}
//					}
//
//					List<Map.Entry<String, Double>> sortedList = new ArrayList<>(dailyTotalMap.entrySet());
//
//					// Sắp xếp danh sách theo trường "day" tăng dần.
//					sortedList.sort(Comparator.comparing(Map.Entry::getKey));
//
//					// In ra dữ liệu đã sắp xếp.
//					Date currentDateTime = new Date();
//
//					// Lấy ngày hiện tại
//					TimeZone timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
//					Calendar calendar = Calendar.getInstance(timeZone);
//					calendar.setTime(currentDateTime);
//
//					// Đặt thời gian thành 00:00:01
//					calendar.set(Calendar.HOUR_OF_DAY, 7);
//					calendar.set(Calendar.MINUTE, 0);
//					calendar.set(Calendar.SECOND, 1);
//
//					// Lấy timestamp sau khi đặt thời gian
//					double totalBefore = 0.0;
//					for (Map.Entry<String, Double> entry : sortedList) {
//						String day = entry.getKey();
//						double total = entry.getValue();
//						
//
//						// Kiểm tra xem ngày có bắt đầu từ 1/10 trở đi không
//						String[] part = day.split("-");
//						int year = Integer.parseInt(part[0]);
//						int month = Integer.parseInt(part[1]);
//						if (year > 2023 || (year == 2023 && month >= 10)) {
//							// Tính timestamp chỉ cho những ngày từ 1/10 trở đi
//							totalProfits += total;
//							calendar.set(Calendar.DATE, Integer.parseInt(part[2]));
//							calendar.set(Calendar.MONTH, month - 1);
//							calendar.set(Calendar.YEAR, year);
//							long timestamp = calendar.getTimeInMillis() / 1000;
//							System.out.println("('" + exness_id + "', " + timestamp + ", (-" + df.format(total) + ")),");
//						} else {
//							totalBefore += total;
//						}
//					}
//					
//					List<Map.Entry<String, Double>> sortedList2 = new ArrayList<>(dailyTotalMap2.entrySet());
//
//					// Sắp xếp danh sách theo trường "day" tăng dần.
//					sortedList2.sort(Comparator.comparing(Map.Entry::getKey));
//					double before = 0.0;
//
//					System.out.println("--------- Transaction ---------");
//					for (Map.Entry<String, Double> entry : sortedList2) {
//						String day = entry.getKey();
//						double total = entry.getValue();
//						String datePart = day.split("T")[0];
//						String[] part = day.split("-");
//						int year = Integer.parseInt(part[0]);
//						int month = Integer.parseInt(part[1]);
//						if (year > 2023 || (year == 2023 && month < 10)) {
//							// Tính timestamp chỉ cho những ngày từ 1/10 trở đi
//							before += total;
//						} else {
//							// Định dạng của chuỗi ngày tháng
//							// Chuyển đổi chuỗi ngày tháng thành đối tượng LocalDateTime
//							LocalDateTime localDateTime = LocalDateTime.parse(day);
//
//							// Đặt múi giờ thành UTC+0 (GMT)
//							ZoneId utcZone = ZoneId.of("Etc/UTC");
//
//							// Chuyển đổi thành ZonedDateTime với múi giờ UTC
//							ZonedDateTime zonedDateTime = localDateTime.atZone(utcZone);
//
//							// Chuyển đổi thành Unix timestamp (giây)
//							String type = "";
//							long unixTimestamp = zonedDateTime.toEpochSecond();
//							if (total > 0) {
//								type = "Deposit";
//							} else {
//								type = "Withdraw";
//							}
////							System.out.println("('" + exness_id + "', " + unixTimestamp + ", " + Math.abs(total) + ", '"
////									+ type + "'),");
//						}
//
//					}
////					System.out.println("--------- Transaction ---------");
////					
////					System.out.println("Tong profit 1/10 -> 7/11 " + df.format(totalProfits));
////					System.out.println("Tong profit truoc 1/10 " + df.format(totalBefore));
////					System.out.println("Balance vao ngay 1/10 " + df.format(totalBefore + before));
//
////					System.out.println("update exness set total_profit = " + df.format(profit) + " where exness = '"
////							+ exness_id + "';");
//					System.out.println("-- Exness id#" + exness_id + " balance " + df.format(balance));
//					System.out.println();
//					fis.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		};
//
//	}

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
			
			double[] totalAmount = { 0.0, 0.0, 0.0 };

			List<History> toAdmin = new ArrayList<>();
			List<History> toUser = new ArrayList<>();
			List<History> toLisa = new ArrayList<>();
			List<String> listExness = new ArrayList<>();
			StringBuilder sb = new StringBuilder();

			for (DataItem item : dataItems) {
				Long clientAccount = item.getClient_account();
				Optional<Exness> exness = exService.findByExnessId(String.valueOf(clientAccount));
				
				if (exness.isEmpty()) {
					continue;
				}
				if (exness.get().getUser().getBranchName().equals("ALEX")) {
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
				} else if (exness.get().getUser().getBranchName().equals("LISA")) {
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
						historyToAdmin.setTime(String.valueOf(timestamp));
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
								historyToLisa.setTime(String.valueOf(timestamp));
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
									historyToSystem.setTime(String.valueOf(timestamp));
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
							historyToLisa.setTime(String.valueOf(timestamp));
							historyToLisa.setMessage("Số còn lại từ khoản IB=" + amount + " của ExnessID=" + exnessId);

							toLisa.add(historyToLisa);
						}

						listExness.add(String.valueOf(exnessTransaction));
					}
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
		}
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

	// Chuyen status ve inactive
//	@Scheduled(cron = "0 57 14 * * *", zone = "GMT+7:00")
//	public void toggle() {
//		userService.toggleStatusAllBranch("ALEX");
//	}
}

//var admin = RegisterRequest.builder()
//.firstname("Admin")
//.lastname("Admin")
//.email("admin@gmail.com")
//.password("123")
//.role(ADMIN)
//.build();
//System.out.println("Admin token: " + service.register(admin).getAccessToken());
//
//var manager = RegisterRequest.builder()
//.firstname("Admin")
//.lastname("Admin")
//.email("manager@gmail.com")
//.password("password")
//.role(MANAGER)
//.build();
//System.out.println("Manager token: " + service.register(manager).getAccessToken());
