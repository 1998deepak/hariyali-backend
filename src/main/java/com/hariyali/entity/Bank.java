package com.hariyali.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity class for bank
 *
 * @author Vinod
 * @version 1.0
 * @date 13/09/2023
 */
@Entity
@Table(name = "tbl_bank")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bank_id")
    Integer id;

    @Column(name = "bank_name")
    String bankName;

    @Column(name = "isactive")
    Boolean isActive;

    @Column(name = "created_date")
    Date createdDate;

    @Column(name = "created_by")
    String created_by;
}
