package com.hariyali.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIdentityReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tbl_user_master")
public class Users implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private int userId;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "mobile_number")
	private String mobileNo;

	@Column(name = "emailId", unique = true)
	private String emailId;

	@Column(name = "donor_type")
	private String donarType;

	@Column(name = "organisation")
	private String organisation;

	@Column(name = "password")
	private String password;

	@Column(name = "last_login_date")
	private Date lastloginDate;

	@Column(name = "attempts")
	private Integer attempts;

	@Column(name = "donorId")
	private String donorId;

	@Column(name = "webId")
	private String webId;

	@Column(name = "payment_status")
	private String paymentStatus;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "createdBy")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modifiedDate")
	private Date modifiedDate;

	@Column(name = "modifiedBy")
	private String modifiedBy;

	@Column(name = "status")
	private String status;

	@Column(name = "prefix")
	private String prefix;

	@Column(name = "is_tax_benifit")
	private Boolean isTaxBenifit;

	@Column(name = "pan_card")
	private String panCard;

	@Column(name = "activity_type")
	private String activityType;

	@OneToMany(mappedBy = "users", fetch = FetchType.LAZY)
//	@JsonManagedReference
	@JsonIdentityReference(alwaysAsId = true)
	private List<Address> address;

	@OneToMany(mappedBy = "users", fetch = FetchType.LAZY)
	@JsonIdentityReference(alwaysAsId = true)
	private List<Donation> donations;

	@Column(name = "is_deleted")
	private Boolean isDeleted = Boolean.FALSE;

	@Column(name = "is_approved")
	private Boolean isApproved = Boolean.FALSE;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Roles userRole;
	
	@Column(name = "planted")
	private Boolean isPlanted = Boolean.FALSE;
	
	@Column(name = "citizenship")
	private String citizenship ;
	
	@Column(name = "country")
	private String country ;

	@Column(name = "approval_status")
	private String approvalStatus ="Pending";

	@Column(name = "remark")
	private String remark;

}
