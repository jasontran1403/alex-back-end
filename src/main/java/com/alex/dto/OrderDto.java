package com.alex.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
	private int ticket;
	private String time;
	private String type;
	private double lot;
	private String symbol;
	private double openPrice;
	private double currentPrice;
	private double commission;
	private double swap;
	private double profit;
}
