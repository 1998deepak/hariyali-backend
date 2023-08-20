package com.hariyali.dto;

import lombok.Data;

@Data
public class ReceiptDto {
	int user_id;
	int donation_id;
	int receiptId;
	String reciept_Path;
	String reciept_number;

}
