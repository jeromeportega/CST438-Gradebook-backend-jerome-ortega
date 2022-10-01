package com.cst438.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.CourseDTOG;

public class RegistrationServiceREST extends RegistrationService {

	
	RestTemplate restTemplate = new RestTemplate();
	
	@Value("${registration.url}") 
	String registration_url;
	
	public RegistrationServiceREST() {
		System.out.println("REST registration service ");
	}
	
	@Override
	public void sendFinalGrades(int course_id , CourseDTOG courseDTOG) {
		System.out.println("Sending grades to registration service");
		
		// Construct URL
	    final String url = registration_url + "/course/" + course_id;
	    
	    // Send the put request.
	    restTemplate.put(url, courseDTOG);
	}
}
