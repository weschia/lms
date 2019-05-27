package iss.lms.model;

import java.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="leavetaken")
public class Leave {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="Leaveid")
	private int leaveid;
	@Column(name="Employeeid")
	private int employeeid;
	@Column(name="Startdate")
	@NotNull(message = "Please enter a start date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	@NotNull(message = "Please enter an end date")
	@Column(name="Enddate")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;
	@Column(name="Leavetype")
	private String leaveType;
	@Column(name="Status")
	private String status;
	@NotBlank(message = "Please enter a reason for taking leave")
	@Column(name="Reason")
	private String reason;
	@Column(name="Managercomment")
	private String managerComment;

	public Leave() {
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public int getLeaveid() {
		return leaveid;
	}

	public void setLeaveid(int leaveid) {
		this.leaveid = leaveid;
	}

	public int getEmployeeid() {
		return employeeid;
	}

	public void setEmployeeid(int employeeid) {
		this.employeeid = employeeid;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getManagerComment() {
		return managerComment;
	}

	public void setManagerComment(String managerComment) {
		this.managerComment = managerComment;
	}

}
