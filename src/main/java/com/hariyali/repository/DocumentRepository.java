package com.hariyali.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hariyali.entity.Document;
import com.hariyali.entity.Donation;
import com.hariyali.entity.Users;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {

	@Query(value = "SELECT doc_id FROM tbl_user_document WHERE doc_id IS NOT NULL AND doc_id !='' ORDER BY doc_id DESC LIMIT 1", nativeQuery = true)
	public String getLastDocID();

	public Document findByYearAndDocTypeAndDonation(int year, String docType,Donation donation);

	@Query(value = "select id, doc_id, doc_type, file_name, file_path, file_type,  year from tbl_user_document where doc_type<>'CERTIFICATE' and userId = :userId",
	 countQuery = "select count(*) from tbl_user_document where doc_type<>'CERTIFICATE' and userId = :userId", nativeQuery = true)
	public Page<Object[]> findByUserId(@Param("userId") Integer userId, Pageable pageable);

}
