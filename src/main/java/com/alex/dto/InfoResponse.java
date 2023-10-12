package com.alex.dto;

import java.util.List;

import com.alex.user.Balance;
import com.alex.user.Commission;
import com.alex.user.Profit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfoResponse {
	private double profit;
	private double commission;
	private List<Profit> profits;
	private List<Commission> commissions;
	private List<Balance> balances;
}
