package com.hariyali.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_season_month")
public class SeasonMonth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "season_month_id")
    private Integer id;

    @Column(name = "season")
    private String season;

    @Column(name = "month")
    private String month;

    @Column(name = "month_number")
    private Integer monthNumber;


}
