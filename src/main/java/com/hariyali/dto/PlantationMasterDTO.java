package com.hariyali.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlantationMasterDTO {

    private long id;
    private String state;
    private String district;
    private String village;
    private String season;
    private String plot;
    private Long noOfPlantsPlanted;
    private Date plantationDate;
    private String plantationDateString;
    private Double latitude;
    private Double longitude;
    private String status;
    private Integer plantationYear;

}//class
