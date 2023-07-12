package com.hariyali.dao;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hariyali.dto.UsersDTO;
import com.hariyali.entity.Users;
import com.hariyali.exceptions.CustomException;

public interface UserDao {

	public UsersDTO deleteUserById(int userId) throws CustomException;

	public UsersDTO getByDonorId(String donorId);

	public long getdonorCount();

	public Map<String, String> getdonorDetails(int userId);

	public UsersDTO getByUserByEamilID(String email);

	public UsersDTO saveUser(UsersDTO userResponse);

	public Page<Users> getAllUsersByUserIdDesc(Pageable paging);

	public Users saveData(Users user);
}
