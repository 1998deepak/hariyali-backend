package com.hariyali.service;

import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.PaginationRequestDTO;
import com.hariyali.dto.PlantationDTO;

import java.util.List;

/**
 * Service for commitment
 *
 * @author Vinod
 * @version 1.0
 * @date 17/09/2023
 */
public interface CommitmentService {

    public ApiResponse<List<PlantationDTO>> getCommitmentList(PaginationRequestDTO<PlantationDTO> requestDTO);

}//interface
