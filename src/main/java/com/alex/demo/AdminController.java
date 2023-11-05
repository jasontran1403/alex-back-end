package com.alex.demo;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alex.dto.ExnessDto;
import com.alex.exception.NotFoundException;
import com.alex.user.User;
import com.alex.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
	private final UserRepository userRepo;
	

    @GetMapping
    public String get() {
        return "GET:: admin controller";
    }
    @PostMapping("/add-exness")
    public ResponseEntity<ExnessDto> post(@RequestBody ExnessDto request) {
    	Optional<User> user = userRepo.findByEmail(request.getEmail());
    	if (user.isEmpty()) {
    		throw new NotFoundException("This user" + request.getEmail() + " is not existed!");
    	}
        return ResponseEntity.ok(request);
    }
    @PostMapping("/update-exness")
    public ResponseEntity<ExnessDto> put(@RequestBody ExnessDto request) {
    	Optional<User> user = userRepo.findByEmail(request.getEmail());
    	if (user.isEmpty()) {
    		throw new NotFoundException("This user" + request.getEmail() + " is not existed!");
    	}
        return ResponseEntity.ok(request);
    }
    @DeleteMapping
    public String delete() {
        return "DELETE:: admin controller";
    }
}
