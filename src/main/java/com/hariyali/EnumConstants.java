package com.hariyali;

import java.util.Random;

public interface EnumConstants {

	Random r = new Random();
	int random = r.nextInt(99) + 10;
	String donor = Integer.toString(random);
	String resetPasswordLink = "http://localhost:3000/resetPassword?token=";

	public static final String PAYMENT_COMPLETED = "Completed";

	public static final String PAYMENT_PENDING = "Pending";

	public static final String PAYMENT_FAILED = "Failed";

	public static final String LINK_URL = "http://localhost:3000";

	public static final String SUCCESS = "Success";

	public static final String PASSWORD = "&%$#@123";

	public static final String ERROR = "Error";

	public static final String Bulletin_DOCUMENTS = "C:\\EcatelogueApp\\Bulletins";

	public static final String donorId = "0001" + donor + "D";

	public static final String certificateNumber = "Cert123" + donor;

	public static final String receiptNumber = "txn_23" + donor;

	public static final String GIFTING_MSG_BODY = "Dear Sponsor,\n" +
            "\t\t Welcome to Project “Hariyali”.\n" +
            "On the occasion of your %s, you have been gifted with Hariyali sponsorship to make your day special.\n" +
            "The gift certificate is attached herewith.\n" +
            " \tYou can login to your account on our website www.hariyali.org.in with your login details.\n" +
            "Email ID : %s\n" +
            "Password : %s\n" +
            "\n" +
            "Mahindra Foundation\n" +
            "Sheetal Mehta\n" +
            "Trustee & Executive Director\n" +
            "K.C. Mahindra Education Trust,\n" +
            "3rd Floor, Cecil Court,\n" +
            "Near Regal Cinema,\n" +
            "Mahakavi Bushan Marg,\n" +
            "Mumbai 400001\n" +
            "PS : Contact support@hariyali.org.in in case of any query.";

	public static final String GIFTING_MSG_SUBJECT = "Someone wanted you to have this special gift";
	
	
	String subject = "Welcome To Hariyali";
	String content = "Dear Sponsor,\n\tWelcome to Project “Hariyali”.\n"
            + "The Mahindra Foundation and Naandi Foundation would like to thank you for your donation.\n"
            + "You can login to your account on our website www.hariyali.org.in with your login details.\n"
            + "Email ID : %s\n"
            + "Password : %s\n"
            + "\tThanking you for your support to Project Hariyali.\n"
            + "Mahindra Foundation\n"
            + "Sheetal Mehta\n"
            + "Trustee & Executive Director\n"
            + "K.C. Mahindra Education Trust,\n"
            + "3rd Floor, Cecil Court,\n"
            + "Near Regal Cinema,\n"
            + "Mahakavi Bushan Marg,\n"
            + "Mumbai 400001\n"
            + "PS : Contact support@hariyali.org.in in case of any query.";
	

}
