package com.alex.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DailyDataRepository extends JpaRepository<DailyData, String>{
	@Query(value="select * from daily_data where exness_id = ?1", nativeQuery = true)
	Optional<DailyData> findByExnessId(String exnessId);

}
