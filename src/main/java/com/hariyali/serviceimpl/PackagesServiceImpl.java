package com.hariyali.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.hariyali.dao.PackageDao;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.PackagesRequest;
import com.hariyali.dto.UsersDTO;
import com.hariyali.entity.Packages;
import com.hariyali.entity.Roles;
import com.hariyali.entity.Users;
import com.hariyali.exceptions.CustomException;
import com.hariyali.exceptions.CustomExceptionNodataFound;
import com.hariyali.repository.PackagesRepository;
import com.hariyali.repository.UsersRepository;
import com.hariyali.service.PackagesService;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hariyali.EnumConstants;
import com.hariyali.config.JwtHelper;

@Service
public class PackagesServiceImpl implements PackagesService {

	@Autowired
	private JwtHelper jwtHelper;

	@Autowired
	private PackageDao packageDao;

	@Autowired
	private PackagesRepository packageRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private UsersRepository usersRepository;

	// Get Package By Id
	@Override
	public ApiResponse<PackagesRequest> getByPackageId(String packageId) {
		ApiResponse<PackagesRequest> result = new ApiResponse<>();

		if (packageId == null) {
			throw new CustomExceptionNodataFound("Package Id Should be Required");
		}
		Packages p = this.packageRepository.findByPackageId(Integer.parseInt(packageId));

		if (p == null) {
			throw new CustomExceptionNodataFound("Package with Given Package Id Doesn't Exists");
		}
		Packages pkg = this.packageRepository.findPackageById(Integer.parseInt(packageId));

		if (pkg != null) {
			result.setData(modelMapper.map(pkg, PackagesRequest.class));
			result.setMessage("Package with package Id " + packageId + " Fetched Successfully..");
			result.setStatus(EnumConstants.SUCCESS);
			result.setStatusCode(HttpStatus.OK.value());
		}

		return result;

	}

	// Get Package By Title
	@Override
	public ApiResponse<PackagesRequest> getByPackageTitle(String packageTitle) {
		ApiResponse<PackagesRequest> result = new ApiResponse<>();

		PackagesRequest packages = this.packageDao.getPackageByTitle(packageTitle);

		if (packages != null) {
			result.setData(packages);
			result.setMessage("Package with package Id " + packageTitle + " Fetched Successfully..");
			result.setStatus(EnumConstants.SUCCESS);
			result.setStatusCode(HttpStatus.OK.value());

		}
		return result;
	}

	// Add Package
	@Override
	public ApiResponse<PackagesRequest> savePackage(JsonNode jsonNode, HttpServletRequest request) {

		ApiResponse<PackagesRequest> result = new ApiResponse<>();

		String token = request.getHeader("Authorization");

		String userName = jwtHelper.getUsernameFromToken(token.substring(7));

		Users userToken = this.usersRepository.findByEmailId(userName);

		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
		JsonNode packageNode = jsonNode.get("package");

		if (packageNode == null) {
			throw new CustomException("No data found In package");
		}

		Packages pkg = gson.fromJson(packageNode.toString(), Packages.class);
		Date newDate = new Date();

		// set created and updated date
		pkg.setCreatedDate(newDate);
		pkg.setModifiedDate(newDate);

		// set created by and modifiedBy
		pkg.setCreatedBy(userToken.getEmailId());
		pkg.setModifiedBy(userToken.getEmailId());

		// set status for package
		pkg.setActive(true);

		this.packageRepository.save(pkg);

		if (pkg != null) {

			result.setMessage("Package Saved Successfully..");
			result.setStatus(EnumConstants.SUCCESS);
			result.setStatusCode(HttpStatus.OK.value());

		}

		return result;

	}

	// Update Package
	@Override
	public ApiResponse<PackagesRequest> updatePackage(PackagesRequest pkg, int packageId) {
		ApiResponse<PackagesRequest> result = new ApiResponse<>();

		PackagesRequest packageRequest = this.packageDao.getPackageById(packageId);

		if (packageRequest != null) {

			packageRequest.setTitle(pkg.getTitle());
			packageRequest.setPrice(pkg.getPrice());
			packageRequest.setDescription(pkg.getDescription());
			packageRequest.setUpdatedAt(new Date());
			packageRequest.setUpdatedBy("Admin");

			this.packageDao.updatePackage(packageRequest, packageId);
		}

		if (packageRequest != null) {
			result.setData(packageRequest);
			result.setMessage("Package Updated Successfully..");
			result.setStatus(EnumConstants.SUCCESS);
			result.setStatusCode(HttpStatus.OK.value());

		}
		return result;
	}

	// Delete Package
	@Override
	public ApiResponse<PackagesRequest> deletePackageById(int packageId) {
		ApiResponse<PackagesRequest> result = new ApiResponse<>();

		PackagesRequest deletePackage = this.packageDao.deletePackageById(packageId);

		if (deletePackage != null) {
			result.setData(deletePackage);
			result.setMessage("Package Deleted Successfully..");
			result.setStatus(EnumConstants.SUCCESS);
			result.setStatusCode(HttpStatus.OK.value());

		}

		return result;

	}

	@Override
	public String getAllPackages() {
		String packages = packageRepository.getAllPackages();
		if (!packages.isEmpty())
			return packages;
		else
			throw new CustomExceptionNodataFound("No packages added in database");
	}

	@Override
	public ApiResponse<String> setActivePackagetoInactive(Date endDate) {
		ApiResponse<String> result = new ApiResponse<>();

		List<Packages> inactivePackages = this.packageRepository.findByEndDateBeforeAndActive(endDate, false);
		inactivePackages.forEach(packages -> packages.setActive(true));
		List<Packages> savedPackages = packageRepository.saveAll(inactivePackages);

		if (savedPackages != null) {
			result.setStatus("Package deActivated");
			result.setStatusCode(HttpStatus.OK.value());
		}
		return result;
	}
}
