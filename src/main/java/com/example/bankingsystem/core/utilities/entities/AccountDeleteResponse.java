package com.example.bankingsystem.core.utilities.entities;

public class AccountDeleteResponse {
	public String message;

	public AccountDeleteResponse(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}	
}
