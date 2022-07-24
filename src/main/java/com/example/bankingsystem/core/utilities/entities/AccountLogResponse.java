package com.example.bankingsystem.core.utilities.entities;

import org.apache.ibatis.type.Alias;

@Alias("AccountLogResponse")
public class AccountLogResponse {
	private String log;

	public AccountLogResponse(String log) {
		this.log = log;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}
}
