package com.hariyali.entity;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
@Table(name="tbl_reciept")
public class Receipt {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recieptId")
  	private Integer recieptId;
	

	@Column(name = "reciept_number")
  	private String recieptNumber;
		
	@Column(name = "reciept_date")
	@JsonFormat(pattern = "yyyy-MM-dd", shape = Shape.STRING)
  	private Date recieptDate;
	
	@Column(name = "reciept_Path")
	private String  reciept_Path ;
	
	@ManyToOne
	@JoinColumn(name = "donation_id")
	private Donation donation;
	
	

}
