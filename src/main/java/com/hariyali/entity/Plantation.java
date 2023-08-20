package com.hariyali.entity;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
	@JoinColumn(name = "userPackageId")
	private UserPackages userPackages;
	
	@JsonIgnore
	@OneToMany(mappedBy="plantation")
	private List<Commitment> commitment;

}
