package com.hariyali.controller;

import com.hariyali.dto.ApiResponse;
import com.hariyali.entity.Bank;
import com.hariyali.entity.BankAccount;
import com.hariyali.service.BankAccountService;
import com.hariyali.service.BankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Rest controller class for bank
 *
 * @author Vinod
 * @version 1.0
 * @date 13/09/2023
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/bank/")
public class BankController {


    @Autowired
    private BankService service;

    /**
     * Rest endpoint to get all active bank
     *
     * @return
     */
    @GetMapping("/findAllActiveBank")
    public ApiResponse<List<Bank>> getAllActiveBankList() {
        return service.getAllBanks();
    }
}
