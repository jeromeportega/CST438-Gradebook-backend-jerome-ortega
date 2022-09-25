package com.cst438.services;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Course;
import com.cst438.domain.CourseDTOG;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;


public class RegistrationServiceMQ extends RegistrationService {

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public RegistrationServiceMQ() {
		System.out.println("MQ registration service ");
	}

	// ----- configuration of message queues

	@Autowired
	Queue registrationQueue;

	// ----- end of configuration of message queue

	// receiver of messages from Registration service
	
	@RabbitListener(queues = "gradebook-queue")
	@Transactional
	public void receive(EnrollmentDTO enrollmentDTO) {
		System.out.println("Adding enrollment for student via RABBITMQ: " + enrollmentDTO.studentEmail);

		// Check that the course exists before inserting the enrollment.
		Course course = checkCourseExists(enrollmentDTO.course_id);
		
		// Create enrollment object from DTO.
		Enrollment enrollment = new Enrollment();
		enrollment.setCourse(course);
		enrollment.setStudentName(enrollmentDTO.studentName);
		enrollment.setStudentEmail(enrollmentDTO.studentEmail);
		
		// Insert the enrollment into the database.
		enrollmentRepository.save(enrollment);
	}

	// sender of messages to Registration Service
	@Override
	public void sendFinalGrades(int course_id, CourseDTOG courseDTOG) {
		// Converting courseDTOG and sending to the registrationQueue.
		System.out.println("Sending final grades via RABBITMQ");
		rabbitTemplate.convertAndSend(registrationQueue.getName(), courseDTOG);
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
