package com.hariyali.service;

import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.PaginationRequestDTO;
import com.hariyali.dto.PlantationMasterDTO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Service for plantation master
 *
 * @author Vinod
 * @version 1.0
 * @date 13/09/2023
 */
public interface PlantationMasterService {

    /**
     * Service method to ge all plantation details by pagination
     *
     * @param dto
     * @return
     */
    public ApiResponse<List<PlantationMasterDTO>> findAllByFilter(PaginationRequestDTO<PlantationMasterDTO> dto);

    /**
     * upload plantation details using Excel file
     * this method allocate plant to donations user packages and send mail to respective donor.
     *
     * @param multipartFile
     * @return
     */
    public ApiResponse<PlantationMasterDTO> upload(MultipartFile multipartFile, HttpServletRequest request);

    /**
     * export plantation details based on given filter
     *
     * @param dto
     * @return
     */
    public ByteArrayInputStream export(PlantationMasterDTO dto);

    public ByteArrayInputStream downloadTemplate();

    /**
     * service method to get all years
     * @return
     */
    public ApiResponse<List<Integer>> findByDistinctYears();

    /**
     * service method to get season list
     *
     * @return
     */
    public ApiResponse<List<String>> findByDistinctSeason();

    /**
     * service method to get all districts based on year given
     *
     * @param year
     * @return
     */
    public ApiResponse<List<String>> findByDistinctDistricts(Integer year);

    /**
     * service method to get all cities based on year given
     *
     * @param year
     *
     * @return
     */
    public ApiResponse<List<String>> findByDistinctCities(Integer year);

}//interface
