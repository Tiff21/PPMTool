package com.hcltech.ppmtool.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.hcltech.ppmtool.domain.User;
import com.hcltech.ppmtool.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public User saveUser(User newUser) {
		newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
		
		// Username has to be unique (exception)
		newUser.setUsername(newUser.getUsername());
		// Make sure that password and confirmPassword match
		// we don't persist or show the confirmPassword
		newUser.setConfirmPassword("");
		return userRepository.save(newUser);
	} // saveUSer
	
	
} // class
