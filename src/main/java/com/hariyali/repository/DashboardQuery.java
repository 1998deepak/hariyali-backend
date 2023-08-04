package com.hariyali.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface DashboardQuery  {

	
//	@Query( value="select sum(amount) as total_donation_of_all_donor from hariyalidbletest.transaction;\r\n"
//			+ "",nativeQuery=true)
//	public Map<String,Integer> getTotalDonationOfAllDonors();
	
	
	
	
	String donationOfAllDonorQuery="select sum(amount) as totalDonation from hariyalidbletest.transaction";
	
	String donationOfSpecificUserQuery="select sum(amount) as totalDonation from hariyalidbletest.transaction where user_id=?";
	
	String latestStoriesQuery="select * from (select s.name as name,s.email as email,s.description as description ,s.created_at as createdAt from hariyalidbletest.stories s where s.created_at=(select max(st.created_at)from hariyalidbletest.stories st where s.user_id=st.user_id))as res\r\n"
			+ "group by name order by createdAt desc;";
	
	
	
//	@Query(value="select sum(amount) as total_donation_of_all_donor from hariyalidbletest.transaction\"",nativeQuery = true)
//	public Map<String,Integer> getTotalDonationOfAllDonors();
//	
//	@Query(value="select sum(amount) as total_donation_of_donor from hariyalidbletest.transaction where user_id=?",nativeQuery = true)
//	public Map<String,Integer> getTotalDonationOfSpecificUser(long userId);
//	
	
}
