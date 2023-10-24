package com.alex.demo;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.poi.openxml4j.exceptions.PartAlreadyExistsException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alex.auth.AuthenticationService;
import com.alex.auth.RefferalRequest;
import com.alex.auth.UpdateExnessRequest;
import com.alex.auth.UpdateRefRequest;
import com.alex.auth.UpdateRefResponse;
import com.alex.dto.ChangePasswordRequest;
import com.alex.dto.InfoResponse;
import com.alex.dto.NetworkDto;
import com.alex.dto.PreviousMonthResponse;
import com.alex.dto.TwoFARequest;
import com.alex.dto.UpdateInfoRequest;
import com.alex.exception.NotFoundException;
import com.alex.service.CommissionService;
import com.alex.service.ExnessService;
import com.alex.service.MessageService;
import com.alex.service.PrevService;
import com.alex.service.TransactionService;
import com.alex.service.UserService;
import com.alex.user.Message;
import com.alex.user.Transaction;
import com.alex.user.User;
import com.alex.user.UserRepository;
import com.google.zxing.WriterException;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrDataFactory;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/secured")
@CrossOrigin("*")
@Hidden
@RequiredArgsConstructor
public class DemoController {
	private final UserRepository userRepo;
	private final AuthenticationService service;
	private final UserService userService;
	private final MessageService messService;
	private final ExnessService exService;
	private final PrevService prevService;
	private final SecretGenerator secretGenerator;
	private final PasswordEncoder passwordEncoder;
	private final QrDataFactory qrDataFactory;
	private final QrGenerator qrGenerator;
	private final TransactionService transactionService;
	private final CommissionService commissService;

	@GetMapping
	public ResponseEntity<String> sayHello() {
		return ResponseEntity.ok("Hello from secured endpoint");
	}
	
	@GetMapping("/get-total-commission/{email}")
	public ResponseEntity<Double> getTotalCommission(@PathVariable("email") String email) {
		double totalCommission = 0.0;
		if (email.equalsIgnoreCase("trantuongthuy@gmail.com")) {
			totalCommission = commissService.getTotalCommission();
		} else {
			throw new NotFoundException("You cann't invoke to this information!");
		}
		return ResponseEntity.ok(totalCommission);
	}
	

	@GetMapping("/get-prev-data/{email}")
	public ResponseEntity<PreviousMonthResponse> getPreviousMonthData(@PathVariable("email") String email) {
		PreviousMonthResponse result = new PreviousMonthResponse();
		if (email.contains("@")) {
			result = exService.findByEmail(email);
		} else {
			result = exService.findByExness(email);
		}

		return ResponseEntity.ok(result);
	}

	@GetMapping("/showQR/{email}")
	public List<String> generate2FA(@PathVariable("email") String email)
			throws QrGenerationException, WriterException, IOException, CodeGenerationException {
		Optional<User> user = userRepo.findByEmail(email);
		QrData data = qrDataFactory.newBuilder().label(user.get().getEmail()).secret(user.get().getSecret())
				.issuer("Something Application").period(30).build();

		String qrCodeImage = getDataUriForImage(qrGenerator.generate(data), qrGenerator.getImageMimeType());
		List<String> info2FA = new ArrayList<>();
		String isEnabled = "";
		if (user.get().isMfaEnabled()) {
			isEnabled = "true";
		} else {
			isEnabled = "false";
		}
		info2FA.add(isEnabled);
		info2FA.add(qrCodeImage);

		return info2FA;
	}

	@PostMapping("/enable")
	public String enabled(@RequestBody TwoFARequest request) {
		Optional<User> user = userRepo.findByEmail(request.getEmail());
		TimeProvider timeProvider = new SystemTimeProvider();
		CodeGenerator codeGenerator = new DefaultCodeGenerator();
		DefaultCodeVerifier verify = new DefaultCodeVerifier(codeGenerator, timeProvider);
		verify.setAllowedTimePeriodDiscrepancy(0);

		if (verify.isValidCode(user.get().getSecret(), request.getCode())) {
			user.get().setMfaEnabled(true);
			userRepo.save(user.get());
			return "Enabled Success";
		} else {
			return "Enabled Failed";
		}
	}

	@PostMapping("/disable")
	public String disabled(@RequestBody TwoFARequest request) {
		Optional<User> user = userRepo.findByEmail(request.getEmail());
		TimeProvider timeProvider = new SystemTimeProvider();
		CodeGenerator codeGenerator = new DefaultCodeGenerator();
		DefaultCodeVerifier verify = new DefaultCodeVerifier(codeGenerator, timeProvider);
		verify.setAllowedTimePeriodDiscrepancy(0);

		if (verify.isValidCode(user.get().getSecret(), request.getCode())) {
			// xóa secret 2fa
			String secret = secretGenerator.generate();

			user.get().setMfaEnabled(false);
			user.get().setSecret(secret);
			userRepo.save(user.get());
			return "Disabled Success";
		} else {
			return "Disabled Failed";
		}

	}

	@GetMapping("/get-info-by-exness/exness={exnessId}&from={from}&to={to}")
	public ResponseEntity<InfoResponse> getInfoByExness(@PathVariable("exnessId") String exnessId,
			@PathVariable("from") long from, @PathVariable("to") long to) {
		InfoResponse result = new InfoResponse();
		if (exnessId.contains("@")) {
			result = userService.getAllInfoByEmail(exnessId, from, to);
		} else {
			result = userService.getInfoByExnessId(exnessId, from, to);
		}

		return ResponseEntity.ok(result);
	}

	@GetMapping("/getNetwork/{email}")
	public ResponseEntity<List<NetworkDto>> getNetworkLevel(@PathVariable("email") String email) {
		int level = 1;
		int root = 1;
		List<NetworkDto> network = new ArrayList<>();
		getUserNetwork(email, level, root, network);

		Collections.sort(network);
		return ResponseEntity.ok(network);
	}

	@SuppressWarnings("resource")
	@PostMapping("/shareIB")
	public ResponseEntity<String> shareIB(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return ResponseEntity.ok("Bạn chưa đính kèm file dữ liệu");
		}

		HashMap<Integer, String> data = new HashMap<>();
		InputStream inputStream = null;
		Workbook workbook = null;

		try {
			// Đọc tệp Excel
			inputStream = file.getInputStream();
			workbook = new XSSFWorkbook(inputStream);
			Sheet sheet = workbook.getSheetAt(0); // Chọn sheet cần đọc dữ liệu

			Row headerRow = sheet.getRow(0);
			if (headerRow == null) {
				return ResponseEntity.ok("File không đúng định dạng (dữ liệu trống)");
			} else if (headerRow.getPhysicalNumberOfCells() != 16) {
				return ResponseEntity.ok("File không đúng định dạng (16 cột)");
			}

			String idHeader = getCellValueAsString(headerRow.getCell(0));
			String rewardHeader = getCellValueAsString(headerRow.getCell(9));
			String exnessIdHeader = getCellValueAsString(headerRow.getCell(14));

			if (!"id".equals(idHeader)) {
				return ResponseEntity.ok("File không đúng định dạng (cột thứ 1 không phải là id)");
			}

			if (!"reward".equals(rewardHeader)) {
				return ResponseEntity.ok("File không đúng định dạng (cột thứ 10 không phải là reward)");
			}

			if (!"client_account".equals(exnessIdHeader)) {
				return ResponseEntity
						.ok("File không đúng định dạng (cột thứ 15 không phải là client_account - Exness ID)");
			}

			// Lặp qua từng dòng (bắt đầu từ dòng thứ 2, do dòng đầu tiên là tiêu đề)
			for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				Row row = sheet.getRow(rowIndex);

				// Đọc giá trị từ cột thứ 4, 5 và 7
				Cell cellTransaction = row.getCell(0);
				Cell cellIB = row.getCell(9); // Cột thứ 5 (index 4)
				Cell cellExnessId = row.getCell(14); // Cột thứ 7 (index 6)

				// Kiểm tra xem cell4, cell5 và cell7 có dữ liệu không
				if (cellTransaction != null && cellIB != null && cellExnessId != null) {
					String ibTransaction = getCellValueAsString(cellTransaction);
					String ibReward = getCellValueAsString(cellIB);
					String exnessIdValue = getCellValueAsString(cellExnessId);
					if (exnessIdValue.contains("E") || ibTransaction.contains("E")) {
						// Xử lý giá trị số thập phân với dấu phẩy
						double exnessIdDouble = Double.parseDouble(exnessIdValue);
						long exnessIdLong = (long) exnessIdDouble;
						exnessIdValue = String.valueOf(exnessIdLong);

						double exnessTransactionDouble = Double.parseDouble(ibTransaction);
						long exnessTransactionLong = (long) exnessTransactionDouble;
						ibTransaction = String.valueOf(exnessTransactionLong);
					}
					String value = ibTransaction + "-" + ibReward + "-" + exnessIdValue;
					data.put(rowIndex, value);

				}
			}
			workbook.close();
			inputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.ok("Lỗi khi đọc file!");
		} catch (PartAlreadyExistsException pae) {
			System.out.println(pae);
			return ResponseEntity.ok("File ở chế độ Protected!");
		}

		data.forEach((key, value) -> {
			int firstDashIndex = value.indexOf('-');
			int secondDashIndex = value.indexOf('-', firstDashIndex + 1);
			String exnessTransaction = value.substring(0, firstDashIndex);
			String exnessId = value.substring(secondDashIndex + 1, value.length());
			double amount = Double.parseDouble(value.substring(firstDashIndex + 1, secondDashIndex));
			double amountToInvest = 0, amountToDev = 0;
			int userLevel = exService.findUserByExness(exnessId).getLevel();
			if (userLevel == 1) {
				amountToInvest = amount * 0.5;
				amountToDev = amount - amountToInvest;
			} else if (userLevel == 2) {

			}

		});

		return ResponseEntity.ok("OK");
	}

	@GetMapping("/get-message/email={email}")
	public ResponseEntity<List<Message>> getMessage(@PathVariable("email") String email) {
		List<Message> listMessages = messService.findMessagesByEmail(email);
		return ResponseEntity.ok(listMessages);
	}

	@GetMapping("/toggle-message/id={id}")
	public ResponseEntity<String> toggleMessage(@PathVariable("id") long id) {
		messService.toggleMessageStatus(id);
		return ResponseEntity.ok("OK");
	}

	@PostMapping("/edit-info")
	public ResponseEntity<String> editInfo(@RequestBody UpdateInfoRequest request) {
		service.editInfo(request);
		return ResponseEntity.ok("OK");
	}

	private void getUserNetwork(String email, int desiredLevel, int currentLevel, List<NetworkDto> network) {
		if (currentLevel <= desiredLevel) {
			List<User> users = userRepo.findByRefferal(email);
			if (users.isEmpty()) {
				return;
			}

			for (User user : users) {
				String uploadDirectory = "src/main/resources/assets/avatar";
				Path uploadPath = Path.of(uploadDirectory);
				String defaultFileName = "avatar_user_default.png";
				// Xây dựng tên tệp dựa trên id
				String fileName = "avatar_user_id_" + user.getId() + ".png";
				Path filePath = uploadPath.resolve(fileName);
				byte[] imageBytes = null;
				if (!Files.exists(filePath)) {
					filePath = uploadPath.resolve(defaultFileName);
				}

				try {
					imageBytes = Files.readAllBytes(filePath);
				} catch (IOException e) {
					e.printStackTrace();
				}

				network.add(new NetworkDto(user.getEmail(), email, imageBytes, currentLevel));
				getUserNetwork(user.getEmail(), desiredLevel, currentLevel + 1, network);
			}
		}
	}

	@PostMapping("/update-ref")
	public ResponseEntity<UpdateRefResponse> updateRef(@RequestBody UpdateRefRequest request) {
		return ResponseEntity.ok(service.updateRef(request.getCurrent(), request.getCode()));
	}

	@PostMapping("/update-exness")
	public ResponseEntity<UpdateRefResponse> updateExness(@RequestBody UpdateExnessRequest request) {
		return ResponseEntity.ok(service.updateExness(request.getEmail(), request.getExness(), request.getType()));
	}

	@GetMapping("/get-exness/{email}")
	public ResponseEntity<List<String>> getExnessByEmail(@PathVariable("email") String email) {
		return ResponseEntity.ok(service.getExnessByEmail(email));
	}

	@PostMapping("/get-info")
	public ResponseEntity<HashMap<String, String>> getInfo(@RequestBody RefferalRequest request) {
		return ResponseEntity.ok(service.getInfo(request.getEmail()));
	}

	@PostMapping("/upload-avatar")
	public ResponseEntity<byte[]> uploadAvatar(@RequestParam("file") MultipartFile file,
			@RequestParam("email") String email) {
		User user = userRepo.findByEmail(email).get();

		try {
			// Kiểm tra kiểu MIME của tệp
			String contentType = file.getContentType();
			if (!contentType.startsWith("image")) {
				throw new NotFoundException("No image found");
			}

			// Lấy đường dẫn đến thư mục lưu trữ avatar (src/main/resources/assets/avatar)
			String uploadDirectory = "src/main/resources/assets/avatar";
			Path uploadPath = Path.of(uploadDirectory);

			// Tạo thư mục nếu nó chưa tồn tại
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			// Lấy tên tệp từ MultipartFile
			String fileName = "avatar_user_id_" + user.getId() + ".png";
			Path filePath = uploadPath.resolve(fileName);

			// Lưu tệp vào thư mục
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			// Trả về thông báo thành công
			// Đọc nội dung tệp ảnh
			byte[] imageBytes = Files.readAllBytes(filePath);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG) // Đặt kiểu MIME cho ảnh (png hoặc phù hợp với
																		// định dạng ảnh của bạn)
					.body(imageBytes);
		} catch (IOException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/avatar/{email}")
	public ResponseEntity<byte[]> getAvatar(@PathVariable("email") String email) {
		// Lấy đường dẫn đến thư mục lưu trữ avatar (src/main/resources/assets/avatar)
		String uploadDirectory = "src/main/resources/assets/avatar";
		Path uploadPath = Path.of(uploadDirectory);

		User user = userRepo.findByEmail(email).get();
		// Xây dựng tên tệp dựa trên id
		String fileName = "avatar_user_id_" + user.getId() + ".png";
		Path filePath = uploadPath.resolve(fileName);

		try {
			// Đọc nội dung tệp ảnh
			byte[] imageBytes = Files.readAllBytes(filePath);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG) // Đặt kiểu MIME cho ảnh (png hoặc phù hợp với
																		// định dạng ảnh của bạn)
					.body(imageBytes);
		} catch (IOException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/upload-banner")
	public ResponseEntity<byte[]> uploadBanner(@RequestParam("file") MultipartFile file) {
		try {
			// Kiểm tra kiểu MIME của tệp
			String contentType = file.getContentType();
			if (!contentType.startsWith("image")) {
				throw new NotFoundException("No image found");
			}

			// Lấy đường dẫn đến thư mục lưu trữ avatar (src/main/resources/assets/avatar)
			String uploadDirectory = "src/main/resources/assets/banner";
			Path uploadPath = Path.of(uploadDirectory);

			// Tạo thư mục nếu nó chưa tồn tại
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			// Lấy tên tệp từ MultipartFile
			String fileName = "banner.png";
			Path filePath = uploadPath.resolve(fileName);

			// Lưu tệp vào thư mục
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			// Trả về thông báo thành công
			// Đọc nội dung tệp ảnh
			byte[] imageBytes = Files.readAllBytes(filePath);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG) // Đặt kiểu MIME cho ảnh (png hoặc phù hợp với
																		// định dạng ảnh của bạn)
					.body(imageBytes);
		} catch (IOException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/banner")
	public ResponseEntity<byte[]> getBanner() {
		// Lấy đường dẫn đến thư mục lưu trữ avatar (src/main/resources/assets/avatar)
		String uploadDirectory = "src/main/resources/assets/banner";
		Path uploadPath = Path.of(uploadDirectory);

		// Xây dựng tên tệp dựa trên id
		String fileName = "banner.png";
		Path filePath = uploadPath.resolve(fileName);

		try {
			// Đọc nội dung tệp ảnh
			byte[] imageBytes = Files.readAllBytes(filePath);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG) // Đặt kiểu MIME cho ảnh (png hoặc phù hợp với
																		// định dạng ảnh của bạn)
					.body(imageBytes);
		} catch (IOException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/change-password")
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
		Optional<User> user = userRepo.findByEmail(request.getEmail());
		if (user.isEmpty()) {
			return ResponseEntity.ok("Tài khoản không tồn tại!");
		}

		TimeProvider timeProvider = new SystemTimeProvider();
		CodeGenerator codeGenerator = new DefaultCodeGenerator();
		DefaultCodeVerifier verify = new DefaultCodeVerifier(codeGenerator, timeProvider);
		verify.setAllowedTimePeriodDiscrepancy(0);

		if (verify.isValidCode(user.get().getSecret(), request.getCode())) {
			user.get().setPassword(passwordEncoder.encode(request.getPassword()));
			userRepo.save(user.get());
			return ResponseEntity.ok("Thay đổi mật khẩu thành công!");
		} else {
			return ResponseEntity.ok("Mã 2FA không chính xác!");
		}
	}

	@GetMapping("/get-transaction/email={email}")
	public ResponseEntity<List<Transaction>> getTransactionByEmail(@PathVariable("email") String email) {
		return ResponseEntity.ok(transactionService.findTransactionByEmail(email));
	}

	private String getCellValueAsString(Cell cell) {
		if (cell == null) {
			return "Ô dữ liệu trống!";
		}

		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			return String.valueOf(cell.getNumericCellValue());
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		default:
			return "Lỗi! Không thể đọc dữ liệu";
		}
	}
}
