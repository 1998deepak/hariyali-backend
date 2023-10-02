package com.hariyali.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hariyali.dao.UserDao;
import com.hariyali.entity.Users;
import com.hariyali.repository.UsersRepository;

@Service
@Transactional
public class CustomUserDetailService implements UserDetailsService {
	@Autowired
	@Lazy
	private UsersRepository userRepo;

	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		
		Users user = this.userRepo.findByDonorId(username);
		if (user!=null   &&  (username.equals(user.getDonorId()))) {
			    System.out.println("LoadUserName: " + user.getDonorId());
			    System.out.println("Name: " + user.getFirstName() + " " + user.getLastName());
			    return new CustomUserDetails(user);
			  
		    
		}
		user= this.userRepo.findByEmailId(username);
		
		 if (user!=null &&  (username.equalsIgnoreCase(user.getEmailId()))) {
			    System.out.println("LoadUserName: " + user.getEmailId());
			    System.out.println("Name: " + user.getFirstName() + " " + user.getLastName());
			    return new CustomUserDetails(user);
			
			
		}
		
		
		
		    throw new UsernameNotFoundException("User not found with username: " + username);
		

	}

}
