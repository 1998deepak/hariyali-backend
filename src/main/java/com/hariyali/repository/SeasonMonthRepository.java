package com.hariyali.repository;

import com.hariyali.entity.SeasonMonth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for season and month mapping
 *
 * @author Vinod
 * @version 1.0
 * @date 14/09/2023
 */
@Repository
public interface SeasonMonthRepository extends JpaRepository<SeasonMonth, Integer> {

    public List<SeasonMonth> findBySeasonOrderByMonthNumber(String season);

}//class
