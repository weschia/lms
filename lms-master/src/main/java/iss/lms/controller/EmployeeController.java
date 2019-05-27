package iss.lms.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import iss.lms.model.Employee;
import iss.lms.model.Leave;
import iss.lms.service.*;
import iss.lms.utilities.LeaveUtility;

@Controller
public class EmployeeController {


	@Autowired
	private EmployeeRepository er;
	@Autowired
	private LeaveRepository lr;
	@Autowired
	private LeaveUtility lU ;
	
	public void setRepo(EmployeeRepository er, LeaveRepository lr)	{
		this.er = er;
		this.lr = lr;
	}
		
	@RequestMapping("/index")
	public String index(Model model, @CookieValue("id") String id) {
		Employee e = er.findEmployeeById(Integer.parseInt(id));
		model.addAttribute("user", e);
		LocalDate now = LocalDate.now();
		model.addAttribute("year",now.getYear());
		model.addAttribute("month",now.getMonthValue()-1);
		return "index";
	}
	@RequestMapping("/applyform")
	public String applyForm(Model model, @CookieValue("id") String id) {
		Employee e = er.findEmployeeById(Integer.parseInt(id));
		model.addAttribute("user", e);
		model.addAttribute("leave", new Leave());
		model.addAttribute("errormsg","");
		return "applyform";
	}
	@PostMapping("/apply")
	public String applyLeave(@Valid @ModelAttribute("leave") Leave l, BindingResult br, Model model, @CookieValue("id") String id) {
		Employee e = er.findEmployeeById(Integer.parseInt(id));
		model.addAttribute("user", e);
		if(br.hasErrors())
		{	
			return "applyform";
		}
		l.setStatus("Applied");
		if(lU.checkValidDates(l))
			if(lU.checkNoLeaveConflict(l))
				if(lU.processLeave(l))
					lr.save(l);
				else {
					model.addAttribute("errormsg","Insufficient leave.");
					return "applyform";}
			else {
				model.addAttribute("errormsg","Your leave conflicts with another instance of your own leave.");
				return "applyform";}
		else {
			model.addAttribute("errormsg","Your leave end date must be after your start date, and your leave must start and end on working days.");
			return "applyform";
			}
		return "redirect:/viewleave";
	}
	@RequestMapping("/viewleave")
	public String viewLeave(Model model, @CookieValue("id") String id) {
		Employee f = er.findEmployeeById(Integer.parseInt(id));
		model.addAttribute("user", f);
		ArrayList<Leave> llist = lr.getLeaveByEmployeeId(Integer.parseInt(id));
		model.addAttribute("leave", llist);
		return "viewleave";
	}
	
	@RequestMapping("/updateform/{leaveId}")
	public String updateForm(Model model, @CookieValue("id") String id, @PathVariable(value="leaveId") int leaveid, HttpSession session) {
		Employee e = er.findEmployeeById(Integer.parseInt(id));
		model.addAttribute("user", e);
		Leave leave = lr.getLeaveById(leaveid);
		model.addAttribute("leave", leave);
		return "updateform";
	}
	@PostMapping("/update/{leaveId}")
	public String updateLeave(@Valid Leave l, BindingResult br, Model model, @PathVariable(value="leaveId") int leaveid, @CookieValue("id") String id) {
		
		Employee e = er.findEmployeeById(Integer.parseInt(id));
		model.addAttribute("user", e);
		model.addAttribute("leave", l);
		if(br.hasErrors())
		{	
			return "updateform";
			}
		else {
			if(lU.checkValidDates(l)){
				
				Leave oldLeave = lr.getLeaveById(l.getLeaveid());
				oldLeave.setStatus("temp");
				lr.save(oldLeave);
				if(lU.checkNoLeaveConflict(l))
				{	
					if(lU.processLeave(l)) {
						lU.refundLeave(oldLeave);
						l.setStatus("Updated");
						lr.save(l);}
					else {
						oldLeave.setStatus("Updated");
						lr.save(oldLeave);
						model.addAttribute("errormsg","Insufficient leave balance.");
						return "updateform"; }
				}
				else {
					oldLeave.setStatus("Updated");
					lr.save(oldLeave);
					model.addAttribute("errormsg","Updated leave conflicts with another instance of your own leave.");
					return "updateform";}
				}
			else {
				model.addAttribute("errormsg","Your leave end date must be after your start date, and your leave must start and end on working days.");
				return "updateform";}
		return "redirect:/viewleave";
		}
	}
	@RequestMapping("/delete/{leaveId}")
	public String deleteLeave(@CookieValue("id") String id, @PathVariable(value="leaveId") int leaveid, Model model) {
		Leave l = lr.getLeaveById(leaveid);
		l.setStatus("Deleted");
		lr.save(l);
		lU.refundLeave(l);
		return "redirect:/viewleave";
	}
	@RequestMapping("/cancel/{leaveId}")
	public String cancelLeave(@CookieValue("id") String id, @PathVariable(value="leaveId") int leaveid, Model model) {
		Leave l = lr.getLeaveById(leaveid);
		l.setStatus("Cancelled");
		lr.save(l);
		lU.refundLeave(l);
		return "redirect:/viewleave";
	}
	
	@RequestMapping("/movementregister/{year}/{month}")
	public String movementRegister(Model model, @CookieValue("id") String id,@PathVariable(value="year") int year,@PathVariable(value="month") int month) {
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, 1);
		LocalDate monthStart = c.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate monthEnd = monthStart.with(TemporalAdjusters.lastDayOfMonth());
		List<Leave> allLeaves = lr.findAll(new Sort(Sort.Direction.ASC,"startDate"));
		ArrayList<Leave> monthlyLeave = new ArrayList<Leave>();
		for(Leave l : allLeaves) {
			if(!l.getStartDate().isAfter(monthEnd) && !l.getEndDate().isBefore(monthStart) && l.getStatus().equals("Approved"))
				monthlyLeave.add(l);
		}
		model.addAttribute("mregister",monthlyLeave);	
		model.addAttribute("currentmonth", monthStart.getMonth());
		model.addAttribute("currentyear", monthStart.getYear());
		model.addAttribute("nextyear",monthStart.plusMonths(1).getYear());
		model.addAttribute("nextmonth",monthStart.plusMonths(1).getMonthValue()-1);
		model.addAttribute("prevyear",monthStart.minusMonths(1).getYear());
		model.addAttribute("prevmonth",monthStart.minusMonths(1).getMonthValue()-1);
		return "movementregister";
	}
	
	@PostMapping("/movementregister")
	public String movementRegisterJump(Model model, @CookieValue("id") String id, @ModelAttribute ("month") int month, @ModelAttribute("year") int year) {
		return "redirect:/movementregister/"+year+"/"+month;
	}
	


}