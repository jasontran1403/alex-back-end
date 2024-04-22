package com.alex.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface Mq4DataRepository extends JpaRepository<Mq4Data, Long>{
	@Query(value="select * from mq4data where exness_id = ?1 and currency_name = ?2", nativeQuery=true)
	Optional<Mq4Data> findExistedData(String exnessId, String currencyName);
	
	@Query(value = "select MIN(lastest_updated) from mq4data", nativeQuery=true)
	long getLatestRealtimeData();
}
