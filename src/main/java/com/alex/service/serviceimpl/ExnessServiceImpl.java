package com.alex.service.serviceimpl;

import org.springframework.stereotype.Service;

import com.alex.service.ExnessService;
import com.alex.user.ExnessRepository;
import com.alex.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExnessServiceImpl implements ExnessService{
	private final ExnessRepository exRepo;

	@Override
	public User findUserByExness(String exnessId) {
		// TODO Auto-generated method stub
		return exRepo.findByExness(exnessId).get().getUser();
	}

}
