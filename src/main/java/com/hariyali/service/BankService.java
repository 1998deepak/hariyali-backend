package com.hariyali.service;

import com.hariyali.dto.ApiResponse;
import com.hariyali.entity.Bank;

import java.util.List;

/**
 * Service class for bank
 *
 * @author Vinod
 * @version 1.0
 * @date 13/09/2023
 */
public interface BankService {

    public ApiResponse<List<Bank>> getAllBanks();
}
