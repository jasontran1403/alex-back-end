package com.alex.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RealtimeDto {
	private String exnessId;
	private String currencyName;
	private double totalEquity;
	private double currencyEquity;
	private String currentCandle;
	private double lot;
	private double currentBalance;
	private long lastestUpdated;
	private double initLot;
	private double initSpread;
	private int magic1;
	private int magic2;
	private int isActived;
	private int isRunning;
	private double lotBuy;
	private double lotSell;
	private double largestLotBuy;
	private double largestLotSell;
	private double lotBuyDefault = 0;
	private double lotSellDefault = 0;
	private String version;
	private double newMagic;
	private double hedgMagic;
	private int currentMagicBuy;
	private int currentMagicSell;
	private String oldMagics;
}
