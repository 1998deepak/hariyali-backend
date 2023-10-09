package com.hariyali.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * DTO for donor list page request
 *
 * @author Vinod
 * @version 1.0
 * @date 10/09/2023
 */
@Getter
@Setter
public class DonorListRequestDTO {

    String searchText;
    String status;
    String donorType;
    Integer pageNumber;
    Integer pageSize;
    Integer userId;
    Date fromDate;
    Date toDate;

}
