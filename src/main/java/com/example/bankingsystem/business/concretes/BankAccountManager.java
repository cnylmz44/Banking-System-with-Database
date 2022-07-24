package com.example.bankingsystem.business.concretes;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.bankingsystem.business.abstracts.BankAccountService;
import com.example.bankingsystem.core.utilities.entities.AccountCreateErrorResponse;
import com.example.bankingsystem.core.utilities.entities.AccountCreateRequest;
import com.example.bankingsystem.core.utilities.entities.AccountCreateResponse;
import com.example.bankingsystem.core.utilities.entities.AccountCreateSuccessResponse;
import com.example.bankingsystem.core.utilities.entities.AccountDeleteResponse;
import com.example.bankingsystem.core.utilities.entities.AccountDepositMoneyRequest;
import com.example.bankingsystem.core.utilities.entities.AccountLogResponse;
import com.example.bankingsystem.core.utilities.entities.AccountMoneyTransferRequest;
import com.example.bankingsystem.core.utilities.entities.AccountMoneyTransferResponse;
import com.example.bankingsystem.core.utilities.exchange.CollectApiCurrencyExchange;
import com.example.bankingsystem.dataAccess.abstracts.BankAccountDao;
import com.example.bankingsystem.entities.BankAccount;

@Component
@EnableCaching
public class BankAccountManager implements BankAccountService {

	private BankAccountDao bankAccountDao;
	private KafkaTemplate<String, String> producer;
	private CollectApiCurrencyExchange exchanger;

	@Autowired
	public BankAccountManager(BankAccountDao bankAccountDao, KafkaTemplate<String, String> producer,
			CollectApiCurrencyExchange exchanger) {
		this.bankAccountDao = bankAccountDao;
		this.producer = producer;
		this.exchanger = exchanger;
	}

	@Override
	public ResponseEntity<AccountCreateResponse> createAccount(AccountCreateRequest request) {
		// Check E-Mail,Tc Identity Number and Currency Type
		if (!request.isEmailAddressValid(request.getEmail()))
			return new ResponseEntity<>(new AccountCreateErrorResponse("Invalid Mail Format!"), HttpStatus.BAD_REQUEST);
		else if (!request.isTCIdentityNumberValid(request.getTc()))
			return new ResponseEntity<>(new AccountCreateErrorResponse("Invalid TC Identity Format!"),
					HttpStatus.BAD_REQUEST);
		else if (!request.isTypeValid(request.getType()))
			return new ResponseEntity<>(new AccountCreateErrorResponse("Invalid Type!"), HttpStatus.BAD_REQUEST);
		else
			return new ResponseEntity<>(new AccountCreateSuccessResponse("Account is created succesfully!",
					bankAccountDao.saveBankAccount(bankAccountDao.createAccount(request))), HttpStatus.OK);
	}

	// Add Account to Cache
	@Cacheable(value = "'bank-account-'+#id", key = "'bank-account-'+#id")
	@Override
	public ResponseEntity<BankAccount> getBankAccountDetails(String id) {
		if (bankAccountDao.getBankAccount(id) == null)
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		else
			return ResponseEntity.ok().lastModified(bankAccountDao.getBankAccount(id).getLastModified())
					.body(bankAccountDao.getBankAccount(id));
	}

	// Remove Account From Cache
	@CacheEvict(value = "'bank-account-'+#id", key = "'bank-account-'+#id")
	@Override
	public ResponseEntity<BankAccount> depositMoney2BankAccount(String id, AccountDepositMoneyRequest depositedMoney) {
		if (bankAccountDao.getBankAccount(id) == null)
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		else {
			// Kafka Logging Producer
			producer.send("logs", id + " deposit amount: " + depositedMoney.getAmount()
					+ bankAccountDao.getBankAccount(id).getType());

			return ResponseEntity.ok().lastModified(bankAccountDao.getBankAccount(id).getLastModified())
					.body(bankAccountDao.getBankAccount(bankAccountDao.updateBalance(bankAccountDao
							.depositMoney2BankAccount(bankAccountDao.getBankAccount(id), depositedMoney.getAmount()))));
		}
	}

	// Remove Account and Transferred Account From Cache
	@Caching(evict = { @CacheEvict(value = "'bank-account-'+#id", key = "'bank-account-'+#id"),
			@CacheEvict(value = "'bank-account-'+#request.getTransferredAccountNumber()", key = "'bank-account-'+#request.getTransferredAccountNumber()") })
	@Override
	public ResponseEntity<AccountMoneyTransferResponse> moneyTransferFromBankAccount(String id,
			AccountMoneyTransferRequest request) {
		// Check Bank Accounts Exist
		if (bankAccountDao.getBankAccount(id) == null
				|| bankAccountDao.getBankAccount(request.getTransferredAccountNumber()) == null)
			return new ResponseEntity<>(new AccountMoneyTransferResponse("Account cannot be found!"),
					HttpStatus.NOT_FOUND);

		String withdrawedAccountType = bankAccountDao.getBankAccount(id).getType();
		String depositedAccountType = bankAccountDao.getBankAccount(request.getTransferredAccountNumber()).getType();

		// Withdrawed Balance Check
		if (bankAccountDao.isBalanceSufficientForWithdraw(bankAccountDao.getBankAccount(id), request.getAmount())) {
			int exchangedAmount, gramGoldPriceInTRY;
			// Exchange Check
			if (!withdrawedAccountType.equals(depositedAccountType)) {
				// Directly Gold Exchange is not possible
				// Currency to Gold
				if (depositedAccountType.equals("GAU")) {
					// Get Gold Selling Price
					gramGoldPriceInTRY = exchanger.getGramGoldPriceInTRY("selling");
					// If Both types are 'TRY', Response is Null
					if (!withdrawedAccountType.equals("TRY")) {
						exchangedAmount = exchanger.exchangeCurrencies(request.getAmount(), withdrawedAccountType,
								"TRY") / gramGoldPriceInTRY;
					} else
						exchangedAmount = request.getAmount() / gramGoldPriceInTRY;
				}
				// Gold to Currency
				else if (withdrawedAccountType.equals("GAU")) {
					// Get Gold Buying Price
					gramGoldPriceInTRY = exchanger.getGramGoldPriceInTRY("buying");
					// If Both types are 'TRY', Response is Null
					if (!depositedAccountType.equals("TRY"))
						exchangedAmount = exchanger.exchangeCurrencies(request.getAmount() * gramGoldPriceInTRY, "TRY",
								depositedAccountType);
					else
						exchangedAmount = request.getAmount() * gramGoldPriceInTRY;
				}
				// Currency Exchange
				else {
					exchangedAmount = exchanger.exchangeCurrencies(request.getAmount(), withdrawedAccountType,
							depositedAccountType);
				}
			} else {
				exchangedAmount = request.getAmount();
			}
			// Withdraw and Save
			bankAccountDao.updateBalance(bankAccountDao.withdrawMoneyFromBankAccount(bankAccountDao.getBankAccount(id),
					request.getAmount()));
			// Deposit and Save
			bankAccountDao.updateBalance(bankAccountDao.depositMoney2BankAccount(
					bankAccountDao.getBankAccount(request.getTransferredAccountNumber()), exchangedAmount));
			// Kafka Logging Producer
			producer.send("logs",
					id + " transfer amount: " + request.getAmount() + bankAccountDao.getBankAccount(id).getType()
							+ " transferred_account: " + request.getTransferredAccountNumber());

			return ResponseEntity.ok().lastModified(bankAccountDao.getBankAccount(id).getLastModified())
					.body(new AccountMoneyTransferResponse("Transferred succesfully!"));
		} else {
			return new ResponseEntity<>(new AccountMoneyTransferResponse("Insufficient balance!"),
					HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<ArrayList<AccountLogResponse>> getAccountLogs(String id) {
		return new ResponseEntity<>(bankAccountDao.getAccountLogs(id), HttpStatus.OK);
	}

	// Remove Account From Cache
	@CacheEvict(value = "'bank-account-'+#id", key = "'bank-account-'+#id")
	@Override
	public ResponseEntity<AccountDeleteResponse> deleteBankAccount(String id) {
		if (bankAccountDao.deleteBankAccount(bankAccountDao.getBankAccount(id))) {
			producer.send("logs", id + " delete");
			return ResponseEntity.ok().lastModified(bankAccountDao.getBankAccount(id).getLastModified())
					.body(new AccountDeleteResponse("Account is deleted succesfully!"));
		} else
			return new ResponseEntity<>(new AccountDeleteResponse("Account cannot be found!"), HttpStatus.NOT_FOUND);
	}

}
