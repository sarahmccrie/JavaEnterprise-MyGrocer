package ca.sheridancollege.mccries.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.sheridancollege.mccries.beans.Address;
import ca.sheridancollege.mccries.beans.Customer;
import ca.sheridancollege.mccries.beans.Order;
import ca.sheridancollege.mccries.beans.Product;
import ca.sheridancollege.mccries.beans.User;
import ca.sheridancollege.mccries.database.DatabaseAccess;
import ca.sheridancollege.mccries.security.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpSession;

/* Name: Sarah McCrie 991405606
* Assignment: Assignment #3
* Date: November 23, 2023
* Program: A3_mccries
*/

@Controller
public class HomeController {

	@Autowired
	@Lazy
	PasswordEncoder passwordEncoder;

	@Autowired
	@Lazy
	private DatabaseAccess da;

	@Autowired
	@Lazy
	UserDetailsServiceImpl detailService;

	// register get and post
	@GetMapping("/register")
	public String getRegister() {
		return "register";
	}

	@PostMapping("/register")
	public String postRegister(@RequestParam String username, @RequestParam String password) {
		da.addUser(username, password);
		Long userId = da.findUserAccount(username).getUserId();
		da.addRole(userId, Long.valueOf(1));
		da.addCustomer(userId);
		return "redirect:/";
	}

	// index (not logged in/unsecure)
	@GetMapping("/")
	public String index() {
		return "index";
	}

	// secure index
	@GetMapping("/secure")
	public String secureIndex(Model model, Authentication authentication) {
		String email = authentication.getName();
		List<String> roleList = new ArrayList<String>();
		for (GrantedAuthority ga : authentication.getAuthorities()) {
			roleList.add(ga.getAuthority());
		}
		User authenticatedUser = da.findUserAccount(email);
		model.addAttribute("email", email);
		model.addAttribute("roleList", roleList);
		model.addAttribute("userId", authenticatedUser.getUserId());
		return "/secure/index";
	}

	// login
	@GetMapping("/login")
	public String login() {
		return "login";
	}

	// permission denied
	@GetMapping("/permission-denied")
	public String permissionDenied() {
		return "/error/permission-denied";
	}

	// password
	@GetMapping("/pass")
	public String pass() {
		String password = passwordEncoder.encode("1234").toString();
		System.out.println("The encrypted password is " + password);
		boolean m = passwordEncoder.matches("1234", password);
		System.out.println("The password is " + m);
		boolean n = passwordEncoder.matches("124", password);
		System.out.println("The password is " + n);
		return "login";
	}

	// Customer related methods
	// secure customer page
	@GetMapping("/secure/customer")
	public String customer(Model model, Authentication authentication) {
		String email = authentication.getName();
		System.out.println("The userId is " + email);
		User thisUser = da.findUserAccount(email);
		Customer thisCustomer = da.getCustomerByUserId(thisUser.getUserId());
		if (thisCustomer == null) {
			model.addAttribute("customer", new Customer());
		} else {
			model.addAttribute("customer", thisCustomer);
		}
		Boolean hasNameInfo = thisCustomer.getFirstName() != "";
		model.addAttribute("hasNameInfo", hasNameInfo);
		model.addAttribute("firstName", thisCustomer.getFirstName());
		model.addAttribute("lastName", thisCustomer.getLastName());
		return "secure/customer";
	}

	@PostMapping("/secure/customer")
	public String index(Model model, @ModelAttribute Customer customer) {
		System.out.println("The post to customer was called");
		da.updateCustomer(customer);
		model.addAttribute("customer", customer);
		return "secure/customer";
	}

	// customerlist related methods
	@GetMapping("/adminSecure/customerlist")
	public String customerList(Model model) {
		List<Customer> allCustomers = da.getAllCustomers();
		model.addAttribute("customerlist", allCustomers);
		return "adminSecure/customerlist";
	}

	@GetMapping("/adminSecure/deleteCustomerById/{userId}")
	public String deleteCustomerById(Model model, @PathVariable Long userId) {
		da.deleteCustomerByUserId(userId);
		model.addAttribute("customerlist", da.getAllCustomers());
		return "adminSecure/customerlist";
	}

	@GetMapping("/adminSecure/editCustomerById/{userId}")
	public String editCustomerById(Model model, @PathVariable Long userId) {
		Customer customer = da.getCustomerByUserId(userId);
		System.out.println("The name is " + customer.getFirstName());
		model.addAttribute("customer", customer);
		da.deleteCustomerByUserId(userId);
		return "secure/customer";
	}

	// address related methods
	@GetMapping("/secure/address")
	public String address(Model model, Authentication authentication) {
		String email = authentication.getName();
		Address address = da.findUserAddress(email);
		model.addAttribute("address", address);
		if (address != null) {
			model.addAttribute("address", address);
		} else {
			model.addAttribute("address", new Address());
		}
		return "secure/address";
	}

	@PostMapping("/secure/address")
	public String index(Model model, @ModelAttribute Address address, Authentication authentication) {
		String email = authentication.getName();
		System.out.println("The post to customer was called");
		User user = da.findUserAccount(email);
		Customer customer = da.getCustomerByUserId(user.getUserId());
		da.insertAddress(address, customer);
		model.addAttribute("address", address);
		return "secure/address";
	}

	// Product related methods
	@GetMapping("/secure/product")
	public String product(Model model) {
		List<Product> allProducts = da.getAllProducts();
		model.addAttribute("productlist", allProducts);
		return "secure/product";
	}

	// productlist related methods
	@GetMapping("/adminSecure/productlist")
	public String productlist(Model model) {
		List<Product> allProducts = da.getAllProducts();
		model.addAttribute("productlist", allProducts);
		return "adminSecure/productlist";
	}

	@GetMapping("/adminSecure/addProduct")
	public String addProduct(Model model, Product product, Long productId) {
		Product thisProduct = da.getProductByProductId(productId);
		if (thisProduct == null) {
			model.addAttribute("product", new Product());
		} else {
			model.addAttribute("product", thisProduct);
		}
		model.addAttribute("productName", thisProduct.getProductName());
		model.addAttribute("productPrice", thisProduct.getProductPrice());
		return "adminSecure/addProduct";
	}

	@GetMapping("/adminSecure/deleteProductById/{productId}")
	public String deleteProductById(Model model, @PathVariable Long productId) {
		da.deleteProductByProductId(productId);
		model.addAttribute("productlist", da.getAllProducts());
		return "adminSecure/productlist";
	}

	@PostMapping("/adminSecure/editProduct")
	public String editProduct(Model model, @ModelAttribute Product product) {
		da.addProductToProducts(product);
		model.addAttribute("productlist", da.getAllProducts());
		return "adminSecure/productlist";
	}

	@GetMapping("/adminSecure/editProductById/{productId}")
	public String editProductById(Model model, @PathVariable Long productId) {
		Product product = da.getProductByProductId(productId);
		model.addAttribute("product", product);
		da.deleteProductByProductId(productId);
		return "adminSecure/editProduct";
	}

	// shoppingcart related methods
	@GetMapping("/secure/shoppingcart")
	public String shoppingCart(Model model, HttpSession session) {
		List<Long> currentCartIds = (CopyOnWriteArrayList<Long>) session.getAttribute("currentShoppingCart");
		if (currentCartIds == null || currentCartIds.isEmpty()) {
			return "secure/shoppingcart";
		}
		List<Product> productsFromCart = da.getProductsFromCart(currentCartIds);
		model.addAttribute("cartProducts", productsFromCart);
		String cartTotal = da.generateTotal(productsFromCart);
		model.addAttribute("cartTotal", cartTotal);
		return "secure/shoppingcart";
	}

	@PostMapping("/secure/shoppingcart")
	public String product(Model model, @RequestParam Long productId, HttpSession session) {
		List<Long> productIdsInCart;
		// check if the session is new
		if (session.getAttribute("currentShoppingCart") == null) {
			System.out.println("Session is new.");
			// create a new list when the session is new!
			productIdsInCart = new CopyOnWriteArrayList<Long>();
		} else { // if session is old, get the list from the session object
			System.out.println("Session is old.**");
			productIdsInCart = (CopyOnWriteArrayList<Long>) session.getAttribute("currentShoppingCart");
		}
		// add new phone to the list
		productIdsInCart.add(productId);
		// update the list
		session.setAttribute("currentShoppingCart", productIdsInCart);
		List<Product> allProducts = da.getAllProducts();
		model.addAttribute("productlist", allProducts);
		return "secure/product";
	}

	@PostMapping("/secure/deleteFromCart")
	public String deleteFromCart(Model model, @RequestParam Long productId, HttpSession session) {
		// get all the products in the cart for current session
		List<Long> currentCartIds = (CopyOnWriteArrayList<Long>) session.getAttribute("currentShoppingCart");
		if (currentCartIds == null || currentCartIds.isEmpty()) {
			return "secure/shoppingcart";
		}
		// remove product that was brought into method to be removed
		for (Long thisIdInCart : currentCartIds) {
			if (thisIdInCart == productId) {
				currentCartIds.remove(thisIdInCart);
				break;
			}
		}
		// put changed list back into session
		session.setAttribute("currentShoppingCart", currentCartIds);
		List<Product> productsFromCart = da.getProductsFromCart(currentCartIds);
		model.addAttribute("cartProducts", productsFromCart);
		String cartTotal = da.generateTotal(productsFromCart);
		model.addAttribute("cartTotal", cartTotal);
		return "secure/shoppingcart";
	}

	// order related methods
	@PostMapping("/secure/createOrder")
	public String createOrder(Model model, HttpSession session, Authentication authentication) {
		if (session.getAttribute("currentShoppingCart") == null) {
			System.out.println("Session is new.");
			return "secure/shoppingcart";
		}
		List<Long> currentCartIds = (CopyOnWriteArrayList<Long>) session.getAttribute("currentShoppingCart");
		List<Product> productsFromCart = da.getProductsFromCart(currentCartIds);
		Customer customer = getCustomerFromEmail(authentication.getName());
		if (customer == null) {
			return "secure/order";
		}
		boolean success = da.createOrder(productsFromCart, customer.getCustomerId());
		if (success == true) {
			session.setAttribute("currentShoppingCart", null);
			List<Order> allOrders = da.getAllOrders(customer.getCustomerId());
			model.addAttribute("orderlist", allOrders);
		}
		return "secure/order";
	}

	@GetMapping("/secure/order")
	public String order(Model model, Authentication authentication) {
		String email = authentication.getName();
		User user = da.findUserAccount(email);
		if (user != null) {
			Customer customer = da.getCustomerByUserId(user.getUserId());
			if (customer != null) {
				List<Order> allOrders = da.getAllOrders(customer.getCustomerId());
				model.addAttribute("orderlist", allOrders);
			} else {
				model.addAttribute("message", "No customer details found.");
			}
		} else {
			model.addAttribute("message", "User not found.");
		}

		return "secure/order";
	}

	// helper methods
	public Customer getCustomerFromEmail(String email) {
		System.out.println("The post to customer was called");
		User user = da.findUserAccount(email);
		Customer customer = da.getCustomerByUserId(user.getUserId());
		return customer;
	}

}
