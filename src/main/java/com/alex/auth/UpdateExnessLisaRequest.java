package com.alex.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateExnessLisaRequest {
	private String email;
	private String exness;
	private String server;
	private String passview;
	private String password;
	private int type;
}
