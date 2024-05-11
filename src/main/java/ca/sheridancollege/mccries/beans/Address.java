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
public class Address {
	
	@NonNull
	private Long addressId;
	
	private String streetName;
	
	private String streetNumber;
	
	private String city;
	
	private String province;
	
	private String postalCode;
	
	
	
	

}
