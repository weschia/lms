package iss.lms.service;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import iss.lms.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
	@Query("SELECT e FROM Employee e where e.username = :username AND e.password = :password")
	Employee employeeLogin(@Param("username") String username, @Param("password") String password);
	@Query("SELECT e FROM Employee e where e.id = :id")
	Employee findEmployeeById(@Param("id") int id);
	@Query("SELECT e FROM Employee e where e.username = :username")
	Employee findEmployeeByUsername(@Param("username") String username);
	@Query("SELECT e FROM Employee e where e.managerId = :managerId")
	ArrayList<Employee> findSubordinates(@Param("managerId") Integer managerid);

	@Query("SELECT e FROM Employee e where e.employeeType = 'Manager'")
	ArrayList<Employee> findManagers();
	
	@Transactional
	@Modifying
	@Query("UPDATE Employee e set e.username = ?1, e.password =?2, e.employeeType = ?3, e.annualLeave = ?4, e.medicalLeave = ?5, e.compLeave = ?6, e.managerId = ?7 WHERE e.id =?8")
	void modify(String username, String password, String employeeType, Integer annualLeave, Integer medicalLeave, Double compLeave, Integer managerId, int id);
	
	@Transactional
	@Modifying
	@Query("DELETE from Employee e where e.id =?1")
	void delete(int id);
}
