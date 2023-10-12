package com.alex.auth;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alex.service.PrevService;
import com.alex.service.UserService;
import com.alex.user.UserRepository;

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
	private final PrevService prevService;
	private final UserService userService;
	
	@GetMapping("/insert-data/exnessId={exnessId}&profit={profit}")
	public ResponseEntity<String> insertData(@PathVariable("exnessId") String exnessId, @PathVariable("profit") double profit) {
		System.out.println("ExnessId= " + exnessId);
		System.out.println("Profit= " + profit);
		System.out.println("-------");
		return ResponseEntity.ok("OK");
	}
	
	@GetMapping("/transfer-data/exnessId={exnessId}&balance={balance}&profit={profit}&withdraw={withdraw}&deposit={deposit}")
	public ResponseEntity<String> insertData(@PathVariable("exnessId") String exnessId, 
			@PathVariable("balance") double balance, @PathVariable("profit") double profit, 
			@PathVariable("withdraw") double withdraw, @PathVariable("deposit") double deposit) {
		System.out.println("ExnessId= " + exnessId + " - Balance=" + balance + " - Profit=" + profit + " - Withdraw=" + withdraw + " - Deposit=" + deposit);
		long time = System.currentTimeMillis() / 1000;
		userService.saveProfit(exnessId, profit, time);
		userService.saveBalance(exnessId, balance, time);
		if (withdraw < 0) {
			userService.saveTransaction(exnessId, withdraw, time);
		}
		if (deposit > 0) {
			userService.saveTransaction(exnessId, deposit, time);
		}
		
		Date date = new Date();

        // Định dạng ngày trong tháng bằng cách sử dụng SimpleDateFormat
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd"); // "dd" đại diện cho ngày trong tháng

        // Lấy ngày trong tháng từ đối tượng Date
        String dayOfMonth = dateFormat.format(date);
        if (dayOfMonth.equals("01")) {
        	prevService.updatePrevData(exnessId, balance, profit, deposit-withdraw);
        }
		
		
		return ResponseEntity.ok("OK");
	}
	
	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
		return ResponseEntity.ok(service.register(request));
	}

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
		return ResponseEntity.ok(service.authenticate(request));
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
}
