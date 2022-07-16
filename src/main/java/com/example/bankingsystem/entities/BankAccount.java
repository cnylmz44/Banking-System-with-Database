package com.example.bankingsystem.entities;

import java.io.Serializable;

public class BankAccount implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String number;
	private String name;
	private String surname;
	private String email;
	private String tc;
	private int balance;
	private String type;
	
	public BankAccount(String number, String name, String surname, String email, String tc, int balance, String type) {
		this.number = number;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.tc = tc;
		this.balance = balance;
		this.type = type;
	}
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTc() {
		return tc;
	}
	public void setTc(String tc) {
		this.tc = tc;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String toFileFormat() {
		return this.number + "----"
				+ this.name + "----"
				+ this.surname + "----"
				+ this.email + "----"
				+ this.tc + "----"
				+ this.balance + "----"
				+ this.type;
	}
}
