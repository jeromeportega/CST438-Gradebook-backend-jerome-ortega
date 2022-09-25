package com.cst438.domain;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AssignmentRepository extends CrudRepository <Assignment, Integer> {

	@Query("select a from Assignment a where a.needsGrading=1 and a.dueDate < current_date and a.course.instructor= :email order by a.id")
	List<Assignment> findNeedGradingByEmail(@Param("email") String email);
	
	@Modifying
	@Query(value = "INSERT INTO assignment (name, due_date, course_id, needs_grading) VALUES (:assignmentName, :dueDate, :courseId, 1)", nativeQuery = true)
	void insertAssignment(@Param("assignmentName") String assignmentName, @Param("dueDate") String dueDate, @Param("courseId") int courseId);
	
	@Modifying
	@Query("update Assignment a set a.name = :assignmentName where a.id = :assignmentId")
	void updateAssignmentName(@Param("assignmentName") String assignmentName, @Param("assignmentId") int assignmentId);
}
