package com.hariyali.config;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.hariyali.entity.Packages;
import com.hariyali.repository.PackagesRepository;

@Configuration
@EnableScheduling
public class SchedularConfig {


	@Autowired
    private PackagesRepository packageRepository;

	 //@Scheduled(fixedDelay = 100000)
   @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Kolkata") // Run every day at midnight
	public void deactivatePackagesOnEndDate() {
        System.err.println("Scheduler started");

        // Get the current date
        Date currentDate = new Date();

        // Retrieve packages with endDate before the current date and active is true
        List<Packages> packagesToDeactivate = packageRepository.findByEndDateBeforeAndActive(currentDate, true);

        // Set the active field to false for the retrieved packages
        if(packagesToDeactivate!=null)
        {
        	packagesToDeactivate.forEach(pkg -> pkg.setActive(false));
        	   packageRepository.saveAll(packagesToDeactivate);
        }
        
    
    }
}
