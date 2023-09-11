package com.hariyali.repository;

import com.hariyali.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {

    public List<BankAccount> findByIsActive(Boolean isActive);
}
