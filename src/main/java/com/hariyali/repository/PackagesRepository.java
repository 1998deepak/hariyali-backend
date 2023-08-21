package com.hariyali.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.hariyali.entity.Packages;
import com.hariyali.entity.UserPackages;

@Repository
public interface PackagesRepository extends JpaRepository<Packages, Integer> {

	@Query(value = "SELECT JSON_ARRAYAGG(JSON_OBJECT(\r\n"
			+ "	'package_name',p.package_name,\r\n"
			+ "    'bouquet_price',p.bouquet_price\r\n"
			+ ")) as result from tbl_packages as p WHERE p.active = true;", nativeQuery = true)
	public String getAllPackages();

	@Query(value = "SELECT p.package_id,p.active,p.bouquet_price,p.maintenance_cost,p.package_description,p.package_name,\r\n"
			+ " DATE_FORMAT(p.start_date, '%Y-%m-%d') as startDate,\r\n"
			+ "DATE_FORMAT(p.end_date, '%Y-%m-%d') as endDate\r\n"
			+ " FROM tbl_packages p \r\n"
			+ "where p.package_id=? AND p.deleted=false AND p.active=true;", nativeQuery = true)
	Packages findPackageById(int packageId);

	Packages findByPackageId(int packageId);

	public Page<Packages> findAllByOrderByPackageIdDesc(Pageable paging);

	List<Packages> findByEndDateBeforeAndActive(Date endDate, boolean active);

}
