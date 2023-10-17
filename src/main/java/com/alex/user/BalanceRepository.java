package com.alex.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BalanceRepository extends JpaRepository<Balance, Long>{
	@Query(value="select * from balance where exness_id = ?1 order by time asc", nativeQuery = true)
	List<Balance> findByExness(String exness);
	
	@Query(value="select * from balance where exness_id = ?1 and time >= ?2 and time <= ?3 order by time asc", nativeQuery = true)
	List<Balance> findByExnessByTime(String exness, long from, long to);
}
