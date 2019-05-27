package iss.lms.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import iss.lms.model.Employee;
import iss.lms.model.Leave;
import iss.lms.service.EmployeeRepository;
import iss.lms.service.LeaveRepository;
import iss.lms.utilities.LeaveUtility;


@Controller
public class ManagerController {

	@Autowired
	private EmployeeRepository er;
	
	@Autowired
	private LeaveRepository lr;
	@Autowired
	private LeaveUtility lU ;
	
	
	@RequestMapping("/viewsubleave")
	public String viewSubLeave(Model model, @CookieValue("id") String id) {
		Employee f = er.findEmployeeById(Integer.parseInt(id));
		model.addAttribute("user", f);
		ArrayList<Employee> elist = er.findSubordinates(Integer.parseInt(id));
		ArrayList<Leave> llist = new ArrayList<Leave>();
		for(Employee sub : elist) {
			ArrayList<Leave> leaveperemployee = lr.getLeaveByEmployeeId(sub.getId());
			llist.addAll(leaveperemployee);
		}
		model.addAttribute("subleave", llist);
		return "viewsubleave";
	}
	@RequestMapping("/viewpending")
	public String viewPendingApprovals(Model model, @CookieValue("id") String id) {
		Employee f = er.findEmployeeById(Integer.parseInt(id));
		model.addAttribute("user", f);
		ArrayList<Employee> elist = er.findSubordinates(Integer.parseInt(id));
		ArrayList<Leave> llist = new ArrayList<Leave>();
		for(Employee sub : elist) {
			ArrayList<Leave> leaveperemployee = lr.getLeaveByEmployeeId(sub.getId());
			llist.addAll(leaveperemployee);
		}
		model.addAttribute("subleave", llist);
		return "viewpending";
	}
	@RequestMapping("/approveform/{leaveId}")
	public String approveForm(Model model, @CookieValue("id") String id, @PathVariable(value="leaveId") int leaveid) {
		Employee e = er.findEmployeeById(Integer.parseInt(id));
		model.addAttribute("user", e);
		Leave l = lr.getLeaveById(leaveid);
		model.addAttribute("leave", l);
		String subname = er.findEmployeeById(l.getEmployeeid()).getUsername();
		model.addAttribute("subordinate", subname);
		
		
		ArrayList<Employee> subordinates = er.findSubordinates(Integer.parseInt(id));
		ArrayList<Leave> allsubordinateleaves = new ArrayList<Leave>();
		for(Employee sub : subordinates) {
			ArrayList<Leave> leaveperemployee = lr.getLeaveByEmployeeId(sub.getId());
			allsubordinateleaves.addAll(leaveperemployee);
		}
		ArrayList<Leave> conflictingleaves = new ArrayList<Leave>();
		for(Leave others : allsubordinateleaves) {
			if(lU.checkOthersOnLeave(l, others))
				conflictingleaves.add(others);
		}
		model.addAttribute("conflicts", conflictingleaves);	
		
		return "approveform";
	}
	@RequestMapping("/rejectform/{leaveId}")
	public String rejectForm(Model model, @CookieValue("id") String id, @PathVariable(value="leaveId") int leaveid) {
		Employee e = er.findEmployeeById(Integer.parseInt(id));
		model.addAttribute("user", e);
		Leave l = lr.getLeaveById(leaveid);
		model.addAttribute("leave", l);
		String subname = er.findEmployeeById(l.getEmployeeid()).getUsername();
		model.addAttribute("subordinate", subname);
		
		
		ArrayList<Employee> subordinates = er.findSubordinates(Integer.parseInt(id));
		ArrayList<Leave> allsubordinateleaves = new ArrayList<Leave>();
		for(Employee sub : subordinates) {
			ArrayList<Leave> leaveperemployee = lr.getLeaveByEmployeeId(sub.getId());
			allsubordinateleaves.addAll(leaveperemployee);
		}
		ArrayList<Leave> conflictingleaves = new ArrayList<Leave>();
		for(Leave others : allsubordinateleaves) {
			if(lU.checkOthersOnLeave(l, others))
				conflictingleaves.add(others);
		}
		model.addAttribute("conflicts", conflictingleaves);	
		
		
		
		return "rejectform";
	}
	@PostMapping("/approve/{leaveId}")
	public String approveLeave(@ModelAttribute("leave") Leave l, Model model, @CookieValue("id") String id, @PathVariable(value="leaveId") int leaveid) {
		l.setStatus("Approved");
		lr.save(l);
		return "redirect:/viewsubleave";
	}
	@PostMapping("/reject/{leaveId}")
	public String rejectLeave(@ModelAttribute("leave") Leave l, Model model, @CookieValue("id") String id, @PathVariable(value="leaveId") int leaveid) {
		Employee e = er.findEmployeeById(Integer.parseInt(id));
		model.addAttribute("user", e);
		model.addAttribute("leave", l);
		String subname = er.findEmployeeById(l.getEmployeeid()).getUsername();
		model.addAttribute("subordinate", subname);
		
		
		ArrayList<Employee> subordinates = er.findSubordinates(Integer.parseInt(id));
		ArrayList<Leave> allsubordinateleaves = new ArrayList<Leave>();
		for(Employee sub : subordinates) {
			ArrayList<Leave> leaveperemployee = lr.getLeaveByEmployeeId(sub.getId());
			allsubordinateleaves.addAll(leaveperemployee);
		}
		ArrayList<Leave> conflictingleaves = new ArrayList<Leave>();
		for(Leave others : allsubordinateleaves) {
			if(lU.checkOthersOnLeave(l, others))
				conflictingleaves.add(others);
		}
		model.addAttribute("conflicts", conflictingleaves);	
				
		
		if(l.getManagerComment().equals(""))
		{
			model.addAttribute("errormsg","You need to enter comments if rejecting a leave application.");
			return "rejectform";}
		else {
		l.setStatus("Rejected");
		lr.save(l);
		lU.refundLeave(l);
		}
		return "redirect:/viewsubleave";
	}
	
	
}
