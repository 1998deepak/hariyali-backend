//package com.hariyali.entity;
//
//
//import java.time.LocalDate;
//import java.util.Date;
//
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Entity
//@Table(name = "tbl_commitment")
//public class Commitment {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private long id;
//
//	private LocalDate  startDate;
//
//	private LocalDate  endDate;
//
//	private LocalDate  dateOFPlantation;
//
//	@ManyToOne
//	@JoinColumn(name = "plantation_Id")
//	private Plantation plantation;
//
//
//}
//
//
