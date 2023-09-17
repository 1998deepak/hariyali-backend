package com.hariyali.controller;

import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.PaginationRequestDTO;
import com.hariyali.dto.PlantationDTO;
import com.hariyali.dto.PlantationMasterDTO;
import com.hariyali.service.CommitmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/commitment/")
public class CommitmentController {

    @Autowired
    private CommitmentService service;

    @PostMapping("findByFilter")
    public ResponseEntity<ApiResponse<List<PlantationDTO>>> findAllByFilter(@RequestBody PaginationRequestDTO<PlantationDTO> dto){
        return new ResponseEntity<>(service.getCommitmentList(dto), HttpStatus.OK);
    }//method

}//class
