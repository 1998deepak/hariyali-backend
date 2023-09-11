package com.hariyali.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity class for bank account
 *
 * @author Vinod
 * @version 1.0
 * @date 09/09/2023
 */
@Entity
@Table(name = "tbl_Account")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "account_id")
    Integer id;

    @Column(name = "account_holder_name")
    String accountHolderName;

    @Column(name = "account_no")
    String accountNumber;

    @Column(name = "bank_name")
    String bankName;

    @Column(name = "bank_branch")
    String bankBranch;

    @Column(name = "bank_address")
    String bankAddress;

    @Column(name = "ifsc_code")
    String ifscCode;

    @Column(name = "isactive")
    Boolean isActive;

    @Column(name = "created_date")
    Date createdDate;

    @Column(name = "created_by")
    String bank_name;

}
