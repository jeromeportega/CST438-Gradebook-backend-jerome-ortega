package com.cst438.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;

@RestController
public class EnrollmentController {

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	EnrollmentRepository enrollmentRepository;

	/*
	 * Endpoint used by registration service to add an enrollment to an existing
	 * course.
	 */
	@PostMapping("/enrollment")
	@Transactional
	public EnrollmentDTO addEnrollment(@RequestBody EnrollmentDTO enrollmentDTO) {
		System.out.println("Add enrollment for student: " + enrollmentDTO.studentEmail);

		// Check that the course exists before inserting the enrollment.
		Course course = checkCourseExists(enrollmentDTO.course_id);
		
		// Create enrollment object from DTO.
		Enrollment enrollment = new Enrollment();
		enrollment.setCourse(course);
		enrollment.setStudentName(enrollmentDTO.studentName);
		enrollment.setStudentEmail(enrollmentDTO.studentEmail);
		
		// Insert the enrollment into the database.
		enrollmentRepository.save(enrollment);
		
		return enrollmentDTO;
	}
	

	/**
	 * Verify that the course exists for a given course ID and return the course if it does.
	 * @param courseId
	 * @return Course
	 */
	private Course checkCourseExists(int courseId) {
		// get course
		Course course = courseRepository.findById(courseId).orElse(null);
		if (course == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course not found. " + courseId);
		}

		return course;
	}

}
 