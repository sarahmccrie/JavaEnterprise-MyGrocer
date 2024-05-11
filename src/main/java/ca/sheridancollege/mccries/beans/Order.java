package ca.sheridancollege.mccries.beans;

import java.util.Date;

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
public class Order {
	
	@NonNull
	private Long orderId;
	
	private Date orderDate;
	
	private String orderStatus;
	
	private Long customerId;
	
	private double orderAmount;

}
