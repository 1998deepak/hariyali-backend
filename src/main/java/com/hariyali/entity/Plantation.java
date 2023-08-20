package com.hariyali.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_plantation")
public class Plantation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private long userId;
	private long donationId;
	private long packageId;
	private String username;
	private String packageName;
	private float amount;
	private String plantName;
	private long quantity;
	
	private LocalDate plantDate;
	
	private String plantLocation;
	
	@ManyToOne
    @JoinColumn(name = "user_package_id") // Foreign key column in Plantation table
    private UserPackages userPackages;
	
	@ManyToOne
	@JoinColumn(name = "plantation_Id")
	private Plantation plantation;

}
