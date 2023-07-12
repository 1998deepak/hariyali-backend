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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tbl_certificate")
@Getter
@Setter
public class Certificates {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "certificate_id")
	private int certificateId;

	@Column(name = "certificate_no")
	private String certificateNo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "certificate_date")
	private Date certificateDate;

	@Column(name = "path")
	private String certificatePath;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "created_by")
	private String createdBy;

	@Temporal(TemporalType.DATE)
	@Column(name = "modified_date")
	private Date modifiedDate;

	@Column(name = "modified_by")
	private String modifiedBy;
	
	// bi-directional many-to-one association to Usertypmst
		@ManyToOne
		@JoinColumn(name = "user_id")
		private Users userCertificate;


}
