package com.alex.service.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alex.dto.DailyDataDto;
import com.alex.dto.DailyDataRequest;
import com.alex.exception.NotFoundException;
import com.alex.service.DailyDataService;
import com.alex.user.DailyData;
import com.alex.user.DailyDataRepository;
import com.alex.user.ExnessRepository;

@Service
public class DailyDataServiceImpl implements DailyDataService{
	@Autowired
	DailyDataRepository dailyDataRepo;
	
	@Autowired
	ExnessRepository exRepo;

	@Override
	public DailyData findByExnessId(String exnessId) {
		// TODO Auto-generated method stub
		Optional<DailyData> result = dailyDataRepo.findByExnessId(exnessId);
		if (result.isPresent()) {
			return result.get();
		} else {
			throw new NotFoundException("This exness id " + exnessId + " is not existed!");
		}
	}

	@Override
	public void setDailyDataStatus(DailyDataRequest request) {
	    for (DailyDataDto item : request.getListDailyData()) {
	        Optional<DailyData> result = dailyDataRepo.findByExnessId(item.getExnessId());
	        
	        if (result.isPresent()) {
	            DailyData existingData = result.get();
	            existingData.setTodayHasData(item.isTodayHasData());
	            dailyDataRepo.save(existingData);
	        } else {
	            DailyData newDailyData = new DailyData();
	            newDailyData.setExnessId(item.getExnessId());
	            newDailyData.setTodayHasData(item.isTodayHasData());
	            dailyDataRepo.save(newDailyData);
	        }
	    }
	}


}
