package com.alex.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exness")
public class Exness {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String exness;
	private String name;
	private String server;
	private String token;
	private String chatId;
	private String password;
	private String passview;
	private double balance;
	private double prevBalance;
	private int level;
	private String refferal;
	private boolean isSet;
	private double totalProfit;
	private boolean isActive;
	private double lot;
	@Column(columnDefinition="TEXT")
	private String reason;
	
	
	@Column(columnDefinition="TEXT")
	private String message;

	@ManyToOne(fetch = FetchType.EAGER)
	@JsonIgnore
	@JoinColumn(name = "user_id")
	public User user;
}
