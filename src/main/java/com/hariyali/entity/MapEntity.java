package com.hariyali.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tbl_maps")
@AllArgsConstructor
@NoArgsConstructor
public class MapEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "map_id")
    private Integer mapId;

    @Column(name = "location")
    private String location;

    @Column(name = "latitude")
    private Long latitude;
    
    
    @Column(name = "longitude")
    private Long longitude;
    
    @Column(name = "distance")
    private Long distance;
    
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
		@OneToOne
		@JoinColumn(name = "donation_id")
		private Donation donationMap;

   
    
}
