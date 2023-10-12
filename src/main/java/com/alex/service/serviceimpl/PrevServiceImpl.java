package com.alex.service.serviceimpl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.alex.dto.PrevRequest;
import com.alex.dto.PreviousMonthResponse;
import com.alex.service.PrevService;
import com.alex.user.Exness;
import com.alex.user.ExnessRepository;
import com.alex.user.Prev;
import com.alex.user.PrevRepository;
import com.alex.user.User;
import com.alex.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrevServiceImpl implements PrevService{
	private final UserRepository userRepo;
	private final PrevRepository prevRepo;
	private final ExnessRepository exRepo;
	
	@Override
	public PreviousMonthResponse findPrevByEmail(String email) {
		// TODO Auto-generated method stub
		User user = userRepo.findByEmail(email).get();
		Optional<Prev> prev = prevRepo.getPrevByUser(user);
		if (prev.isEmpty()) {
			return new PreviousMonthResponse();
		}
		PreviousMonthResponse result = new PreviousMonthResponse();
		result.setBalance(prev.get().getBalance());
		result.setCommission(prev.get().getCommission());
		result.setTransaction(prev.get().getTransaction());
		return result;
	}

	@Override
	public void updatePrev(PrevRequest request) {
		// TODO Auto-generated method stub
		Prev prev = new Prev();
		User user = userRepo.findByEmail(request.getEmail()).get();
		prev.setUser(user);
		prev.setBalance(request.getBalance());
		prev.setCommission(request.getCommission());
		prev.setTransaction(request.getTransaction());
		prevRepo.save(prev);
		
	}

	@Override
	public Prev findByExnessId(String exnessid) {
		// TODO Auto-generated method stub
		Exness exness = exRepo.findByExness(exnessid).get();
		Prev prev = prevRepo.getPrevByUser(exness.getUser()).get();
		return prev;
	}

	@Override
	public Prev initPrev(String email) {
		// TODO Auto-generated method stub
		Prev prev = new Prev();
		User user = userRepo.findByEmail(email).get();
		prev.setUser(user);
		return prevRepo.save(prev);
	}

	@Override
	public void updatePrevData(String exnessId, double balance, double profit, double transaction) {
		// TODO Auto-generated method stub
		Exness exness = exRepo.findByExness(exnessId).get();
		Prev prev = prevRepo.getPrevByUser(exness.getUser()).get();
		prev.setBalance(balance);
		prev.setCommission(profit);
		prev.setTransaction(transaction);
		prevRepo.save(prev);
	}

}
