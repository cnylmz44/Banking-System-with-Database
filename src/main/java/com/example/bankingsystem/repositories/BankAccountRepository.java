package com.example.bankingsystem.repositories;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

import com.example.bankingsystem.core.utilities.entities.AccountLogResponse;
import com.example.bankingsystem.entities.BankAccount;

@Mapper
public interface BankAccountRepository {
	public BankAccount findByNumber(String number);

	public void createAccount(BankAccount bankAccount);

	public void updateBalance(BankAccount bankAccount);

	public void deleteAccount(BankAccount bankAccount);

	public void createBankAccountLog(String message);

	public ArrayList<AccountLogResponse> findLogsByNumber(String id);
}
