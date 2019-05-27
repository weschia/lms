package iss.lms.utilities;

import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

@Component("hP")
public class HashPassword {

	public String hashPassword(String password) {
		String hashedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
		
		return hashedPassword;
	}
	
	
}
