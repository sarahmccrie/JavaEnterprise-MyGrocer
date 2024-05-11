package ca.sheridancollege.mccries.database;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import ca.sheridancollege.mccries.beans.Address;
import ca.sheridancollege.mccries.beans.Customer;
import ca.sheridancollege.mccries.beans.Order;
import ca.sheridancollege.mccries.beans.Product;
import ca.sheridancollege.mccries.beans.User;
import lombok.NonNull;

/* Name: Sarah McCrie 991405606
* Assignment: Assignment #3
* Date: November 23, 2023
* Program: A3_mccries
*/

@Repository
public class DatabaseAccess {

	@Autowired
	private NamedParameterJdbcTemplate jdbc;

	@Autowired
	@Lazy
	private PasswordEncoder passwordEncoder;

	// add a new user
	public void addUser(String email, String password) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "INSERT INTO sec_user" + "(email, encryptedPassword, enabled) "
				+ "VALUES (:email, :encryptedPassword, 1)";
		namedParameters.addValue("email", email);
		namedParameters.addValue("encryptedPassword", passwordEncoder.encode(password));
		jdbc.update(query, namedParameters);
	}

	// add a new customer
	public void addCustomer(Long userId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "INSERT INTO `customer` (userId) VALUES (:userId)";
		namedParameters.addValue("userId", userId);
		int rowsAffected = jdbc.update(query, namedParameters);
	}

	// add a role to user
	public void addRole(Long userId, Long roleId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "INSERT INTO user_role (userId, roleId) " + "VALUES (:userId, :roleId)";
		namedParameters.addValue("userId", userId);
		namedParameters.addValue("roleId", roleId);
		jdbc.update(query, namedParameters);
	}

	// find a user based on the user's email used
	public User findUserAccount(String email) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM `sec_user` where email = :email ";
		namedParameters.addValue("email", email);
		try {
			return jdbc.queryForObject(query, namedParameters, new BeanPropertyRowMapper<User>(User.class));
		} catch (EmptyResultDataAccessException erdae) {
			return null;
		}
	}

	// get roles by user's id
	public List<String> getRolesById(Long userId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT sec_role.roleName " + "FROM user_role, sec_role "
				+ "WHERE user_role.roleId = sec_role.roleId " + "AND userId = :userId";
		namedParameters.addValue("userId", userId);
		return jdbc.queryForList(query, namedParameters, String.class);
	}

	// get the customer by the user id
	public Customer getCustomerByUserId(Long userId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM `customer` WHERE userId = :userId ";
		namedParameters.addValue("userId", userId);
		try {
			return jdbc.queryForObject(query, namedParameters, new BeanPropertyRowMapper<Customer>(Customer.class));
		} catch (EmptyResultDataAccessException erdae) {
			return null;
		}
	}

	// list all customers for customerlist
	public List<Customer> getAllCustomers() {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM `customer`";
		try {
			return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Customer>(Customer.class));
		} catch (EmptyResultDataAccessException erdae) {
			return null;
		}
	}

	// delete the customer by their id
	@Transactional
	public void deleteCustomerByUserId(long userId) {

		// Get the customer ID using the userID
		String getCustomerIdQuery = "SELECT customerId FROM customer WHERE userId = :userId";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userId", userId);
		Long customerId = jdbc.queryForObject(getCustomerIdQuery, params, Long.class);

		if (customerId != null) {
			// Delete the user from the sec_user table
			String deleteCustomerAddressQuery = "DELETE FROM customer_address WHERE customerId = :customerId";
			params = new MapSqlParameterSource();
			params.addValue("customerId", customerId);
			jdbc.update(deleteCustomerAddressQuery, params);

			// Delete all records from the order table that match the customer ID
			String deleteOrdersQuery = "DELETE FROM `order` WHERE customerId = :customerId";
			params = new MapSqlParameterSource();
			params.addValue("customerId", customerId);
			jdbc.update(deleteOrdersQuery, params);

			// Delete the customer from the customer table
			String deleteCustomerQuery = "DELETE FROM customer WHERE customerId = :customerId";
			jdbc.update(deleteCustomerQuery, params);

		}
	}

	// update the customer's info
	public void updateCustomer(Customer customer) {
		String query = "UPDATE `customer` " + "SET firstName = :firstName, lastName = :lastName "
				+ "WHERE customerId = :customerId";
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("customerId", customer.getCustomerId());
		namedParameters.addValue("firstName", customer.getFirstName());
		namedParameters.addValue("lastName", customer.getLastName());

		int rowsAffected = jdbc.update(query, namedParameters);

		if (rowsAffected > 0)
			System.out.println("A customer was updated in the database");

	}

	// list all of the products
	public List<Product> getAllProducts() {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM `product`";
		try {
			return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Product>(Product.class));
		} catch (EmptyResultDataAccessException erdae) {
			return null;
		}
	}

	// list all of the products in the current cart
	public List<Product> getProductsFromCart(List<Long> currentCartIds) {
		String sql = "SELECT * FROM Product WHERE ProductId IN (:productIds)";
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("productIds", currentCartIds);

		List<Product> productTypesInCart = jdbc.query(sql, parameters, new BeanPropertyRowMapper<>(Product.class));
		return quantifyCartProducts(currentCartIds, productTypesInCart);
	}

	// quantify all the products into list
	private List<Product> quantifyCartProducts(List<Long> currentCartIds, List<Product> productTypesInCart) {
		List<Product> quantifiedCartProducts = new ArrayList<Product>();
		for (Long thisProductId : currentCartIds) {
			for (Product thisProduct : productTypesInCart) {
				if (thisProduct.getProductId() == thisProductId) {
					quantifiedCartProducts.add(thisProduct);
				}
			}
		}
		return quantifiedCartProducts;
	}

	// delete the product from products using product id
	public void deleteProductByProductId(Long productId) {
		String deleteProductQuery = "DELETE FROM `product` WHERE productId = :productId";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("productId", productId);
		jdbc.update(deleteProductQuery, params);

	}

	// get the product by product id
	public Product getProductByProductId(Long productId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM `product` WHERE productId = :productId ";
		namedParameters.addValue("productId", productId);
		try {
			return jdbc.queryForObject(query, namedParameters, new BeanPropertyRowMapper<Product>(Product.class));
		} catch (EmptyResultDataAccessException erdae) {
			return null;
		}
	}

	// find the user's address by email
	public Address findUserAddress(String email) {
		User theUser = findUserAccount(email);
		Long userId = theUser.getUserId();
		MapSqlParameterSource params = new MapSqlParameterSource();
		String query = "SELECT addressId FROM `customer_address` WHERE userId = :userId ";
		params.addValue("userId", userId);
		Long addressId = null;
		try {
			addressId = jdbc.queryForObject(query, params, Long.class);
		} catch (EmptyResultDataAccessException erdae) {
			return null;
		}
		MapSqlParameterSource addressParams = new MapSqlParameterSource();
		addressParams.addValue("addressId", addressId);
		String addressQuery = "SELECT * FROM `address` WHERE addressId = :addressId";
		try {
			return jdbc.queryForObject(addressQuery, addressParams, new BeanPropertyRowMapper<Address>(Address.class));
		} catch (EmptyResultDataAccessException erdae) {
			return null;
		}
	}

	// insert a new address
	public void insertAddress(Address address, Customer customer) {
		String query = "INSERT INTO `address` (streetName, streetNumber, city, province, postalCode) VALUES (:streetName, :streetNumber, :city, :province, :postalCode)";
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("streetName", address.getStreetName());
		namedParameters.addValue("streetNumber", address.getStreetNumber());
		namedParameters.addValue("city", address.getCity());
		namedParameters.addValue("province", address.getProvince());
		namedParameters.addValue("postalCode", address.getPostalCode());

		KeyHolder keyHolder = new GeneratedKeyHolder();

		int rowsAffected = jdbc.update(query, namedParameters, keyHolder);

		if (rowsAffected > 0) {
			Number key = keyHolder.getKey();
			System.out.println("An address was inserted into database with ID: " + key);
			String custAddressQuery = "INSERT INTO `customer_address` (customerId, userId, addressID) VALUES (:customerId, :userId, :addressId)";
			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue("customerId", customer.getCustomerId());
			params.addValue("userId", customer.getUserId());
			params.addValue("addressId", key);
			int moreRowsAffected = jdbc.update(custAddressQuery, params);
			System.out.println("Here: " + moreRowsAffected);
		}

	}

	// create a new order
	public boolean createOrder(List<Product> productsFromCart, @NonNull Long customerId) {
		String stringTotal = generateTotal(productsFromCart);
		Double doubleAmount = Double.parseDouble(stringTotal);
		String query = "INSERT INTO `order` (orderDate, orderStatus, customerId, orderAmount) VALUES (:orderDate, :orderStatus, :customerId, :orderAmount)";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("orderDate", LocalDateTime.now());
		System.out.println(LocalDateTime.now());
		params.addValue("orderStatus", "Processing");
		params.addValue("customerId", customerId);
		params.addValue("orderAmount", doubleAmount);
		int rowsAffected = jdbc.update(query, params);
		System.out.println("Here: " + rowsAffected);
		return rowsAffected > 0;
	}

	// generate total cost in the cart
	public String generateTotal(List<Product> productsFromCart) {
		double totalAmount = 0;
		for (Product product : productsFromCart) {
			totalAmount += product.getProductPrice();
		}
		totalAmount *= 1.13;
		String stringTotal = (String) String.format("%.2f", totalAmount);
		return stringTotal;
	}

	// get all the orders to display based on customer
	public List<Order> getAllOrders(Long customerId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("customerId", customerId);

		String query = "SELECT * FROM `order` WHERE customerId = :customerId";
		try {
			return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Order>(Order.class));
		} catch (EmptyResultDataAccessException erdae) {
			return null;
		}
	}

	// add a new product to the products list
	public void addProductToProducts(Product product) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "INSERT INTO `product` (productName, productPrice) VALUES (:productName, :productPrice)";
		namedParameters.addValue("productName", product.getProductName());
		namedParameters.addValue("productPrice", product.getProductPrice());
		int rowsAffected = jdbc.update(query, namedParameters);
		System.out.println("Here: " + rowsAffected);
	}

}
