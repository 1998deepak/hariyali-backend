package com.hariyali.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;


@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class JwtWebSecurityConfig   {
	@Autowired
	private CustomUserDetailService customeUserDetailService;
	
	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	
	@Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
	public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(this.customeUserDetailService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}
	
	
	@Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .antMatchers("/api/v1/activateuser/**");
    }
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http.csrf().disable()
	        .authorizeRequests()
	         .antMatchers("/api/v1/login", "/api/v1/resetPassword", "/api/v1/sendEmail", "/api/v1/logout",
	                         "/api/v1/sendEmailForDonorId", "/api/v1/activateuser", "/api/v1/TotalNoOfDonors",
	                         "/api/v1/formData", "/api/v1/getAllPackages","/api/v1/userAddOnline").permitAll()
	            .antMatchers("/api/v1/usersGetAll", "/api/v1/user-by-email/**", "/api/v1/package/**", "/api/v1/AddPackage",
	                         "/api/v1/package-by-id/**", "/api/v1/GetAllReports", "/api/v1/uploadFileDocument",
	                         "/api/v1/downloadFileDocument/**", "/api/v1/donorList", "/api/v1/leaderBoard", "/api/v1/map/**",
	                         "/api/v1/AddMap", "/api/v1/deleteuser/**", "/api/v1/userAdd","/api/v1/updateDonation","/api/v1/inactivePackages/**")
	                .hasAnyAuthority("Admin")
	            .antMatchers("/api/v1/updateUser/**","/api/v1/newDonation").hasAnyAuthority("User","Admin")
	            .and()
	        .exceptionHandling()
	            .authenticationEntryPoint(this.jwtAuthenticationEntryPoint)
	            .and()
	        .sessionManagement()
	            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

	    http.addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

	    http.authenticationProvider(authenticationProvider());

	    return http.build();
	}

}
