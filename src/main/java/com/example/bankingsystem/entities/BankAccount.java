package com.example.bankingsystem.entities;

import java.io.Serializable;

import org.apache.ibatis.type.Alias;

@Alias("BankAccount")
public class BankAccount implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String surname;
	private String email;
	private String tc;
	private int balance;
	private String type;
	private boolean isDeleted;
	private long lastModified;

	public BankAccount(String id, String name, String surname, String email, String tc, int balance, String type,
			boolean isDeleted, long lastModified) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.tc = tc;
		this.balance = balance;
		this.type = type;
		this.isDeleted = isDeleted;
		this.lastModified = lastModified;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

}
