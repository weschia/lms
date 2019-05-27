package iss.lms.utilities;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import iss.lms.model.Employee;
import iss.lms.model.Leave;
import iss.lms.service.EmployeeRepository;
import iss.lms.service.LeaveRepository;

@Component("lU")
public class LeaveUtility {
	
	@Autowired
	private EmployeeRepository er;
	
	@Autowired
	private LeaveRepository lr;
	
	public static ArrayList<String> publicHolidays = new ArrayList<String>() {
	{
		add("2019-05-01");
		add("2019-05-20");
		add("2019-06-05");
		add("2019-08-09");
		add("2019-08-12");
		add("2019-11-28");
		add("2019-12-25");
	}};
	
	
	public int computeDays(Leave l)
	{
		LocalDate i = l.getStartDate();
		LocalDate j = l.getEndDate();
		long daysLeave = ChronoUnit.DAYS.between(i, j.plusDays(1));
			if(daysLeave <=14) {
				for(LocalDate k = i; !k.isAfter(j); k = k.plusDays(1))
				{
					if(k.getDayOfWeek().equals(DayOfWeek.SATURDAY) || k.getDayOfWeek().equals(DayOfWeek.SUNDAY) || publicHolidays.contains(k.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
						daysLeave = daysLeave - 1;
				}
			}
	return (int) daysLeave;
	}
	public boolean processLeave(Leave l)
	{
		Employee e = er.findEmployeeById(l.getEmployeeid());
		double daysLeave = computeDays(l);
		if(l.getLeaveType().equals("Annual")) {
			if(e.getAnnualLeave() >= daysLeave) {
				e.setAnnualLeave(e.getAnnualLeave() - (int)daysLeave);
				er.save(e);
				return true;
				}
			else return false;
		}
		else if(l.getLeaveType().equals("Medical")) {
			if(e.getMedicalLeave()>= daysLeave) {
				e.setMedicalLeave(e.getMedicalLeave() - (int)daysLeave);
				er.save(e);
				return true;
				}
			else return false;
		}
		else if(l.getLeaveType().equals("Compensation")){
			if(e.getCompLeave() >= daysLeave) {
				e.setCompLeave(e.getCompLeave() - daysLeave);
				er.save(e);
				return true;
				}
			else return false;
		}
		else return false;
	}
	public void refundLeave(Leave l) {
		Employee e = er.findEmployeeById(l.getEmployeeid());
		double daysLeave = computeDays(l);
		if(l.getLeaveType().equals("Annual")) {
			e.setAnnualLeave(e.getAnnualLeave() + (int)daysLeave);
		}
		else if(l.getLeaveType().equals("Medical")) {
			e.setMedicalLeave(e.getMedicalLeave() + (int)daysLeave);
		}
		else if(l.getLeaveType().equals("Compensation")) {
			e.setCompLeave(e.getCompLeave() + daysLeave);
		}
		er.save(e);
	}
	public boolean checkNoLeaveConflict(Leave l) {
		ArrayList<Leave> allleave = lr.getLeaveByEmployeeId(l.getEmployeeid());
		for(Leave m : allleave)
		{
			if(!m.getStartDate().isAfter(l.getEndDate()) && !m.getEndDate().isBefore(l.getStartDate()) && (m.getStatus().equals("Applied") || m.getStatus().equals("Updated") || m.getStatus().equals("Approved")))
				return false;
		}
		return true;
	}
	public boolean checkOthersOnLeave(Leave toreview, Leave other) {
		if(!toreview.getStartDate().isAfter(other.getEndDate()) && !toreview.getEndDate().isBefore(other.getStartDate()) && other.getStatus().equals("Approved"))
			return true;
		return false;
	}
	public boolean checkValidDates(Leave l)
	{
		LocalDate i = l.getStartDate();
		LocalDate j = l.getEndDate();
		if(i.isAfter(j))
			return false;
		if(i.getDayOfWeek().equals(DayOfWeek.SATURDAY) || i.getDayOfWeek().equals(DayOfWeek.SUNDAY) ||j.getDayOfWeek().equals(DayOfWeek.SATURDAY) || j.getDayOfWeek().equals(DayOfWeek.SUNDAY) || publicHolidays.contains(i.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) || publicHolidays.contains(j.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
			return false;
		return true;
	}
}
