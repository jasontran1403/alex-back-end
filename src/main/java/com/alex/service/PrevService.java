package com.alex.service;

import com.alex.dto.PrevRequest;
import com.alex.dto.PreviousMonthResponse;
import com.alex.user.Prev;

public interface PrevService {
	PreviousMonthResponse findPrevByEmail(String email);
	void updatePrev(PrevRequest request);
	Prev findByExnessId(String exnessid);
	Prev initPrev(String email);
	void updatePrevData(String exnessId, double balance, double profit, double deposit, double withdraw);
}
