package com.hariyali.serviceimpl;

import com.hariyali.dto.ApiResponse;
import com.hariyali.entity.BankAccount;
import com.hariyali.repository.BankAccountRepository;
import com.hariyali.service.BankAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation class of BankAccountService interface
 *
 * @author Vinod
 * @version 1.0
 * @date 09/09/2023
 */
@Service
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    @Autowired
    private BankAccountRepository repository;

    @Override
    public ApiResponse<List<BankAccount>> getAllActiveAccountList() {
        ApiResponse<List<BankAccount>> apiResponse = new ApiResponse<>();
        apiResponse.setData(repository.findByIsActive(true));
        log.info(apiResponse.getData().size() +" account found!");
        apiResponse.setStatus("Success");
        return apiResponse;
    }//method

}//class
