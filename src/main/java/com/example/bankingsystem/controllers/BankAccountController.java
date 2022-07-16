package com.example.bankingsystem.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import com.example.bankingsystem.business.abstracts.BankAccountService;
import com.example.bankingsystem.core.utilities.entities.AccountCreateRequest;
import com.example.bankingsystem.core.utilities.entities.AccountCreateResponse;
import com.example.bankingsystem.core.utilities.entities.AccountDeleteResponse;
import com.example.bankingsystem.core.utilities.entities.AccountDepositMoneyRequest;
import com.example.bankingsystem.core.utilities.entities.AccountLogResponse;
import com.example.bankingsystem.core.utilities.entities.AccountMoneyTransferRequest;
import com.example.bankingsystem.core.utilities.entities.AccountMoneyTransferResponse;
import com.example.bankingsystem.entities.BankAccount;

@RestController
@RequestMapping("api/bankingsystem")
//@CrossOrigin(origins = {"http://localhost"})
public class BankAccountController {
	
	private BankAccountService bankAccountService;
	
	@Autowired
	public BankAccountController(BankAccountService bankAccountService) {
		this.bankAccountService = bankAccountService;
	}
	
	//Create Bank Account
    @RequestMapping(path = "/accounts", method = RequestMethod.POST)
    public ResponseEntity<AccountCreateResponse> createAccount(@RequestBody AccountCreateRequest request){
    	return bankAccountService.createAccount(request);
    }
    
    //Get Bank Account Details
    @RequestMapping(path = "/accounts/{id}", method = RequestMethod.GET)
    public ResponseEntity<BankAccount> getBankAccountDetails(@PathVariable(name = "id") String id){
    	return bankAccountService.getBankAccountDetails(id);
    }
    
    //Deposit to Bank Account
    @RequestMapping(path = "/accounts/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<BankAccount> depositMoney2BankAccount(@PathVariable(name = "id") String id, @RequestBody AccountDepositMoneyRequest amount ){
    	return bankAccountService.depositMoney2BankAccount(id,amount);
    }

    //Delete Bank Account
    @RequestMapping(path = "/accounts/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<AccountDeleteResponse> deleteBankAccount(@PathVariable(name = "id") String id){
    	return bankAccountService.deleteBankAccount(id);
    }    
    
    //Transfer From an Account to another Account
    @RequestMapping(path = "/accounts/{id}/transfer", method = RequestMethod.PATCH)
    public ResponseEntity<AccountMoneyTransferResponse> moneyTransferFromBankAccount(@PathVariable(name = "id") String id, @RequestBody AccountMoneyTransferRequest transferRequest ){
    	return bankAccountService.moneyTransferFromBankAccount(id,transferRequest);
    }
    
    //Get Logs of Bank Account
    @CrossOrigin(origins = {"http://localhost:6162"})
    @RequestMapping(path = "/accounts/{id}/activities", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<AccountLogResponse>> getAccountLogs(@PathVariable(name = "id") String id){
    	return bankAccountService.getAccountLogs(id);
    }
}
