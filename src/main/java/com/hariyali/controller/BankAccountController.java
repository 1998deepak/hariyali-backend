package com.hariyali.controller;

import com.hariyali.dto.ApiResponse;
import com.hariyali.entity.BankAccount;
import com.hariyali.service.BankAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Rest controller for bank account
 *
 * @author Vinod
 * @version 1.0
 * @date 09/09/2023
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class BankAccountController {

    @Autowired
    private BankAccountService service;

    /**
     * Rest endpoint to get all active bank accounts
     *
     * @return
     */
    @GetMapping("/findAllActiveAccount")
    public ApiResponse<List<BankAccount>> getAllActiveAccountList() {
        return service.getAllActiveAccountList();
    }
}
