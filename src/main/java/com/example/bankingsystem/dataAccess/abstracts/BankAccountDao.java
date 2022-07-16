package com.example.bankingsystem.dataAccess.abstracts;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.bankingsystem.core.utilities.entities.AccountCreateRequest;
import com.example.bankingsystem.core.utilities.entities.AccountCreateResponse;
import com.example.bankingsystem.core.utilities.entities.AccountLogResponse;
import com.example.bankingsystem.entities.BankAccount;

@Service
public interface BankAccountDao {

	BankAccount createAccount(AccountCreateRequest request);
	String saveBankAccount2File(BankAccount bankAccount);
	BankAccount readBankAccountFromFile(String accountNumber);
	BankAccount depositMoney2BankAccount(BankAccount bankAccount, int depositedMoney); 
	BankAccount withdrawMoneyFromBankAccount(BankAccount bankAccount, int withdrawedAmount);
	boolean isBalanceSufficientForWithdraw(BankAccount bankAccount, int withdrawRequest);
	ArrayList<AccountLogResponse> getAccountLogs(String id);
	boolean deleteBankAccount(String id);
}
