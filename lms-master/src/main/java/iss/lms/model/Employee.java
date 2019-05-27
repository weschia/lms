package iss.lms.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;


@Entity
@Table(name="employee")
public class Employee {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private int id;

	@NotBlank(message = "Please enter your username")
	@Column(name="Username")
	private String username;
	@NotBlank(message = "Please enter your password")
	@Column(name="Password")
	private String password;
	@Column(name="Employeetype")
	private String employeeType;
	@Column(name="Annualleave")
	private Integer annualLeave;
	@Column(name="Medicalleave")
	private Integer medicalLeave;
	@Column(name="Compleave")
	private Double compLeave;
	@Column(name="Managerid")
	private Integer managerId;

	public Employee() {

	}
	
	public Employee(String username, String password, String employeeType, int managerId) {
		this.username = username;
		this.password = password;
		this.employeeType = employeeType;
		annualLeave = 14;
		medicalLeave = 60;
		compLeave = 0.0;
		this.managerId = managerId;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmployeeType() {
		return employeeType;
	}
	public void setEmployeeType(String employeeType) {
		this.employeeType = employeeType;
	}
	public Integer getAnnualLeave() {
		return annualLeave;
	}
	public void setAnnualLeave(Integer annualLeave) {
		this.annualLeave = annualLeave;
	}
	public Integer getMedicalLeave() {
		return medicalLeave;
	}
	public void setMedicalLeave(Integer medicalLeave) {
		this.medicalLeave = medicalLeave;
	}
	public Double getCompLeave() {
		return compLeave;
	}
	public void setCompLeave(Double compLeave) {
		this.compLeave = compLeave;
	}
	public Integer getManagerId() {
		return managerId;
	}
	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
