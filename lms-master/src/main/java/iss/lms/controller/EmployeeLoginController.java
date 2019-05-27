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
public class EmployeeLoginController {
	
	@Autowired
	private EmployeeRepository er;
	
	@Autowired 
	private HashPassword hP;
	
	@RequestMapping("/login")
	public String login(Model model) {
		model.addAttribute("user", new Employee());
		model.addAttribute("loginstatus","");
		return "login";
	}
	@RequestMapping("/logout")
	public String logout(Model model, @CookieValue("id") String id, HttpServletResponse hr ) {
		Cookie session = new Cookie("id",id);
		session.setMaxAge(0);
		hr.addCookie(session);
		model.addAttribute("user", new Employee());
		model.addAttribute("loginstatus","Successfully logged out.");
		return "login";
	}
	@PostMapping("/login")
	public String doLogin(@Valid @ModelAttribute ("user")Employee e, BindingResult br, Model model, HttpServletResponse hr ) {
		if(br.hasErrors()) {
			return "login";
		}
		
		e.setPassword(hP.hashPassword(e.getPassword()));
		System.out.println(hP.hashPassword(e.getPassword()));
		Employee f = er.employeeLogin(e.getUsername(), e.getPassword());
		if (f == null)
		{
			model.addAttribute("loginstatus","Invalid username or password.");
			return "login";
		}
		else if(f.getEmployeeType().equals("Admin")) {
			model.addAttribute("loginstatus","Please use the admin login page instead.");
			return "login";
		}
		else {
			int id = f.getId();
			Cookie session = new Cookie("id",String.valueOf(id));
			session.setMaxAge(10000);
			hr.addCookie(session);
			return "redirect:index";
			}
	}
	
}
