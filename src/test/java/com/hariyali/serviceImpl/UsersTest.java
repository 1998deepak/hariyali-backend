package com.hariyali.serviceImpl;

import com.ccavenue.security.AesCryptUtil;
import com.hariyali.entity.Users;
import com.hariyali.repository.UsersRepository;
import com.hariyali.serviceimpl.UsersServiceImpl;
import org.apache.commons.collections.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
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
        String donorId = "123";
        Users expectedUser = new Users();
        expectedUser.setDonorId(donorId);
        when(usersRepository.findByDonorId(donorId)).thenReturn(expectedUser);

        // Call the method under test
        Users actualUser = usersService.findByDonorId(donorId);

        // Verify the repository method was called with the correct parameter
        verify(usersRepository).findByDonorId(donorId);

        // Assert the result is as expected
        assertEquals(expectedUser, actualUser);

    }
}
