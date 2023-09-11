package com.hariyali.service;

import com.hariyali.dto.ApiResponse;
import com.hariyali.entity.BankAccount;

import java.util.List;

/**
 * Interface for bank account service
 *
 * @Author Vinod
 * @version 1.0
 * @date 09/09/2023
 */
public interface BankAccountService {

    /**
     * Service method returns all active bank accounts
     *
     * @return
     */
    public ApiResponse<List<BankAccount>> getAllActiveAccountList();
}
