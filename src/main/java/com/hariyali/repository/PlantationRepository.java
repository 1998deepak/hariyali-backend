package com.hariyali.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hariyali.entity.Plantation;

@Repository
public interface PlantationRepository extends JpaRepository<Plantation, Long> {

    @Query(value = "SELECT * FROM tbl_plantation p, tbl_plantation_master m WHERE p.plantation_master_id = m.id AND YEAR(m.plantationDate) = :year AND m.season = :season",
            countQuery = "SELECT COUNT(*) FROM tbl_plantation p, tbl_plantation_master m WHERE p.plantation_master_id = m.id AND YEAR(m.plantationDate) = :year AND m.season = :season",
            nativeQuery = true
    )
    Page<Plantation> findAllByFilter(@Param("year") Integer year, @Param("season") String season, Pageable pageable);

}//interface
