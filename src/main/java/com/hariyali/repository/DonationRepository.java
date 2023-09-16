package com.hariyali.repository;

import com.hariyali.entity.Donation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Integer> {

	public Donation findByCreatedBy(String createdBy);

	public Donation findByDonationId(int donationId);

	@Query(value = "select    JSON_OBJECT(\r\n" + "						 			         'user', JSON_OBJECT(\r\n"
			+ "						                      'donorId', users.donorId,'emailId',users.emailId,\r\n"
			+ "						 			         \r\n"
			+ "						 			         'donations', (\r\n"
			+ "						 			             SELECT JSON_ARRAYAGG(\r\n"
			+ "						 			                 JSON_OBJECT(\r\n"
			+ "						 			                     'donationId', donations.donation_id,\r\n"
			+ "						 			                     'donationType', donations.donation_type,\r\n"
			+ "						 			                     'donationMode', donations.donation_mode,\r\n"
			+ "						 			                     'donationEvent', donations.donation_event,\r\n"
			+ "						 			                     'totalAmount', donations.total_amount,\r\n"
			+ "						 			                     'generalDonation', donations.general_donation,\r\n"
			+ "						                                  'createdDate',Date(donations.created_date),\r\n"
			+ "						                                  'createdBy',donations.created_by,\r\n"
			+ "						                                  'modifiedDate',Date(donations.modified_date),\r\n"
			+ "						                                  'modifiedBy',donations.modified_by,\r\n"
			+ "						                                  'isDeleted',donations.deleted,'userId',donations.userId,\r\n"
			+ "						 			                     'userPackage', (\r\n"
			+ "						 			                         SELECT JSON_ARRAYAGG(\r\n"
			+ "						 			                             JSON_OBJECT(\r\n"
			+ "						 											'packageId',packages.package_id,\r\n"
			+ "						 			                                 'packageName', packages.package_name,\r\n"
			+ "						                                              'noOfBouquets', packages.no_of_bouquets,\r\n"
			+ "						                                              'bouquetPrice',packages.bouquet_price,\r\n"
			+ "						 			                                 'maintenanceCost', packages.maintenance_cost,\r\n"
			+ "						                                              'createdDate',Date(packages.created_date),\r\n"
			+ "						 											'createdBy',packages.created_by,\r\n"
			+ "						                                  'modifiedDate',Date(packages.modified_date),\r\n"
			+ "						                                  'modifiedBy',packages.modified_by,\r\n"
			+ "						                                  'isDeleted',packages.deleted,\r\n"
			+ "						 			                                 'amount', packages.amount\r\n"
			+ "						 			                             )\r\n"
			+ "						 			                         )\r\n"
			+ "						 			                         FROM tbl_user_packages AS packages\r\n"
			+ "						 			                         WHERE packages.donationId = donations.donation_id\r\n"
			+ "						 			                     ),\r\n"
			+ "						 			                     'recipient', (\r\n"
			+ "						 			                         SELECT JSON_ARRAYAGG(\r\n"
			+ "						 			                             JSON_OBJECT(\r\n"
			+ "						 												'recipientId',recipients.recipient_id,\r\n"
			+ "						 			                                 'firstName', recipients.first_name,\r\n"
			+ "						 			                                 'lastName', recipients.last_name,\r\n"
			+ "						 			                                 'mobileNo', recipients.mobile_no,\r\n"
			+ "						 			                                 'emailId', recipients.emailID,\r\n"
			+ "						                                              'createdDate',Date(recipients.created_date),\r\n"
			+ "						 											'createdBy',recipients.created_by,\r\n"
			+ "						                                  'modifiedDate',Date(recipients.modified_date),\r\n"
			+ "						                                  'modifiedBy',recipients.modified_by,\r\n"
			+ "						                                  'isDeleted',recipients.deleted,\r\n"
			+ "						 			                                 'address', (\r\n"
			+ "						 			                                     SELECT JSON_ARRAYAGG(\r\n"
			+ "						 			                                         JSON_OBJECT(\r\n"
			+ "						 														'addressId',address.address_id,\r\n"
			+ "						 			                                             'street1', address.street1,\r\n"
			+ "						 			                                             'street2', address.street2,\r\n"
			+ "						 			                                             'street3', address.street3,\r\n"
			+ "						 			                                             'country', address.country,\r\n"
			+ "						 			                                             'state', address.state,\r\n"
			+ "						 			                                             'city', address.city,\r\n"
			+ "						 			                                             'postalCode', address.postal_code,\r\n"
			+ "						                                                          'createdDate',Date(address.created_date),\r\n"
			+ "						 											'createdBy',address.created_by,\r\n"
			+ "						                                  'modifiedDate',Date(address.modified_date),\r\n"
			+ "						                                  'modifiedBy',address.modified_by,\r\n"
			+ "						                                  'isDeleted',address.is_deleted\r\n"
			+ "						 			                                         )\r\n"
			+ "						 			                                     )\r\n"
			+ "						 			                                     FROM tbl_address AS address\r\n"
			+ "						 			                                     WHERE address.recipientId = recipients.recipient_id\r\n"
			+ "						 			                                 )\r\n"
			+ "						 			                             )\r\n"
			+ "						 			                         )\r\n"
			+ "						 			                         FROM tbl_recipient AS recipients\r\n"
			+ "						 			                         WHERE recipients.donationId = donations.donation_id\r\n"
			+ "						 			                     ),\r\n"
			+ "						 			                     'paymentInfo', (\r\n"
			+ "						 			                         SELECT JSON_ARRAYAGG(\r\n"
			+ "						 			                             JSON_OBJECT(\r\n"
			+ "						 			                                 'paymentInfoId', paymentInfo.paymentInfo_id,\r\n"
			+ "						 			                                 'paymentMode', paymentInfo.payment_mode,\r\n"
			+ "						 			                                 'bankName', paymentInfo.bank_name,\r\n"
			+ "						 			                                 'chqORddNo', paymentInfo.chq_OR_dd_no,\r\n"
			+ "						 			                                 'chqORddDate', DATE_FORMAT(paymentInfo.chq_OR_dd_date, '%Y-%m-%d'),\r\n"
			+ "						 			                                 'paymentDate', DATE_FORMAT(paymentInfo.payment_date, '%Y-%m-%d'),\r\n"
			+ "						 			                                 'amount', paymentInfo.amount,\r\n"
			+ "						                                              'paymentStatus',paymentInfo.payment_status,\r\n"
			+ "						                                              'remark',paymentInfo.remark,\r\n"
			+ "						                                              'isDeleted',paymentInfo.is_deleted,\r\n"
			+ "						                                              'createdDate',Date(paymentInfo.created_date),\r\n"
			+ "						 											'createdBy',paymentInfo.created_by,\r\n"
			+ "						                                  'modifiedDate',Date(paymentInfo.modified_date),\r\n"
			+ "						                                  'modifiedBy',paymentInfo.modified_by\r\n"
			+ "						 			                             )\r\n"
			+ "						 			                         )\r\n"
			+ "						 			                         FROM tbl_payment_info AS paymentInfo\r\n"
			+ "						 			                         WHERE paymentInfo.donationId = donations.donation_id\r\n"
			+ "						 			                     )\r\n"
			+ "						 			                 )\r\n"
			+ "						 			             )\r\n"
			+ "						 			             FROM tbl_donation AS donations\r\n"
			+ "						 			             JOIN tbl_user_master AS users ON users.user_id = donations.userId\r\n"
			+ "						 			             WHERE donations.donation_id = ?1 AND donations.deleted = false\r\n"
			+ "						 			         )\r\n" + "						 			     ) \r\n"
			+ "						 			     )AS Result\r\n"
			+ "						 			 FROM tbl_donation AS donations\r\n"
			+ "						 			 JOIN tbl_user_master AS users ON users.user_id = donations.userId\r\n"
			+ "						 			 WHERE donations.donation_id = ?1 AND donations.deleted = false", nativeQuery = true)

	Object getSpecificDonationById(int donationId);

	@Query(value = "\r\n" + "\r\n" + "SELECT *  FROM tbl_donation AS donation \r\n"
			+ "WHERE donation.deleted = false\r\n" + " AND donation.created_date = (\r\n"
			+ "			   SELECT MAX(created_date)    \r\n"
			+ "               FROM tbl_donation    WHERE DATE(created_date) = (\r\n"
			+ "			     SELECT DATE(MAX(created_date))     \r\n" + "                 FROM tbl_donation     \r\n"
			+ "                 WHERE userId = ?1\r\n"
			+ "			   )  ) AND donation.userId =?1  ORDER BY TIME(created_date) DESC  LIMIT 1;\r\n", nativeQuery = true)
	Donation getDonationByUserID(int userId);

	@Query(value = "SELECT * FROM tbl_donation AS donation   WHERE donation.deleted=false and  donation.modified_date = (\r\n"
			+ "			 			   SELECT MAX(modified_date)    FROM tbl_donation    WHERE DATE(modified_date) = (\r\n"
			+ "			 			     SELECT DATE(MAX(modified_date))      FROM tbl_donation      WHERE userId = ?1\r\n"
			+ "			 			   )  ) AND donation.userId = ?1  ORDER BY TIME(modified_date) DESC  LIMIT 1;", nativeQuery = true)
	Donation getDonationLatestUpdateByUserId(int userId);

	@Query(value = "select * from tbl_donation where userId=?", nativeQuery = true)
	List<Donation> getDonationDataByUserId(int userId);

	@Query(value = "SELECT JSON_ARRAYAGG(JSON_OBJECT('donationId', d.donation_id,'donationMode',d.donation_mode,'donationType',d.donation_type,'paymentInfo', JSON_OBJECT(\r\n"
			+ "'paymentInfoId', p.paymentInfo_id,'paymentDate', DATE(p.payment_date),'paymentStatus', p.payment_status,'amount',p.amount,\r\n"
			+ "'donorId', u.donorId,'firstName', u.first_name,'lastName', u.last_name))) AS Result\r\n"
			+ "FROM tbl_donation d INNER JOIN tbl_payment_info p ON d.donation_id = p.donationId\r\n"
			+ "INNER JOIN tbl_user_master u ON u.user_id = d.userId WHERE u.emailId = ? AND u.is_deleted = false\r\n"
			+ "ORDER BY p.payment_date DESC;", nativeQuery = true)
	Object getAllDonationDoneByUser(String email);

	@Query(value = "SELECT * FROM tbl_donation Where order_id = :orderId", nativeQuery = true)
	public Donation findByOrderId(@Param("orderId") String orderId);
	
	@Query(value = "SELECT COUNT(d.donation_id) AS donation_count\r\n"
			+ "FROM tbl_user_master u\r\n"
			+ "LEFT JOIN tbl_donation d ON u.user_id = d.userId\r\n"
			+ "WHERE u.emailId = ?;",nativeQuery = true)
	public int donationCount(String emailId);

	@Query(value = "SELECT * FROM tbl_donation WHERE userId = :userId",
	countQuery = "SELECT COUNT(*) FROM tbl_donation WHERE userId = :userId",
	nativeQuery = true)
	Page<Donation> findByUserId(@Param("userId") Integer userId, Pageable pageable);
	
	
	@Query(value = "SELECT * FROM tbl_donation where userId in(select user_id from tbl_user_master where pan_card=?1 )",nativeQuery = true)
	public Donation findByUserPan(String panCard);
}
