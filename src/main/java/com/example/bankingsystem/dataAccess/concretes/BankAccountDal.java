package com.example.bankingsystem.dataAccess.concretes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.sound.sampled.Line;

import org.apache.tomcat.jni.OS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.example.bankingsystem.core.utilities.entities.AccountCreateRequest;
import com.example.bankingsystem.core.utilities.entities.AccountCreateResponse;
import com.example.bankingsystem.core.utilities.entities.AccountLogResponse;
import com.example.bankingsystem.dataAccess.abstracts.BankAccountDao;
import com.example.bankingsystem.entities.BankAccount;

@Component
public class BankAccountDal implements BankAccountDao{

	private String filePathString = "/home/canyilmaz/eclipse-workspace/";
	private BankAccount bankAccount;
	
	@Override
	public BankAccount createAccount(AccountCreateRequest request) {
		String accountNumber = generateBankAccountNumber(10); 
		bankAccount = new BankAccount(accountNumber, request.getName(), request.getSurname(), request.getEmail(), request.getTc(), 0, request.getType());
		return bankAccount;
	}
	
	public String generateBankAccountNumber(int digitNumber) {
		String generatedNumber = "";
		Random random = new Random();
		
		//Generate every digits randomly
		for(int i=0;i<digitNumber;i++) {
			generatedNumber = generatedNumber + Integer.toString(random.nextInt(10)); 
		}
		
		//Check if generated account number is exist
		if (readBankAccountFromFile(generatedNumber) != null ) return generateBankAccountNumber(digitNumber);
		else return generatedNumber;
	}
	
	public String saveBankAccount2File(BankAccount bankAccount) {
		try {
			File file = new File(filePathString + bankAccount.getNumber());
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			//Save Bank Account Object to File
			objectOutputStream.writeObject(bankAccount);
			objectOutputStream.close();
			return bankAccount.getNumber();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public BankAccount readBankAccountFromFile(String accountNumber) {
		try {
			File file = new File(filePathString + accountNumber);
			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			//Read Bank Account from File
			bankAccount = (BankAccount) objectInputStream.readObject();
			objectInputStream.close();
			return bankAccount;
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<AccountLogResponse> getAccountLogs(String id){
		try {
			File file = new File(filePathString + "logs.txt");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			ArrayList<AccountLogResponse> logs = new ArrayList<>();
			while((line = bufferedReader.readLine())!=null) {
				String[] parts = line.split(" ");
				//Get Records Contains ID
				if(Arrays.asList(parts).contains(id)) {
					if(parts[1].equals("deposit")) logs.add(new AccountLogResponse(parts[0] + " nolu hesaba " + parts[3] + " yatırılmıştır."));
					else if(parts[1].equals("transfer")) logs.add(new AccountLogResponse(parts[0] + " nolu hesaptan " + parts[5] + " hesaba " + parts[3] + " transfer edilmiştir."));
					else if(parts[1].equals("delete")) logs.add(new AccountLogResponse(parts[0] +" nolu hesap başarıyla silinmiştir"));
				}
			}
			bufferedReader.close();
			fileReader.close();
			return logs;
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
	}
	
	public BankAccount depositMoney2BankAccount(BankAccount bankAccount, int depositedAmount) {
		bankAccount.setBalance(bankAccount.getBalance() + depositedAmount);
		return bankAccount;
	}
	
	public BankAccount withdrawMoneyFromBankAccount(BankAccount bankAccount, int withdrawedAmount) {
		bankAccount.setBalance(bankAccount.getBalance() - withdrawedAmount);
		return bankAccount;
	}
	
	public boolean isBalanceSufficientForWithdraw(BankAccount bankAccount, int withdrawRequest) {
		if(bankAccount.getBalance() >= withdrawRequest) return true;
		else return false;
	}

	@Override
	public boolean deleteBankAccount(String id) {
		try {
			File file = new File(filePathString + id);
			if (file.delete())	return true;
			else return false;
		} catch (Exception e) {
			return false;
		}
	}

}
