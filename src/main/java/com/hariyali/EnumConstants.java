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

	public static final String GIFTING_MSG_BODY = "Dear Sponsor,<br><p>&nbsp;Welcome to Project &ldquo;Hariyali&rdquo;.<br>"
			+ "&nbsp;On the occasion of your %s, you have been gifted with Hariyali sponsorship to make your day special.<br>"
			+ "The gift certificate is attached herewith.<br></p>"
			+ "&nbsp;You can login to your account on our website www.hariyali.org.in with your login details.<br>"
			+ "Email ID : %s<br>" + "Password : %s<br>" + "<br>" + "Mahindra Foundation<br>" + "Sheetal Mehta<br>"
			+ "Trustee & Executive Director<br>" + "K.C. Mahindra Education Trust,<br>" + "3rd Floor, Cecil Court,<br>"
			+ "Near Regal Cinema,<br>" + "Mahakavi Bushan Marg,<br>" + "Mumbai 400001<br>"
			+ "PS : Contact support@hariyali.org.in in case of any query.";

	public static final String GIFTING_MSG_SUBJECT = "Someone wanted you to have this special gift";

	String subject = "Welcome To Hariyali";
	String content="<p>Dear Sponsor,</p>" +
			"<p>&nbsp;&nbsp;Welcome to Project \"Hariyali\".</p>" +
			"<p>&nbsp;The Mahindra Foundation and Naandi Foundation would like to thank you for your donation.You can login to your account on our website "
			+"<a href='http://www.hariyali.org.in'>www.hariyali.org.in</a> with your login details.</p>"
			+"<p>Email ID :%s<br>"
			+"Password :%s</p>"
			+"<p>&nbsp;Thanking you for your support to Project Hariyali.</p>"
			+"<p>PS : Contact <a href='mailto:support@hariyali.org.in'>support@hariyali.org.in</a> in case of any query.</p>";

}
