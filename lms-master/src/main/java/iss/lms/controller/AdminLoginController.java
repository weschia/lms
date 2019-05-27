package iss.lms.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import iss.lms.model.Employee;
import iss.lms.service.EmployeeRepository;
import iss.lms.utilities.HashPassword;

@Controller
public class AdminLoginController {

	@Autowired
	private EmployeeRepository er;
	@Autowired
	private HashPassword hP;
	
	@RequestMapping("/alogin")
	public String adminLogin(Model model) {
		model.addAttribute("user", new Employee());
		model.addAttribute("loginstatus","");
		return "alogin";
	}
	@RequestMapping("/alogout")
	public String adminLogout(Model model, @CookieValue("admin") String id, HttpServletResponse hr ) {
		Cookie session = new Cookie("admin",id);
		session.setMaxAge(0);
		hr.addCookie(session);
		model.addAttribute("user", new Employee());
		model.addAttribute("loginstatus","Successfully logged out.");
		return "alogin";
	}
	@PostMapping("/adminlogin")
	public String doAdminLogin(@Valid @ModelAttribute ("user")Employee e, BindingResult br, Model model, HttpServletResponse hr ) {
		if(br.hasErrors()) {
			return "alogin";
		}
		
		e.setPassword(hP.hashPassword(e.getPassword()));
		
		Employee f = er.employeeLogin(e.getUsername(), e.getPassword());
		if (f == null)
		{
			model.addAttribute("loginstatus","Invalid username or password.");
			return "alogin";
		}
		else if(!f.getEmployeeType().equals("Admin")) {
			model.addAttribute("loginstatus","You need admin permissions to log in.");
			return "alogin";
		}
		else {
			int id = f.getId();
			Cookie session = new Cookie("admin",String.valueOf(id));
			session.setMaxAge(10000);
			hr.addCookie(session);
			return "redirect:admin";
			}
	}

	
}
