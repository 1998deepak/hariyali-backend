package com.hariyali.daoimpl;

import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.hariyali.dao.UserDao;
import com.hariyali.dto.UsersDTO;
import com.hariyali.entity.Users;
import com.hariyali.exceptions.CustomException;
import com.hariyali.exceptions.CustomExceptionNodataFound;
import com.hariyali.repository.UsersRepository;

@Component
public class UserdaoImpl implements UserDao {

	@Autowired
	private UsersRepository userRepo;

	@Autowired
	ModelMapper mapper;

	@Override
	public UsersDTO deleteUserById(int userId) throws CustomException {
		Optional<Users> user = userRepo.findById(userId);

		if (user.isPresent()) {
			user.get().setIsDeleted(true);
			this.userRepo.save(user.get());
			ModelMapper modelMapper = new ModelMapper();
			return modelMapper.map(user.get(), UsersDTO.class);

		}
		throw new CustomExceptionNodataFound("User with " + userId + " Not Found");
	}

//	@Override
//	public UsersRequest getByUserEmail(String userEmail) throws CustomException {
//		
//		Optional<Users> userOptional = Optional.ofNullable(this.userRepo.findByUserEmail(userEmail));
//
//		if (userOptional.isPresent()) {
//			ModelMapper modelMapper = new ModelMapper();
//			return modelMapper.map(userOptional.get(), UsersRequest.class);
//
//		}
//
//		throw new CustomExceptionNodataFound("User with " + userEmail + " Not Found");
//
//	}

	@Override
	public long getdonorCount() {

		long donorCount = this.userRepo.getDonorCount();
		if (donorCount == 0) {
			throw new CustomExceptionNodataFound("No donor Found ");

		}
		return donorCount;
	}

	@Override
	public Map<String, String> getdonorDetails(int userId) {
		return this.userRepo.getUserDetails(userId);
	}

//	@Override
//	public Users getByUserId(int userId) {
//		return this.userRepo.findByUserId(userId);
//	}

	@Override
	public UsersDTO saveUser(UsersDTO userResponse) {
		Users user = userRepo.save(mapper.map(userResponse, Users.class));
		// Users user= userRepo.findByEmailId(userResponse.getEmailId());

		if (user != null)
			return mapper.map(user, UsersDTO.class);
		else
			throw new CustomExceptionNodataFound("Error While saving User");

	}

	@Override
	public UsersDTO getByDonorId(String donorId) {
		Optional<Users> userOptional = Optional.ofNullable(this.userRepo.findByDonorId(donorId));

		if (userOptional.isPresent()) {
			ModelMapper modelMapper = new ModelMapper();
			return modelMapper.map(userOptional.get(), UsersDTO.class);

		}

		throw new CustomExceptionNodataFound("User with DonorId " + donorId + " Not Found");

	}
	


	@Override
	public Page<Users> getAllUsersByUserIdDesc(Pageable paging) {

		return userRepo.findAllByOrderByUserIdDesc(paging);
	}

	@Override
	public UsersDTO getByUserByEamilID(String email) {

		Optional<Users> userOptional = Optional.ofNullable(this.userRepo.findByEmailId(email));

		if (userOptional.isPresent()) {

			return mapper.map(userOptional.get(), UsersDTO.class);
		}

		else
			throw new CustomExceptionNodataFound("No Data Found for EamilID " + email);
	}

	@Override
	public Users saveData(Users user) {
		return this.userRepo.save(user);
	}

}
