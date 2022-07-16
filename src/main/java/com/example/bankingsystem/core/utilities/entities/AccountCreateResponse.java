package com.example.bankingsystem.core.utilities.entities;

public class AccountCreateResponse {
	private String message;
	private String accountNumber;
	
	public AccountCreateResponse(String message) {
		this.message = message;
	}
	
	public AccountCreateResponse(String message, String accountNumber) {
		this(message);
		this.accountNumber = accountNumber;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

}
