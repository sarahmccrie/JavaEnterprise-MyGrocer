package ca.sheridancollege.mccries.beans;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/* Name: Sarah McCrie 991405606
* Assignment: Assignment #3
* Date: November 23, 2023
* Program: A3_mccries
*/

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Customer {
	
	@NonNull
	private Long customerId;
	
	private String firstName;
	
	private String lastName;
	
	private Long userId;

	
	
}
