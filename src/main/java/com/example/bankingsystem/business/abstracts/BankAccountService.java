package com.example.bankingsystem.business.abstracts;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.bankingsystem.core.utilities.entities.AccountCreateRequest;
import com.example.bankingsystem.core.utilities.entities.AccountCreateResponse;
import com.example.bankingsystem.core.utilities.entities.AccountDeleteResponse;
import com.example.bankingsystem.core.utilities.entities.AccountDepositMoneyRequest;
import com.example.bankingsystem.core.utilities.entities.AccountLogResponse;
import com.example.bankingsystem.core.utilities.entities.AccountMoneyTransferRequest;
import com.example.bankingsystem.core.utilities.entities.AccountMoneyTransferResponse;
import com.example.bankingsystem.entities.BankAccount;

@Service
public interface BankAccountService {

	ResponseEntity<AccountCreateResponse> createAccount(AccountCreateRequest request);

	ResponseEntity<BankAccount> getBankAccountDetails(String id);

	ResponseEntity<BankAccount> depositMoney2BankAccount(String id, AccountDepositMoneyRequest depositedMoney);

	ResponseEntity<AccountMoneyTransferResponse> moneyTransferFromBankAccount(String id,
			AccountMoneyTransferRequest amount);

	ResponseEntity<ArrayList<AccountLogResponse>> getAccountLogs(String id);

	ResponseEntity<AccountDeleteResponse> deleteBankAccount(String id);
}
