package com.hariyali;

import java.util.Random;

public interface EnumConstants {
	
	Random r = new Random();
	int random = r.nextInt(99)+10;
	String donor=Integer.toString(random);
	String resetPasswordLink = "http://localhost:3000/resetPassword?token=";
	
	public static final String PAYMENT_COMPLETED= "Completed";
	
	public static final String PAYMENT_PENDING= "Pending";
	
	public static final String PAYMENT_FAILED= "Failed";

	public static final String LINK_URL="http://localhost:3000";
	
	public static final String SUCCESS="Success";
	
	public static final String PASSWORD="&%$#@123";
	
	public static final String ERROR="Error";
	
	public static final String Bulletin_DOCUMENTS = "C:\\EcatelogueApp\\Bulletins";
	
	public static final String donorId="0001"+donor+"D";
	
	public static final String certificateNumber ="Cert123"+donor;
	
	public static final String receiptNumber = "txn_23"+donor;
	
	public static final String GIFTING_MSG_BODY = "Dear %s\r\n,Welcome to the Hariyali family!\r\n" + "\r\n"
			+ "\tOn this happy occasion, your well-wisher, %s has gifted you a Hariyali sponsorship.  The main objective of the project is to do 5 Billion Tree Plantation from 2026 in several parts of the Nation."
			+ "The Tree Plantation is the main Agenda of the Project." + "\r\n" + "Best wishes,\r\n"
			+ "Team Hariyali\r\n" ;
	public static final String GIFTING_MSG_SUBJECT = "Someone wanted you to have this special gift";
	
	
	
}
