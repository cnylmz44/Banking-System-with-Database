package com.example.bankingsystem.core.utilities.entities;

public class AccountCreateRequest {
	private String name;
	private String surname;
	private String email;
	private String tc;
	private String type;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isTypeValid(String Type) {
		if (this.type.equals("TRY") || this.type.equals("USD") || this.type.equals("GAU"))
			return true;
		else
			return false;
	}

	public boolean isEmailAddressValid(String email) {
		String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
		java.util.regex.Matcher m = p.matcher(email);
		return m.matches();
	}

	/*
	 * TR Identity Number actually consists of 9 digits, the last 2 digits have been
	 * added for control/verification purposes. The ID number cannot start with 0.
	 * Using the first 9 digits, the 10th digit is obtained, and using the first 10
	 * digits, the 11th digit is obtained. Add the digits in the 1st, 3rd, 5th, 7th
	 * and 9th digits, multiply by 7 and subtract the sum of the digits in the 2nd,
	 * 4th, 6th and 8th digits. The units digit of the result obtained (mod 10)
	 * gives the 10th digit of the ID number. When we add the first 9 digits of the
	 * ID number and the 10th digit obtained by the above method, the ones digit
	 * (mod 10) gives the 11th digit.
	 */
	public boolean isTCIdentityNumberValid(String tc) {
		//
		boolean isValid = false;
		if (tc != null && tc.length() == 11 && isInt(tc)) {
			long aTcNo, bTcNo, tcNo;
			long n1, n2, n3, n4, n5, n6, n7, n8, n9, control1, control2;
			tcNo = Long.parseLong(tc);
			aTcNo = tcNo / 100;
			bTcNo = tcNo / 100;
			n1 = aTcNo % 10;
			aTcNo = aTcNo / 10;
			n2 = aTcNo % 10;
			aTcNo = aTcNo / 10;
			n3 = aTcNo % 10;
			aTcNo = aTcNo / 10;
			n4 = aTcNo % 10;
			aTcNo = aTcNo / 10;
			n5 = aTcNo % 10;
			aTcNo = aTcNo / 10;
			n6 = aTcNo % 10;
			aTcNo = aTcNo / 10;
			n7 = aTcNo % 10;
			aTcNo = aTcNo / 10;
			n8 = aTcNo % 10;
			aTcNo = aTcNo / 10;
			n9 = aTcNo % 10;
			control1 = ((10 - ((((n1 + n3 + n5 + n7 + n9) * 3) + (n2 + n4 + n6 + n8)) % 10)) % 10);
			control2 = ((10 - (((((n2 + n4 + n6 + n8) + control1) * 3) + (n1 + n3 + n5 + n7 + n9)) % 10)) % 10);
			isValid = ((bTcNo * 100) + (control1 * 10) + control2 == tcNo);
		}
		return isValid;
	}

	private static boolean isInt(String s) // assuming integer is in decimal number system
	{
		for (int a = 0; a < s.length(); a++) {
			if (a == 0 && s.charAt(a) == '-')
				continue;
			if (!Character.isDigit(s.charAt(a)))
				return false;
		}
		return true;
	}
}
