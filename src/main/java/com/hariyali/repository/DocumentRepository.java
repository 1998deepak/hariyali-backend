package com.hariyali.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hariyali.entity.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {

	@Query(value = "SELECT doc_id FROM tbl_user_document ORDER BY doc_id DESC LIMIT 1", nativeQuery = true)
	public String getLastDocID();

	public Document findByYearAndDocType(int year, String docType);

}
