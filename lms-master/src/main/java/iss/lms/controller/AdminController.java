package iss.lms.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import iss.lms.model.Employee;
import iss.lms.model.Leave;
import iss.lms.service.EmployeeRepository;
import iss.lms.service.LeaveRepository;
import iss.lms.utilities.HashPassword;


@Controller
public class AdminController {
	
	@Autowired
	private EmployeeRepository er;
	
	@Autowired
	private LeaveRepository lr;
	
	@Autowired
	private HashPassword hP;
	
	
	
	@RequestMapping("/admin")
	public String admin(Model model, @CookieValue("admin") String id) {
		Employee userdetails = er.findEmployeeById(Integer.parseInt(id));
		List<Employee> plist = er.findAll();
		ArrayList<Employee> managers = er.findManagers();
		model.addAttribute("plist",plist);
		model.addAttribute("userdetails", userdetails);
		model.addAttribute("managers", managers);
		model.addAttribute("employee",new Employee());
		model.addAttribute("addstatus","");
		return "admin";
	}
	@PostMapping("/adduser")
    public String adduser(@Valid Employee e,BindingResult br,Model model,@CookieValue("admin") String id) {
		model.addAttribute("employee",e);
		Employee userdetails = er.findEmployeeById(Integer.parseInt(id));
		List<Employee> plist = er.findAll();
		ArrayList<Employee> managers = er.findManagers();
		model.addAttribute("plist",plist);
		model.addAttribute("userdetails", userdetails);
		model.addAttribute("managers", managers);
		if (br.hasErrors()) {
			return "admin";
		}else if(er.findEmployeeByUsername(e.getUsername()) != null) {
			model.addAttribute("addstatus","Username already registered.");			
			return "admin";
		}
		else {
			
			int annualLeave = 0;
			int medicalLeave = 0;
			
			if(e.getEmployeeType().equals("Manager")) {
				annualLeave = 18;
				medicalLeave = 60;
			}
			else if(e.getEmployeeType().equals("Staff"))	{
				annualLeave = 14;
				medicalLeave = 60;
			}
			else if (e.getEmployeeType().equals("Admin")) {
				annualLeave = 0;
				medicalLeave = 0;				
			}
			
			
			e.setPassword(hP.hashPassword(e.getPassword()));
			e.setAnnualLeave(annualLeave);
			e.setMedicalLeave(medicalLeave);
			e.setCompLeave(0.0);
			er.save(e);	
			return "redirect:/admin";
		}
		
    }
	@PostMapping("/deleteuser")
	public String deleteuser(Employee e)
	{
		if(e.getEmployeeType().equals("Manager")) {
			ArrayList<Employee> subordinates = er.findSubordinates(e.getId());
			for(Employee sub : subordinates)
			{
				sub.setManagerId(null);
			}
		}
		List<Leave> deleteuser = lr.getLeaveByEmployeeId(e.getId());
		for(Leave l : deleteuser)
		{
			lr.delete(l);
		}
		er.delete(e.getId());

	    return "redirect:/admin";
	}
	@PostMapping("/updateuser")
	public String updateuser(Employee e)
	{
		if(!e.getPassword().equals(er.findEmployeeById(e.getId()).getPassword()))
		e.setPassword(hP.hashPassword(e.getPassword()));
		
		 er.modify(e.getUsername(), e.getPassword(), e.getEmployeeType(), e.getAnnualLeave(), e.getMedicalLeave(), e.getCompLeave(), e.getManagerId(), e.getId());
		 return "redirect:/admin";
	}
}
