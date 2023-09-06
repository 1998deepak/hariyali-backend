package com.hariyali.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
	private String season;
	private Integer finacialYear;
	private Long noOfplantsPlanted;
	private String state;
	private String district;
	private String village;
	private String plot;
	private LocalDate plantationDate;
	private Float lattitude;
	private Float longitude;
	private String status;

	@ManyToOne
	@JoinColumn(name = "user_package_id") // Foreign key column in Plantation table
	private UserPackages userPackages;

}
