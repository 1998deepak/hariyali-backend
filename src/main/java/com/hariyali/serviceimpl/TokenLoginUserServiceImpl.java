package com.hariyali.serviceimpl;

import java.util.Date;

import com.hariyali.config.CustomUserDetailService;
import com.hariyali.dao.TokenLoginUserDao;
import com.hariyali.entity.TokenLoginUser;
import com.hariyali.entity.Users;
import com.hariyali.exceptions.CustomExceptionNodataFound;
import com.hariyali.repository.TokenLoginUserRepository;
import com.hariyali.repository.UsersRepository;
import com.hariyali.service.TokenLoginUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class TokenLoginUserServiceImpl implements TokenLoginUserService {
	@Autowired
	private TokenLoginUserRepository tokenLoginUserRepository;

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private CustomUserDetailService customUserDetailService;

	// save User
	@Override
	public TokenLoginUser saveUser(TokenLoginUser tokenLoginUser) {

		return this.tokenLoginUserRepository.save(tokenLoginUser);

	}

//get user by user name
	@Override
	public TokenLoginUser findByUsername(String username) {
	    if (username == null) {
	        throw new CustomExceptionNodataFound("Username cannot be null");
	    }
	    
	    Users user = this.usersRepository.findByEmailId(username);
	    if(user==null)
	    {
	    	user=this.usersRepository.findByDonorId(username);
	    }
	    TokenLoginUser loginUser=this.findByUsernameEmailId(username);
	    if (loginUser == null) {
	    	
	    		loginUser = this.tokenLoginUserRepository.findByDonorId(user.getDonorId());
		    	
	    	
	    }

	    
	    
	    return loginUser;
	}


	// update token
	@Override
	public TokenLoginUser updateToken(TokenLoginUser tokenLoginUser) {
		TokenLoginUser user = this.findByUsernameDonorId(tokenLoginUser.getDonorId());

		if (user != null) {
			user.setToken(tokenLoginUser.getToken());
			user.setId(tokenLoginUser.getId());
			user.setFlag(tokenLoginUser.isFlag());
			user.setLastUpdatedOn(tokenLoginUser.getLastUpdatedOn());
		} else {
			user = this.findByUsernameEmailId(tokenLoginUser.getEmailId());

			if (user != null) {
				user.setToken(tokenLoginUser.getToken());
				user.setId(tokenLoginUser.getId());
				user.setFlag(tokenLoginUser.isFlag());
				user.setLastUpdatedOn(tokenLoginUser.getLastUpdatedOn());
			} else {
				throw new CustomExceptionNodataFound("User not found");
			}
		}

		return this.tokenLoginUserRepository.save(user);
	}

	@Override
	public TokenLoginUser refreshToken(TokenLoginUser tokenLoginUser) {
		boolean flag = tokenLoginUser.isFlag();
		if (tokenLoginUser.isFlag()) {
			TokenLoginUser updatedToken = new TokenLoginUser();
			updatedToken.setLastUpdatedOn(new Date(System.currentTimeMillis()));
			updatedToken.setToken(tokenLoginUser.getToken());
			return this.tokenLoginUserRepository.save(tokenLoginUser);
		}
		return tokenLoginUser;
	}

	@Override
	public TokenLoginUser findByUsernameDonorId(String donorId) {
		return this.tokenLoginUserRepository.findByDonorId(donorId);
	}

	@Override
	public TokenLoginUser findByUsernameEmailId(String emailId) {
		return this.tokenLoginUserRepository.findByEmailId(emailId);
	}

}
