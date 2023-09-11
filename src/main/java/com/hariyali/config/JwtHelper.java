package com.hariyali.config;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import com.hariyali.dao.UserDao;
import com.hariyali.dto.UsersDTO;
import com.hariyali.entity.TokenLoginUser;
import com.hariyali.entity.Users;
import com.hariyali.entity.Roles;
import com.hariyali.repository.UsersRepository;
import com.hariyali.service.TokenLoginUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtHelper implements Serializable {

	@Autowired
	UsersRepository jwtRepository;

	@Autowired
	TokenLoginUserService tokenService;

	@Autowired
	private UsersRepository userRepository; 

	private static final long serialVersionUID = -234234523523L;

	@Value("${jwt.expireTime}")
	private long timeInHours;

	public static final long JWT_TOKEN_VALIDITY =15 * 60 * 1000; // 15 minutes

//	@Value("${jwt.secret}")
//	private String secretKey;

	private String secretKey = "boooooooooom!!!!";

	// retrieve username from jwt token
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	// retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return (Date) getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	// for retrieving any information from token we will need the secret key
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
	}

	// check if the token has d
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date(0));
	}

	// generate token for user
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();

		
		Users user = this.userRepository.findByEmailId(userDetails.getUsername());
		
		if (user == null) {
			user = this.userRepository.findByDonorId(userDetails.getUsername());
		}
		
		Roles userType = user.getUserRole();

		Integer roleId = userType.getUsertypeId();
		String roleName = userType.getUsertypeName();
		claims.put("roleId", roleId);
		claims.put("roleName", roleName);

		return doGenerateToken(claims, userDetails.getUsername());
	}

	// while creating the token -
	// 1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
	// 2. Sign the JWT using the HS512 algorithm and secret key.
	private String doGenerateToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
				.signWith(SignatureAlgorithm.HS512, secretKey).compact();
	}

	// validate token
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		TokenLoginUser tokenLoginUser = tokenService.findByUsername(username);
		tokenService.refreshToken(tokenLoginUser);
		boolean flag = token.equalsIgnoreCase(tokenLoginUser.getToken());
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && flag);
	
	}
}
