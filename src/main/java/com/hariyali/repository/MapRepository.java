package com.hariyali.repository;

import java.util.List;
import java.util.Map;

import com.hariyali.entity.MapEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MapRepository extends JpaRepository<MapEntity, Integer>{
	
	
	MapEntity findByMapId(Integer mapId);
	
	public Page<MapEntity> findAllByOrderByMapIdDesc(Pageable paging);
	
	
	@Query(value=" SELECT u.latitude,u.longitude, u.transaction_id, t.user_id, us.first_name,us.last_name "
			+ " FROM user_location_details  u "
			+ " JOIN transaction t ON u.transaction_id = t.my_transaction_id "
			+ " JOIN user_master us ON t.user_id = us.user_id "
			+ " WHERE u.transaction_id =?",nativeQuery = true)
	public List<Map<String,String>> getByMapDetails(String id);
	

}
