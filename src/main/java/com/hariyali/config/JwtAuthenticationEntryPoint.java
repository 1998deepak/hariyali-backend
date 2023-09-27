package com.hariyali.config;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hariyali.entity.Users;
import com.hariyali.repository.UsersRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint{

	@Autowired
	private JwtHelper jwtTokenUtil;

	@Autowired
	private UsersRepository userRepository;

	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		final String requestTokenHeader = request.getHeader("Authorization");
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer")) {
			String jwtToken = requestTokenHeader.substring(7);
			try {
				String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
				Users users = userRepository.findByEmailId(username);
				users.setActiveSession(0);
				userRepository.save(users);
			} catch (IllegalArgumentException e) {
				log.error("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				log.error("JWT Token has expired");
			} catch (MalformedJwtException e) {
				log.error("Invalid Token");
			}
		} else {
			log.warn("JWT Token does not begin with Bearer String");
		}
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Access Denied! Unauthorized Request..");
	}
}
