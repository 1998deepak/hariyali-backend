package com.hariyali.serviceimpl;

import com.hariyali.EnumConstants;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.PaginationRequestDTO;
import com.hariyali.dto.PlantationDTO;
import com.hariyali.dto.PlantationMasterDTO;
import com.hariyali.entity.Plantation;
import com.hariyali.exceptions.CustomException;
import com.hariyali.repository.PlantationRepository;
import com.hariyali.service.CommitmentService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Optional.of;

/**
 * Implementation class of CommitmentService interface
 *
 * @author Vinod
 * @version 1.0
 * @date 17/09/2023
 */
@Slf4j
@Service
public class CommitmentServiceImpl implements CommitmentService {

    @Autowired
    private PlantationRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ApiResponse<List<PlantationDTO>> getCommitmentList(PaginationRequestDTO<PlantationDTO> requestDTO) {
        ApiResponse<List<PlantationDTO>> response = new ApiResponse<>();
        Pageable pageable = PageRequest.of(requestDTO.getPageNumber(), requestDTO.getPageSize());
        Page<Plantation> result = repository.findAllByFilter(requestDTO.getData().getPlantationMaster().getPlantationYear(), requestDTO.getData().getPlantationMaster().getSeason(), pageable);
        if (!isNull(result) && !result.getContent().isEmpty()) {
            List<PlantationDTO> plantationDTOS = of(result.getContent()).get().stream()
                    .map(entity -> {
                        PlantationDTO plantationDTO = modelMapper.map(entity, PlantationDTO.class);
                        plantationDTO.getPlantationMaster().setPlantationDateString(toDateString(plantationDTO.getPlantationMaster().getPlantationDate(), "dd/MM/yyyy"));
                        return plantationDTO;
                    }).collect(Collectors.toList());
            response.setData(plantationDTOS);
            response.setTotalPages(result.getTotalPages());
            response.setTotalRecords(result.getTotalElements());
            response.setStatus(EnumConstants.SUCCESS);
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("Data fetched successfully..!!");
            return response;
        } else
            throw new CustomException("No record found for given filter");
    }//method

    private String toDateString(Date date, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(date);
    }//method

}//class
