package com.hariyali.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.hariyali.entity.Users;
import com.hariyali.repository.UsersRepository;
import com.hariyali.serviceimpl.UsersServiceImpl;

public class UsersTest {

	
	@Mock
    private UsersRepository usersRepository;

    private UsersServiceImpl usersService;

    @Test
    public void testFindByDonorId() {
        // Mock the repository's behavior
        String donorId = "123";
        Users expectedUser = new Users();
        expectedUser.setDonorId(donorId);
        when(usersRepository.findByDonorId(donorId)).thenReturn(expectedUser);

        // Create an instance of the service implementation with the mocked repository
        usersService = new UsersServiceImpl();

        // Call the method under test
        Users actualUser = usersService.findByDonorId(donorId);

        // Verify the repository method was called with the correct parameter
        verify(usersRepository).findByDonorId(donorId);

        // Assert the result is as expected
        assertEquals(expectedUser, actualUser);
    }
}
