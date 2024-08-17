package com.alex.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer>{
	@Query(value = "select * from order_detail where exness_id = ?1 and symbol = ?2 order by server_time desc limit 1", nativeQuery = true)
	Optional<OrderDetail> findLastestOrderByExnessIdAndSymbol(String exnessId, String symbol);
	
	@Query(value = "delete from order_detail where server_time <= ?1", nativeQuery = true)
	void cleanOldOrders(long time);
}
