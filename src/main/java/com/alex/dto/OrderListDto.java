package com.alex.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderListDto {
	private String exnessId;
	private String accountName;
	private String accountServer;
	private String symbol;
	private String time;
	private List<OrderDto> listOrders;
}
