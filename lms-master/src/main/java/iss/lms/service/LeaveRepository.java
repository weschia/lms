package iss.lms.service;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import iss.lms.model.Leave;

public interface LeaveRepository extends JpaRepository<Leave, Integer>{
	@Query("SELECT l FROM Leave l where l.employeeid = :id order by startdate asc")
	ArrayList<Leave> getLeaveByEmployeeId(@Param("id") int id);
	@Query("SELECT l FROM Leave l where l.leaveid = :id")
	Leave getLeaveById(@Param("id") int id);
}
