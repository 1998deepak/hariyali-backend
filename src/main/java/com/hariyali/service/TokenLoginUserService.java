package com.hariyali.service;

import com.hariyali.entity.TokenLoginUser;

public interface TokenLoginUserService {

	public TokenLoginUser saveUser(TokenLoginUser tokenLoginUser);
	
	public TokenLoginUser findByUsernameDonorId(String donorId);
	
	public TokenLoginUser findByUsernameEmailId(String emailId);
	
	public TokenLoginUser updateToken(TokenLoginUser tokenLoginUser);
		
	public TokenLoginUser refreshToken(TokenLoginUser tokenLoginUser);

	public TokenLoginUser findByUsername(String username);

}
