package com.example.bankingsystem.core.utilities.entities;

public class AccountMoneyTransferRequest extends AccountDepositMoneyRequest {
	private String transferredAccountNumber;

	public String getTransferredAccountNumber() {
		return transferredAccountNumber;
	}

	public void setTransferredAccountNumber(String transferredAccountNumber) {
		this.transferredAccountNumber = transferredAccountNumber;
	}

}
