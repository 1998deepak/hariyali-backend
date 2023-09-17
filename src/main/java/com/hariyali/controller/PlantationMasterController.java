package com.hariyali.controller;

import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.PaginationRequestDTO;
import com.hariyali.dto.PlantationMasterDTO;
import com.hariyali.service.PlantationMasterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1/plantation/")
@Slf4j
public class PlantationMasterController {

    @Autowired
    private PlantationMasterService service;

    /**
     * Rest endpoint to fetch plantation data based on pagination and given filter
     *
     * @param dto PaginationRequestDTO
     * @return
     */
    @PostMapping("findAllByFilter")
    public ResponseEntity<ApiResponse<List<PlantationMasterDTO>>> findAllByFilter(@RequestBody PaginationRequestDTO<PlantationMasterDTO> dto){
        return new ResponseEntity<>(service.findAllByFilter(dto), HttpStatus.OK);
    }//method

    /**
     * Rest endpoint to upload plantation master data
     *
     * @param multipartFile multipart file
     * @param request HttpServletRequest
     * @return
     */
    @PostMapping("upload")
    public ResponseEntity<ApiResponse<PlantationMasterDTO>> upload(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest request){
        return new ResponseEntity<>(service.upload(multipartFile, request), HttpStatus.OK);
    }//method

    /**
     * Rest endpoint to export plantation data based on plantation filter
     *
     * @param response HttpServletResponse
     * @param dto  PlantationMasterDTO
     */
    @PostMapping("/exportReport")
    public void exportExcelUserPlant(HttpServletResponse response, @RequestBody PlantationMasterDTO dto) {

        try {
            ByteArrayInputStream byteArrayInputStream = service.export(dto);
            response.setContentType("application/octet-stream");

            // Set the filename based on the seasonType
            String fileName = "Plantation_Report.xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            IOUtils.copy(byteArrayInputStream, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception = " + e);
        }
    }//method

    @GetMapping("/years")
    public ResponseEntity<ApiResponse<List<Integer>>> findByDistinctYears(){
        return new ResponseEntity<>(service.findByDistinctYears(), HttpStatus.OK);
    }//method


    @GetMapping("/seasons")
    public ResponseEntity<ApiResponse<List<String>>> findByDistinctSeason(){
        return new ResponseEntity<>(service.findByDistinctSeason(), HttpStatus.OK);
    }//method


    @GetMapping("/districts")
    public ResponseEntity<ApiResponse<List<String>>> findByDistinctDistricts(@RequestParam("year") Integer year){
        return new ResponseEntity<>(service.findByDistinctDistricts(year), HttpStatus.OK);
    }//method

    @GetMapping("/cities")
    public ResponseEntity<ApiResponse<List<String>>> findByDistinctCities(@RequestParam("year") Integer year){
        return new ResponseEntity<>(service.findByDistinctCities(year), HttpStatus.OK);
    }//method

}//class
