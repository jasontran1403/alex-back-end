package com.alex.auth;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alex.service.ExnessService;
import com.alex.service.PrevService;
import com.alex.service.TransactionService;
import com.alex.service.UserService;
import com.alex.user.Transaction;
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
	private final ExnessService exService;
	private final TransactionService tranService;
	
	@GetMapping("/transfer-transaction/exnessId={exnessId}&transaction={type}&amount={amount}&time={time}")
	public ResponseEntity<String> insertData(@PathVariable("exnessId") String exnessId, @PathVariable("type") int type,
			@PathVariable("amount") double amount, @PathVariable("time") long time) {
		System.out.println("ExnessId= " + exnessId + " type= " + type + " amount= " + amount + " time= " + time);
		String transactionType;
		if (type == 0) {
			transactionType = "Withdraw";
		} else {
			transactionType = "Deposit";
		}
		
		Transaction transaction = new Transaction();
		transaction.setExnessId(exnessId);
		transaction.setAmount(Math.abs(amount));
		transaction.setType(transactionType);
		transaction.setTime(time);
		tranService.saveTransaction(transaction);
		

		return ResponseEntity.ok("OK");
	}
	
	@GetMapping("/transfer-data/exnessId={exnessId}&balance={balance}&profit={profit}")
	public ResponseEntity<String> insertData(@PathVariable("exnessId") String exnessId, 
			@PathVariable("balance") double balance, @PathVariable("profit") double profit) {
		System.out.println("ExnessId= " + exnessId + " - Balance=" + balance + " - Profit=" + profit);

		long time = System.currentTimeMillis() / 1000;
		// 1) luu profit cua ngay truoc do
		userService.saveProfit(exnessId, profit, time);
		// 2) luu balance cua ngay truoc do
		userService.saveBalance(exnessId, balance, time);
		// 3) cap nhat balance
		userService.updateBalanceExness(exnessId, balance);
		// 4) cap nhat tong profit
		exService.updateTotalProfit(exnessId, profit);
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
