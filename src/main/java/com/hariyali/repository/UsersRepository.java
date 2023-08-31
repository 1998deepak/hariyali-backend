package com.hariyali.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hariyali.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {

	@Query(value = "SELECT\r\n" + "    JSON_OBJECT(\r\n" + "    	    'userId', users.user_id,\r\n"
			+ "        'firstName', users.first_name,\r\n" + "        'lastName', users.last_name,\r\n"
			+ "        'mobileNo', users.mobile_number,\r\n" + "        'donorId', users.donorId,\r\n"
			+ "        'emailId', users.emailId,\r\n" + "        'donarType', users.donor_type,\r\n"
			+ "        'prefix', users.prefix,\r\n" + "        'organisation', users.organisation,\r\n"
			+ "        'isTaxBenifit', CASE WHEN users.is_tax_benifit = 1 THEN 'true' ELSE 'false' END,\r\n"
			+ "        'panCard', users.pan_card,\r\n" + "        'address', (\r\n"
			+ "            SELECT JSON_ARRAYAGG(\r\n" + "                JSON_OBJECT(\r\n"
			+ "                    'street1', address.street1,\r\n"
			+ "                    'street2', address.street2,\r\n"
			+ "                    'street3', address.street3,\r\n"
			+ "                    'country', address.country,\r\n" + "                    'state', address.state,\r\n"
			+ "                    'city', address.city,\r\n"
			+ "                    'postalCode', address.postal_code\r\n" + "                )\r\n"
			+ "            )\r\n" + "            FROM tbl_address AS address\r\n"
			+ "            WHERE address.userId = users.user_id AND address.is_deleted=false\r\n" + "        ),\r\n"
			+ "        'donations', (\r\n" + "            SELECT JSON_ARRAYAGG(\r\n"
			+ "                JSON_OBJECT(\r\n" + "                 \r\n"
			+ "                    'donationId', donations.donation_id,\r\n"
			+ "                    'donationType', donations.donation_type,\r\n"
			+ "                    'donationMode', donations.donation_mode,\r\n"
			+ "                    'donationEvent', donations.donation_event,\r\n"
			+ "                    'totalAmount', donations.total_amount,\r\n"
			+ "                    'generalDonation', donations.general_donation,\r\n"
			+ "                    'userPackage', (\r\n" + "                        SELECT JSON_ARRAYAGG(\r\n"
			+ "                            JSON_OBJECT(\r\n"
			+ "                                'packageName', packages.package_name,\r\n"
			+ "                                'bouquetPrice', packages.bouquet_price,\r\n"
			+ "                                'noOfBouquets', packages.no_of_bouquets,\r\n"
			+ "                                'maintenanceCost', packages.maintenance_cost,\r\n"
			+ "                                'amount', packages.amount\r\n" + "                            )\r\n"
			+ "                        )\r\n" + "                        FROM tbl_user_packages AS packages\r\n"
			+ "                        WHERE packages.donationId = donations.donation_id AND packages.deleted=false\r\n"
			+ "                    ),\r\n" + "                    'recipient', (\r\n"
			+ "                        SELECT JSON_ARRAYAGG(\r\n" + "                            JSON_OBJECT(\r\n"
			+ "                                'recipientId', recipients.recipient_id,\r\n"
			+ "                                'firstName', recipients.first_name,\r\n"
			+ "                                'lastName', recipients.last_name,\r\n"
			+ "                                'mobileNo', recipients.mobile_no,\r\n"
			+ "                                'emailId', recipients.emailID,\r\n"
			+ "                                'address', (\r\n"
			+ "                                    SELECT JSON_ARRAYAGG(\r\n"
			+ "                                        JSON_OBJECT(\r\n"
			+ "                                            'addressId',address.address_id,\r\n"
			+ "                                            'street1', address.street1,\r\n"
			+ "                                            'street2', address.street2,\r\n"
			+ "                                            'street3', address.street3,\r\n"
			+ "                                            'country', address.country,\r\n"
			+ "                                            'state', address.state,\r\n"
			+ "                                            'city', address.city,\r\n"
			+ "                                            'postalCode', address.postal_code\r\n"
			+ "                                        )\r\n" + "                                    )\r\n"
			+ "                                    FROM tbl_address AS address\r\n"
			+ "                                    WHERE address.recipientId = recipients.recipient_id AND address.is_deleted=false\r\n"
			+ "                                )\r\n" + "                            )\r\n"
			+ "                        )FROM tbl_recipient AS recipients\r\n"
			+ "                        WHERE recipients.donationId = donations.donation_id AND deleted=false\r\n"
			+ "                    ),\r\n" + "                    'paymentInfo', (\r\n"
			+ "                        SELECT JSON_ARRAYAGG(\r\n" + "                            JSON_OBJECT(\r\n"
			+ "                                'paymentInfoId', paymentInfo.paymentInfo_id,\r\n"
			+ "                                'paymentMode', paymentInfo.payment_mode,\r\n"
			+ "                                'bankName', paymentInfo.bank_name,\r\n"
			+ "                                'chqORddNo', paymentInfo.chq_OR_dd_no,\r\n"
			+ "                                'chqORddDate', DATE_FORMAT(paymentInfo.chq_OR_dd_date, '%Y-%m-%d'),\r\n"
			+ "                                'paymentDate', DATE(paymentInfo.payment_date),\r\n"
			+ "                                'amount', paymentInfo.amount\r\n" + "                            )\r\n"
			+ "                        )\r\n" + "                        FROM tbl_payment_info AS paymentInfo\r\n"
			+ "                        WHERE paymentInfo.donationId = donations.donation_id AND deleted=false\r\n"
			+ "                    )\r\n" + "                )\r\n" + "            )\r\n"
			+ "            FROM tbl_donation AS donations\r\n"
			+ "            WHERE donations.userId = users.user_id\r\n" + "        )\r\n" + "    ) AS 'Result'\r\n"
			+ "FROM tbl_user_master AS users\r\n" + "\r\n" + "WHERE users.emailId = ? AND users.is_deleted=false"
			+ "", nativeQuery = true)
	Object getUserByEmail(String email);

	@Query(value = "SELECT \r\n" + "			    JSON_ARRAYAGG(\r\n" + "			 				   JSON_OBJECT(\r\n"
			+ "			 								'userId', users.user_id,\r\n"
			+ "			 								'firstName', users.first_name,\r\n"
			+ "			 								'lastName', users.last_name,\r\n"
			+ "			 								'emailId', users.emailId,\r\n"
			+ "			 								'donarType', users.donor_type,\r\n"
			+ "			 								'panCard', users.pan_card,\r\n"
			+ "                                            'donorId',users.donorId,\r\n"
			+ "                                            'organisation',users.organisation,\r\n"
			+ "                                            'status',users.status,\r\n"
			+ "											'paymentDate',(select  date(MAX(payment_date)) \r\n"
			+ "			 										from tbl_donation d\r\n"
			+ "			 										inner join\r\n"
			+ "			 										tbl_payment_info p ON d.donation_id=p.donationId\r\n"
			+ "			 										where d.userId=users.user_id)\r\n"
			+ "			 							)\r\n" + "			 			)\r\n"
			+ "			 			 AS 'Result'\r\n" + "			 FROM tbl_user_master as users\r\n"
			+ "			 where users.donorId IS NOT NULL AND users.is_deleted=false", nativeQuery = true)
	Object getAllUsersWithDonarID();

	@Query(value = "SELECT \r\n" + "			 			     JSON_ARRAYAGG(\r\n"
			+ "			 			         JSON_OBJECT(\r\n"
			+ "			 			             'userId', users.user_id,\r\n"
			+ "			 			             'firstName', users.first_name,\r\n"
			+ "			 			             'lastName', users.last_name,\r\n"
			+ "			 			             'emailId', users.emailId,\r\n"
			+ "			 			             'donarType', users.donor_type,\r\n"
			+ "			 			             'panCard', users.pan_card,\r\n"
			+ "			 			             'webId', users.webId,\r\n"
			+ "			                          'organisation',users.organisation,\r\n"
			+ "			 			             'paymentInfo', (\r\n"
			+ "			 			                 SELECT \r\n"
			+ "			 			                     JSON_OBJECT(\r\n"
			+ "			 			                         'paymentDate', MAX(Date(paymentInfo.payment_date)),\r\n"
			+ "			 			                         'paymentInfoId', MAX(paymentInfo.paymentInfo_id),\r\n"
			+ "			 			                         'amount', MAX(paymentInfo.amount),\r\n"
			+ "			                                      'paymentStatus',MAX(paymentInfo.payment_status)\r\n"
			+ "			 			                     )\r\n"
			+ "			 			                 FROM tbl_payment_info AS paymentInfo\r\n"
			+ "			 			                 WHERE paymentInfo.donationId IN (\r\n"
			+ "			 			                     SELECT d.donation_id\r\n"
			+ "			 			                     FROM tbl_donation d\r\n"
			+ "			 			                     INNER JOIN tbl_payment_info p ON d.donation_id = p.donationId\r\n"
			+ "			 			                     WHERE d.userId = users.user_id\r\n"
			+ "			 			                 )\r\n" + "			 			             )\r\n"
			+ "			 			         )\r\n" + "			 			     ) AS 'Result'\r\n"
			+ "			 			 FROM tbl_user_master AS users\r\n"
			+ "			 			 WHERE users.webId IS NOT NULL AND users.is_deleted = false AND users.is_approved=false;", nativeQuery = true)
	Object getAllUsersWithWebId();

	@Query(value = "SELECT JSON_ARRAYAGG( " +
	        "JSON_OBJECT( " +
	        "'donationId', d.donation_id, " +
	        "'donationType', d.donation_type, " + // Add this line for donation type
	        "'paymentInfo', JSON_OBJECT( " +
	        "'paymentInfoId', p.paymentInfo_id, " +
	        "'paymentDate', DATE(p.payment_date), " +
	        "'paymentStatus', p.payment_status " +
	        "), " +
	        "'donorId', u.donorId, " +
	        "'firstName', u.first_name, " +
	        "'lastName', u.last_name " +
	        ") " +
	        ") AS 'Result' " +
	        "FROM tbl_donation d " +
	        "INNER JOIN tbl_payment_info p ON d.donation_id=p.donationId " +
	        "INNER JOIN tbl_user_master u ON u.user_id = d.userId " +
	        "WHERE u.emailId = ?1 AND u.is_deleted=false " +
	        "ORDER BY p.payment_date DESC", nativeQuery = true)
	
	Object getAllDonationOfSpecificUser(String email);

	Users findByUserId(Integer userId);

	Page<Users> findAllByOrderByUserIdDesc(Pageable paging);

	Users findByEmailId(String email);

	Users findByDonorId(String donorId);

	@Query(value = "select um.first_name as firstName,um.last_name as lastName,um.phone as userPhone,um.email as userEmail,"
			+ "um.designation as userDesignation,um.company_name as userCompanyName,um.user_address as userAddress,um.donor_id as donorId,"
			+ "r.role_name as role from hariyalidbletest.user_master um left join hariyalidbletest.roles r on um.role_id = r.role_id"
			+ " where um.user_id=?", nativeQuery = true)
	public Map<String, String> getUserDetails(int userId);

	@Query(value = "select count(user_id) as no_of_donors from hariyalidbletest.user_master", nativeQuery = true)
	public long getDonorCount();

	@Query(value = "SELECT \r\n" + "	            JSON_OBJECT(\r\n"
			+ "	                 'userId', users.user_id,\r\n" + "	                 'firstName', users.first_name,\r\n"
			+ "	                 'lastName', users.last_name,\r\n"
			+ "	                 'mobileNo', users.mobile_number,\r\n"
			+ "	                 'donorId', users.donorId,\r\n" + "	                 'emailId', users.emailId,\r\n"
			+ "	                 'donarType', users.donor_type,\r\n"
			+ "	                 'prefix', users.prefix,\r\n"
			+ "	                 'organisation', users.organisation,\r\n"
			+ "	                 'activityType', users.activity_type,\r\n"
			+ "                     'panCard',users.pan_card,\r\n" + "	                 'address', (\r\n"
			+ "	                     SELECT JSON_ARRAYAGG(\r\n" + "	                         JSON_OBJECT(\r\n"
			+ "	                             'addressId',address.address_id,\r\n"
			+ "	                             'street1', address.street1,\r\n"
			+ "	                             'street2', address.street2,\r\n"
			+ "	                             'street3', address.street3,\r\n"
			+ "	                             'country', address.country,\r\n"
			+ "	                             'state', address.state,\r\n"
			+ "	                             'city', address.city,\r\n"
			+ "	                             'postalCode', address.postal_code\r\n" + "	                         )\r\n"
			+ "	                     )\r\n" + "	                     FROM tbl_address AS address\r\n"
			+ "	                     WHERE address.userId = users.user_id\r\n" + "	                 )\r\n"
			+ "	             ) AS Result\r\n" + "	          FROM tbl_user_master AS users\r\n"
			+ "	          WHERE users.emailId = ? AND users.is_deleted=false\r\n", nativeQuery = true)
	Object getUserPersonalDetailsByEmail(String email);

	@Query(value = "SELECT   	            JSON_OBJECT(\r\n"
			+ "			 	                 'userId', users.user_id,  	                 'firstName', users.first_name,\r\n"
			+ "			 	                 'lastName', users.last_name,\r\n"
			+ "			 	                 'mobileNo', users.mobile_number,\r\n"
			+ "			 	                 'donorId', users.donorId,  	                 'emailId', users.emailId,\r\n"
			+ "			 	                 'donarType', users.donor_type,\r\n"
			+ "			 	                 'prefix', users.prefix,\r\n"
			+ "			 	                 'organisation', users.organisation,\r\n"
			+ "			 	                 'activityType', users.activity_type,\r\n"
			+ "			                      'panCard',users.pan_card,  	                 'address', (\r\n"
			+ "			 	                     SELECT JSON_ARRAYAGG(  	                         JSON_OBJECT(\r\n"
			+ "			 	                             'addressId',address.address_id,\r\n"
			+ "			 	                             'street1', address.street1,\r\n"
			+ "			 	                             'street2', address.street2,\r\n"
			+ "			 	                             'street3', address.street3,\r\n"
			+ "			 	                             'country', address.country,\r\n"
			+ "			 	                             'state', address.state,\r\n"
			+ "			 	                             'city', address.city,\r\n"
			+ "			 	                             'postalCode', address.postal_code  	                         )\r\n"
			+ "			 	                     )  	                     FROM tbl_address AS address\r\n"
			+ "			 	                     WHERE address.userId = users.user_id  	                 )\r\n"
			+ "			 	             ) AS Result  	          FROM tbl_user_master AS users\r\n"
			+ "			 	          WHERE users.donorId =? AND users.is_deleted=false", nativeQuery = true)
	Object getUserPersonalDetailsByDonorId(String donorId);
	
	@Query(value = "SELECT   	            JSON_OBJECT(\r\n"
			+ "			 	                 'userId', users.user_id,  	                 'firstName', users.first_name,\r\n"
			+ "			 	                 'lastName', users.last_name,\r\n"
			+ "			 	                 'mobileNo', users.mobile_number,\r\n"
			+ "			 	                 'donorId', users.donorId,  	                 'emailId', users.emailId,\r\n"
			+ "			 	                 'donarType', users.donor_type,\r\n"
			+ "			 	                 'prefix', users.prefix,\r\n"
			+ "			 	                 'organisation', users.organisation,\r\n"
			+ "			 	                 'activityType', users.activity_type,\r\n"
			+ "			                      'panCard',users.pan_card,  	                 'address', (\r\n"
			+ "			 	                     SELECT JSON_ARRAYAGG(  	                         JSON_OBJECT(\r\n"
			+ "			 	                             'addressId',address.address_id,\r\n"
			+ "			 	                             'street1', address.street1,\r\n"
			+ "			 	                             'street2', address.street2,\r\n"
			+ "			 	                             'street3', address.street3,\r\n"
			+ "			 	                             'country', address.country,\r\n"
			+ "			 	                             'state', address.state,\r\n"
			+ "			 	                             'city', address.city,\r\n"
			+ "			 	                             'postalCode', address.postal_code  	                         )\r\n"
			+ "			 	                     )  	                     FROM tbl_address AS address\r\n"
			+ "			 	                     WHERE address.userId = users.user_id  	                 )\r\n"
			+ "			 	             ) AS Result  	          FROM tbl_user_master AS users\r\n"
			+ "			 	          WHERE users.emailId =? AND users.is_deleted=false", nativeQuery = true)
	Object getExistingUserPersonalDetailsByEmailId(String emailId);

	@Query(value = "SELECT * from tbl_user_master where webId=?", nativeQuery = true)
	Users getUserByWebId(int webId);

	@Query(value = "SELECT * from tbl_user_master where pan_card=?1", nativeQuery = true)
	Users getUserByPancard(String pancard);

	@Query(value = "select \r\n"
			+ "    tum.user_id AS user,\r\n"
			+ "    td.donation_id AS donation,\r\n"
			+ "    tup.package_id AS packages,\r\n"
			+ "    tum.emailId AS userName,\r\n"
			+ "    tup.package_name AS packageName, \r\n"
			+ "    tup.amount AS amount,\r\n"
			+ "    td.donation_type AS donationType,\r\n"
			+ "    tum.donorId AS donarId,\r\n"
			+ "     CASE\r\n"
			+ "        WHEN td.donation_type = 'Gift-Donate' THEN recpt.recipient_id\r\n"
			+ "        ELSE NULL\r\n"
			+ "    END AS recipientId,\r\n"
			+ "    CASE\r\n"
			+ "        WHEN td.donation_type = 'Gift-Donate' THEN recpt.first_name\r\n"
			+ "        ELSE NULL\r\n"
			+ "    END AS firstName\r\n"
			+ "FROM\r\n"
			+ "    tbl_user_master tum\r\n"
			+ "INNER JOIN\r\n"
			+ "    tbl_donation td ON tum.user_id = td.userId\r\n"
			+ "INNER JOIN\r\n"
			+ "    tbl_user_packages tup ON tup.donationId = td.donation_id\r\n"
			+ "    LEFT JOIN\r\n"
			+ "    tbl_recipient recpt ON recpt.donationId = td.donation_id AND td.donation_type = 'Gift-Donate'\r\n"
			+ "    where   td.donation_type = ?1 AND tup.package_name = ?2 AND tum.planted=false", nativeQuery = true)
	List<Map<String, Object>> getUserPlantExportExcel(String donationType,String packageName);
	
	@Query(value="select * from tbl_user_master as users left join tbl_donation as donation on users.user_id=donation.userId where donation.donation_id=?",nativeQuery=true)
	public List<Users> getUserDataByDonationId(int donationId);
	
	@Query(value="Select * from tbl_user_master where emailId=?",nativeQuery = true)
	Users findByEmailIdForDeletedUser(String email);
	
	@Query(value ="SELECT r.donorId as donorId FROM tbl_user_master r WHERE r.donorId IS NOT NULL",nativeQuery = true)
	List<String> getAllDonorId();
	
	@Query(value="select * from tbl_user_master as users left join tbl_donation as donation on users.user_id=donation.userId where donation.donation_id=?",nativeQuery=true)
	public Users getUserByDonationId(int donationId);
}
