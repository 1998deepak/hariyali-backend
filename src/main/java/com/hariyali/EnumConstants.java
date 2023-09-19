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
	
	public static final String OTHERTHANINDIA = "OTHERTHANINDIA";

	public static final String GIFTING_MSG_BODY ="<p>Dear %s,</p>" 
			+ "Greetings from Project Hariyali.<br>"
			+ "Thank you for your valuable role in making the planet greener.<br>"
			+ "The gift certificate is attached herewith.<br>"
			+ "As a token of appreciation, we are happy to present to you a certificate towards your contribution.<br><br>"
			+ "Team Hariyali<br>"
			+ "Mahindra Foundation<br>"
			+ "3rd Floor, Cecil Court,Near Regal Cinema,<br>" 
			+ "Mahakavi Bushan Marg,Colaba,<br>" 
			+ "Mumbai,Maharashta - 400001<br>"
			+ "PS : Contact support@hariyali.org.in in case of any query.<br>"
			+ "<i>Project Hariyali is a joint initiative of Mahindra Foundation & Naandi Foundation.</i>";

	public static final String GIFTING_MSG_SUBJECT = "Project Hariyali – Certification of Appreciation";
	
	
	public static final String subject = "Welcome to Project Hariyali";
	public static final String content="<p>Dear %s</p>"+
			"<p>Welcome to Project Hariyali!</p>" +
			"<p>The Mahindra Foundation and Naandi Foundation would like to thank you for your donation."
			+"<p>You can login to your account on our website <a href='http://www.hariyali.org.in'>www.hariyali.org.in</a> with following login details.</p>"
			+"<p>Email ID :%s<br>"
			+"Password :%s</p>"
			+"&nbsp;&nbsp;Your contribution towards the planet will not only reduce your carbon footprint but also support livelihood of a marginalized farmer."
			+"<p>Thank you.</p>"+ "Team Hariyali<br>"+ "Mahindra Foundation<br> 3rd Floor, Cecil Court,"
			+ "Near Regal Cinema,<br>" + "Mahakavi Bushan Marg," + "Mumbai 400001<br>"
			+"<p>PS : Contact <a href='mailto:support@hariyali.org.in'>support@hariyali.org.in</a> in case of any query.</p>";
	
	public static final String thankYouLetterSuject="Project Hariyali – Certification of Appreciation";
	public static final String thankYouLetterContent="Dear %s<br>"
			+ "Greetings from Project Hariyali.<br>"
			+ "Thank you for your valuable role in making the planet greener.<br>"
			+ "As a token of appreciation, we are happy to present to you a certificate towards your contribution.<br><br>"			
			+ "Team Hariyali<br>"
			+"Mahindra Foundation<br>"
			+ "3rd Floor, Cecil Court,Near Regal Cinema,<br>" 
			+ "Mahakavi Bushan Marg," + "Mumbai, Maharashtra - 400001<br>"
			+"<p>PS : Contact <a href='mailto:support@hariyali.org.in'>support@hariyali.org.in</a> in case of any query.</p>"
			+"<i>Project Hariyali is a joint initiative of Mahindra Foundation & Naandi Foundation.</i>";
}
