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

	public static final String GIFTING_MSG_BODY ="<p>Dear Sponsor,</p>" +"<p>Welcome to Project \"Hariyali\".</p>"
			+ "<p>On the occasion of your %s, you have been gifted with Hariyali sponsorship to make your day special.<br>"
			+ "The gift certificate is attached herewith.<br></p>"
			+ "You can login to your account on our website www.hariyali.org.in with your login details.<br>"
			+ "Email ID : %s<br>" + "Password : %s<br>" + "<br>" + "Mahindra Foundation<br>" + "Sheetal Mehta<br>"
			+ "Trustee & Executive Director<br>" + "K.C. Mahindra Education Trust,<br>" + "3rd Floor, Cecil Court,<br>"
			+ "Near Regal Cinema,<br>" + "Mahakavi Bushan Marg,<br>" + "Mumbai 400001<br>"
			+ "PS : Contact support@hariyali.org.in in case of any query.";

	public static final String GIFTING_MSG_SUBJECT = "Someone wanted you to have this special gift";
	
	
	public static final String subject = "Welcome to Project Hariyali";
	public static final String content="<p>Dear Tree Planter \"%s\" Green Warrior</p>"+
			"<p>Welcome to Project Hariyali!</p>" +
			"<p>The Mahindra Foundation and Naandi Foundation would like to thank you for your donation."
			+"<p>You can login to your account on our website <a href='http://www.hariyali.org.in'>www.hariyali.org.in</a> with following login details.</p>"
			+"<p>Email ID :%s<br>"
			+"Password :%s</p>"
			+"&nbsp;&nbsp;Your contribution towards the planet will not only reduce your carbon footprint but also support livelihood of a marginalized farmer."
			+"<p>Thank you.</p>"+ "Team Hariyali<br>"+ "Mahindra Foundation<br> 3rd Floor, Cecil Court,"
			+ "Near Regal Cinema,<br>" + "Mahakavi Bushan Marg," + "Mumbai 400001<br>"
			+"<p>PS : Contact <a href='mailto:support@hariyali.org.in'>support@hariyali.org.in</a> in case of any query.</p>";
	
	public static final String thankYouLetterSuject="Thank you For Donation ";
	public static final String thankYouLetterContent="Dear Tree Planter \"%s\" Green Warrior<br>"
			+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Greetings from Project Hariyali !"
			+ "<p>Thank you for your valuable role in making the planet greener.</p>"
			+ "<p>As a token of appreciation please find attached the certificate of your contribution.</p>"			
			+ "<br><br>Team Hariyali<br>"+ "3rd Floor, Cecil Court<br>"
			+ "Near Regal Cinema,<br>" + "Mahakavi Bushan Marg," + "Mumbai 400001<br>"
			+"<p>PS : Contact <a href='mailto:support@hariyali.org.in'>support@hariyali.org.in</a> in case of any query.</p>"
			+"<i>Project Hariyali is a joint initiative of Mahindra Foundation & Naandi Foundation.</i>";
}
