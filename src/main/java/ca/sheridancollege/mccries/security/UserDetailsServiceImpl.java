package ca.sheridancollege.mccries.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ca.sheridancollege.mccries.database.DatabaseAccess;

/* Name: Sarah McCrie 991405606
* Assignment: Assignment #3
* Date: November 23, 2023
* Program: A3_mccries
*/

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	@Lazy
	DatabaseAccess da;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		ca.sheridancollege.mccries.beans.User user = da.findUserAccount(username);
		if (user == null) {
			System.out.println("User not found: " + username);
			throw new UsernameNotFoundException("User " + username + " was not found in the database");
		}
		
		List<String> roleNameList = da.getRolesById(user.getUserId());
	
		
		List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
		if (roleNameList != null) {
			for (String role : roleNameList) {
				grantList.add(new SimpleGrantedAuthority(role));
			}
		}
		
		UserDetails userDetails = (UserDetails) new org.springframework.security.core.userdetails.User(user.getEmail(),
				user.getEncryptedPassword(), grantList);
		
		return userDetails;
		
	}
	
	
	
	
	

}
