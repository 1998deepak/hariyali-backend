package com.hariyali.daoimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.hariyali.dao.TokenLoginUserDao;
import com.hariyali.entity.TokenLoginUser;
import com.hariyali.repository.TokenLoginUserRepository;

@Component
public class TokenLoginUserdaoImpl implements TokenLoginUserDao{

	@Autowired
	private TokenLoginUserRepository tokenLoginUserRepository;

	@Override
	public TokenLoginUser getByUserName(String username) {
		
		return this.tokenLoginUserRepository.findByDonorId(username);
	}

	@Override
	public TokenLoginUser saveTokenLoginUser(TokenLoginUser loginUser) {
		
		return this.tokenLoginUserRepository.save(loginUser);
	}

	
}
