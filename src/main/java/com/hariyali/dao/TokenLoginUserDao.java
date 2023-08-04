package com.hariyali.dao;

import com.hariyali.entity.TokenLoginUser;

public interface TokenLoginUserDao {

	
	public TokenLoginUser getByUserName(String username);
	
	public TokenLoginUser saveTokenLoginUser(TokenLoginUser loginUser);
}
