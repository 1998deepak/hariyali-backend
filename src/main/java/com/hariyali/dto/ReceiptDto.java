package com.hariyali.dto;



import java.util.Date;

import lombok.Data;

@Data
public class ReceiptDto {
	int donation_id;
	int receiptId;
	String reciept_Path;
	String reciept_number;
	Date recieptDate;

}
