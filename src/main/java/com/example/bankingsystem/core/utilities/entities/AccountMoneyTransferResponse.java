package com.example.bankingsystem.core.utilities.entities;

public class AccountMoneyTransferResponse {
	private String message;

	public AccountMoneyTransferResponse(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
