package com.example.bankingsystem.dataAccess.concretes;

import java.util.ArrayList;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.bankingsystem.core.utilities.entities.AccountCreateRequest;
import com.example.bankingsystem.core.utilities.entities.AccountLogResponse;
import com.example.bankingsystem.dataAccess.abstracts.BankAccountDao;
import com.example.bankingsystem.entities.BankAccount;
import com.example.bankingsystem.repositories.BankAccountRepository;

@Component
public class BankAccountDal implements BankAccountDao {

	private BankAccount bankAccount;

	@Autowired
	private BankAccountRepository bankAccountRepository;

	@Override
	public BankAccount createAccount(AccountCreateRequest request) {
		String accountNumber = generateBankAccountNumber(10);
		// Create Account with Constructor
		bankAccount = new BankAccount(accountNumber, request.getName(), request.getSurname(), request.getEmail(),
				request.getTc(), 0, request.getType(), false, System.currentTimeMillis());

		return bankAccount;
	}

	public String generateBankAccountNumber(int digitNumber) {
		String generatedNumber = "";
		Random random = new Random();

		// Generate every digits randomly
		for (int i = 0; i < digitNumber; i++) {
			generatedNumber = generatedNumber + Integer.toString(random.nextInt(10));
		}

		// Check if generated account number is exist
		if (getBankAccount(generatedNumber) != null)
			return generateBankAccountNumber(digitNumber);
		else
			return generatedNumber;

	}

	// Save Account All Data
	public String saveBankAccount(BankAccount bankAccount) {
		try {
			this.bankAccountRepository.createAccount(bankAccount);
			return bankAccount.getId();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Update Bank Account Balance( Withdraw and Deposit )
	public String updateBalance(BankAccount bankAccount) {
		try {
			this.bankAccountRepository.updateBalance(bankAccount);
			return bankAccount.getId();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public BankAccount getBankAccount(String accountNumber) {
		return this.bankAccountRepository.findByNumber(accountNumber);
	}

	public ArrayList<AccountLogResponse> getAccountLogs(String id) {
		try {
			ArrayList<AccountLogResponse> logs = new ArrayList<>();
			for (AccountLogResponse log : bankAccountRepository.findLogsByNumber('%' + id + '%')) {
				String[] parts = log.getLog().split(" ");
				// Log Format
				if (parts[1].equals("deposit"))
					logs.add(new AccountLogResponse(parts[0] + " nolu hesaba " + parts[3] + " yatırılmıştır."));
				else if (parts[1].equals("transfer"))
					logs.add(new AccountLogResponse(
							parts[0] + " nolu hesaptan " + parts[5] + " hesaba " + parts[3] + " transfer edilmiştir."));
				else if (parts[1].equals("delete"))
					logs.add(new AccountLogResponse(parts[0] + " nolu hesap başarıyla silinmiştir"));
			}
			return logs;
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
	}

	// Deposit Operations
	public BankAccount depositMoney2BankAccount(BankAccount bankAccount, int depositedAmount) {
		bankAccount.setBalance(bankAccount.getBalance() + depositedAmount);
		bankAccount.setLastModified(System.currentTimeMillis());
		return bankAccount;
	}

	// Withdraw Operations
	public BankAccount withdrawMoneyFromBankAccount(BankAccount bankAccount, int withdrawedAmount) {
		bankAccount.setBalance(bankAccount.getBalance() - withdrawedAmount);
		bankAccount.setLastModified(System.currentTimeMillis());
		return bankAccount;
	}

	// Balance Check
	public boolean isBalanceSufficientForWithdraw(BankAccount bankAccount, int withdrawRequest) {
		if (bankAccount.getBalance() >= withdrawRequest)
			return true;
		else
			return false;
	}

	// Soft Delete Account
	@Override
	public boolean deleteBankAccount(BankAccount bankAccount) {
		try {
			bankAccount.setDeleted(true);
			bankAccount.setLastModified(System.currentTimeMillis());
			bankAccountRepository.deleteAccount(bankAccount);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
