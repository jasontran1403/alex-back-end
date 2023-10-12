package com.alex.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{
	@Query(value="select * from transaction where exness_id = ?1 order by time desc", nativeQuery = true)
	List<Transaction> findTransactionByExnessId(String exness);

}
