package com.hariyali.serviceImpl;

import com.ccavenue.security.AesCryptUtil;
import com.hariyali.entity.Users;
import com.hariyali.repository.UsersRepository;
import com.hariyali.serviceimpl.UsersServiceImpl;
import io.jsonwebtoken.Jwts;
import org.apache.commons.collections.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsersTest {

	
	@Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UsersServiceImpl usersService;

    @Test
    public void testFindByDonorId() {
        // Mock the repository's behavior
//        String donorId = "123";
//        Users expectedUser = new Users();
//        expectedUser.setDonorId(donorId);
//        when(usersRepository.findByDonorId(donorId)).thenReturn(expectedUser);
//
//        // Call the method under test
//        Users actualUser = usersService.findByDonorId(donorId);
//
//        // Verify the repository method was called with the correct parameter
//        verify(usersRepository).findByDonorId(donorId);
//
//        // Assert the result is as expected
//        assertEquals(expectedUser, actualUser);


//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.MONTH, 0);
//
//        cal.set(Calendar.DAY_OF_MONTH, 1);
//
//        Date fromDate = cal.getTime();
//
//        cal.set(Calendar.MONTH, 6);
//        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
//
//        Date toDate = cal.getTime();
//        System.out.println(fromDate);
//        System.out.println(toDate);
//        System.out.println(Jwts.parser().setSigningKey("boooooooooom!!!!").parsePlaintextJws("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ2aW5vZHNhdGF2YXNlQGdtYWlsLmNvbSIsInJvbGVJZCI6MSwicm9sZU5hbWUiOiJBZG1pbiIsImV4cCI6MTY5NTg5OTk1MSwiaWF0IjoxNjk1ODk5MDUxfQ.k4P3ATSdT0yZXdOBMguTzMHS4gRRD0_EguaYTjrzYhzpFU1wVAcFJQsJ8x3Hz9VSI1nMf_NSy__OVLz67QmebA"));

        System.out.println(new  AesCryptUtil("B42D2F7ADB398C9271B6C1C8249BB1FD").encrypt("payment_mode=Credit Card&tracking_id=1234567890&bank_ref_no=PAYMENT0001&order_status=Success&failure_message=Success&card_name=VISA&merchant_id=2711812&order_id=1695291309369&currency=INR&amount=4500.0&redirect_url=https://hariyali-dev.m-devsecops.com/api/v1/paymentIntegration&cancel_url=https://hariyali-dev.m-devsecops.com/api/v1/paymentIntegration&language=EN&billing_name=null null&billing_address=Gonde  &billing_city=Nashik&billing_state=Maharashtra&billing_zip=422403&billing_country=India&billing_tel=null&billing_email=suvarna.khatale@kriosispl.in"));
    }
}
