package com.hariyali.daoimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hariyali.EnumConstants;
import com.hariyali.dao.PackageDao;
import com.hariyali.dto.ApiResponse;
import com.hariyali.dto.PackagesRequest;
import com.hariyali.entity.Packages;
import com.hariyali.exceptions.CustomExceptionNodataFound;
import com.hariyali.repository.PackagesRepository;

@Component
public class PackagedaoImpl implements PackageDao {

	@Autowired
	private PackagesRepository packageRepo;

	@Override
	public PackagesRequest savePackage(PackagesRequest pkg) {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		Packages savedPackage = mapper.convertValue(pkg, Packages.class);

		this.packageRepo.save(savedPackage);

		PackagesRequest packageRequestObj = mapper.convertValue(savedPackage, PackagesRequest.class);

		if (packageRequestObj != null) {
			return packageRequestObj;
		}

		else {

			throw new CustomExceptionNodataFound("Unable to save Package");
		}

	}

	@Override
	public PackagesRequest getPackageById(int packageId) {

		Optional<Packages> pkgIdOptional = this.packageRepo.findById(packageId);
		if (pkgIdOptional.isPresent()) {
			ModelMapper modelMapper = new ModelMapper();
			return modelMapper.map(pkgIdOptional.get(), PackagesRequest.class);

		}
		throw new CustomExceptionNodataFound("Package with Package Id " + packageId + "not Found");

	}

	@Override
	public PackagesRequest getPackageByTitle(String packageTitle) {
		return null;

//		Optional<Packages> packageOptional = Optional.ofNullable(this.packageRepo.findByTitle(packageTitle));
//
//		if (packageOptional.isPresent()) {
//			ModelMapper modelMapper = new ModelMapper();
//			return modelMapper.map(packageOptional.get(), PackagesRequest.class);
//
//		}
//
//		throw new CustomExceptionNodataFound("Package with title " + packageTitle + " Not Found");

	}

	@Override
	public Boolean existsByTitle(String packageTitle) {

		return null;
	}

	@Override
	public PackagesRequest updatePackage(PackagesRequest packages, int packageId) {

		Optional<Packages> pkg = this.packageRepo.findById(packageId);
		if (pkg.isPresent()) {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			Packages savedPackage = mapper.convertValue(pkg, Packages.class);

			this.packageRepo.save(savedPackage);

			PackagesRequest packageRequestObj = mapper.convertValue(savedPackage, PackagesRequest.class);

			if (packageRequestObj != null) {
				return packageRequestObj;
			}

			else {
				throw new CustomExceptionNodataFound("Unable to save Package");
			}
		}
		throw new CustomExceptionNodataFound("Given Id doesn't Exists.");
	}

	@Override
	public PackagesRequest deletePackageById(int packageId) {

		Optional<Packages> pkg = this.packageRepo.findById(packageId);
		if (pkg.isPresent()) {

			pkg.get().setIsdeleted(true);
			this.packageRepo.save(pkg.get());
			ModelMapper modelMapper = new ModelMapper();
			return modelMapper.map(pkg.get(), PackagesRequest.class);

		}
		throw new CustomExceptionNodataFound("Package with Package Id " + packageId + " Not Found");

	}

//	 @Override
//	    public ApiResponse<Map<String,List<Map<String,Object>>>> getAllPackages() {
//	        ApiResponse<Map<String,List<Map<String,Object>>>> response=new ApiResponse<>();
//	        List<Object[]> packages = entityManager.createNativeQuery("SELECT package_name AS packageName, bouquet_price AS bouquetPrice, maintenance_cost AS maintenanceCost, package_description AS packageDescription FROM tbl_packages").getResultList();
//
//	        if(packages.isEmpty())
//	        {
//	            throw new CustomExceptionNodataFound("There are no packages created");
//	        }
//	        else
//	        {
//	            List<Map<String,Object>> dataList = new ArrayList<>();
//	            for (Object[] row : packages) {
//	                Map<String,Object> data = new HashMap<>();
//	                data.put("packageName", row[0]);
//	                data.put("bouquetPrice", row[1]);
//	                data.put("maintenanceCost", row[2]);
//	                data.put("packageDescription", row[3]);
//	                dataList.add(data);
//	            }
//	            Map<String,List<Map<String,Object>>> data = new HashMap<>();
//	            data.put("data", dataList);
//
//	            response.setData(data);
//	            response.setStatus(EnumConstants.SUCCESS);
//	            response.setStatusCode(HttpStatus.OK.value());
//	            response.setMessage("All Packages Retrieved successfully..!!");
//	        }
//
//	        return response;
//	    }

//	 @Override
//	 public ApiResponse<List<Map<String, Object>>> getAllPackages() {
//	     ApiResponse<List<Map<String, Object>>> response = new ApiResponse<>();
//	     List<Map<String, Object>> packages = new ArrayList<>();
//
//	     List<Object[]> result = packageRepo.getAllPackages();
//
//	     if(result.isEmpty()) {
//	         throw new CustomExceptionNodataFound("There are no packages created");
//	     } else {
//	         for(Object[] row : result) {
//	             Map<String, Object> packageData = new HashMap<>();
//	             packageData.put("packageName", row[0]);
//	             packageData.put("bouquetPrice", row[1]);
//	             packageData.put("maintenanceCost", row[2]);
//	             packageData.put("packageDescription", row[3]);
//	             packages.add(packageData);
//	         }
//
//	         response.setData(packages);
//	         response.setStatus(EnumConstants.SUCCESS);
//	         response.setStatusCode(HttpStatus.OK.value());
//	         response.setMessage("All Packages Retrieved successfully..!!");
//	     }
//
//	     return response;
//	 }

	@Override
	public List<Map<String, Object>> getAllPackages() {
		/*
		 * List<Object[]> packages = packageRepo.getAllPackages(); List<Map<String,
		 * Object>> response = new ArrayList<>(); for (Object[] obj : packages) {
		 * Map<String, Object> packageData = new HashMap<>();
		 * packageData.put("packageId", obj[0]); packageData.put("packageName", obj[1]);
		 * packageData.put("packageDescription", obj[2]);
		 * packageData.put("bouquetPrice", obj[3]); packageData.put("maintenanceCost",
		 * obj[4]); packageData.put("startDate", obj[6]); packageData.put("endDate",
		 * obj[7]);
		 * 
		 * response.add(packageData); }
		 */
		return null;
	}

}
