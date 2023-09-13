package com.hariyali.serviceimpl;

import com.hariyali.dto.ApiResponse;
import com.hariyali.entity.Bank;
import com.hariyali.repository.BankRepository;
import com.hariyali.service.BankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Implementation class of BankService interface
 *
 * @author Vinod
 * @version 1.0
 * @date 13/09/2023
 */
@Service
@Slf4j
public class BankServiceImpl implements BankService {

    @Autowired
    private BankRepository repository;

    @Override
    public ApiResponse<List<Bank>> getAllBanks() {
        ApiResponse<List<Bank>> apiResponse = new ApiResponse<>();
        apiResponse.setData(repository.findByIsActive(true));
        log.info(apiResponse.getData().size() +" banks found!");
        apiResponse.setStatus("Success");
        return apiResponse;
    }//method

}//class
