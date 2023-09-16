package com.hariyali.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hariyali.entity.PlantationMaster;

import java.util.List;

@Repository
public interface PlantationMasterRepository extends JpaRepository<PlantationMaster, Long> {

    @Query(value = "SELECT * FROM tbl_plantation_master WHERE season = :season AND village IN (:cities) AND district IN (:districts) AND YEAR(plantationDate) = :year",
            countQuery = "SELECT COUNT(*) FROM tbl_plantation_master WHERE plot IN (:plots) AND village IN (:cities) AND district IN (:districts) AND YEAR(plantationDate) = :year",
            nativeQuery = true
    )
    Page<PlantationMaster> findAllByFilter(
            @Param("season") String season,
            @Param("cities") List<String> cityList, 
            @Param("districts") List<String> districtList,
            @Param("year") Integer plantationYear,
            Pageable pageable);

    @Query(value = "SELECT * FROM tbl_plantation_master WHERE season = :season AND village IN (:cities) AND district IN (:districts) AND YEAR(plantationDate) = :year",
            nativeQuery = true
    )
    List<PlantationMaster> findAllByFilter(
            @Param("season") String season,
            @Param("cities") List<String> cityList, 
            @Param("districts") List<String> districtList,
            @Param("year") Integer plantationYear);

    @Query(value = "SELECT DISTINCT YEAR(plantationDate) FROM tbl_plantation_master", nativeQuery = true)
    List<Integer> findByDistinctYears();

    @Query(value = "SELECT DISTINCT season FROM tbl_plantation_master", nativeQuery = true)
    List<String> findByDistinctSeason();

    @Query(value = "SELECT DISTINCT district FROM tbl_plantation_master WHERE YEAR(plantationDate) = :year ", nativeQuery = true)
    List<String> findByDistinctDistricts(@Param("year") Integer year);

    @Query(value = "SELECT DISTINCT village FROM tbl_plantation_master WHERE YEAR(plantationDate) = :year ", nativeQuery = true)
    List<String> findByDistinctCities(@Param("year") Integer year);

}
