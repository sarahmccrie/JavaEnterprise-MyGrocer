package ca.sheridancollege.mccries.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import ca.sheridancollege.mccries.beans.Address;
import ca.sheridancollege.mccries.beans.Customer;
import ca.sheridancollege.mccries.beans.Product;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class TestingHomeController {

	@Autowired
	private MockMvc mockMvc;
	
	

	// get method testing
	@Test
	public void testLoadingIndex() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"));
	}

	@Test
	public void testLoadingRegister() throws Exception {
		this.mockMvc.perform(get("/register")).andExpect(status().isOk()).andExpect(view().name("register"));
	}

	@Test
	public void testLoadingLogin() throws Exception {
		this.mockMvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
	}

	@Test
	public void testLoadingPermissionDenied() throws Exception {
		this.mockMvc.perform(get("/permission-denied")).andExpect(status().isOk())
				.andExpect(view().name("/error/permission-denied"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testLoadingAdminSecureCustomerList() throws Exception {
		this.mockMvc.perform(get("/adminSecure/customerlist")).andExpect(status().isOk())
				.andExpect(view().name("adminSecure/customerlist"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testDeleteCustomerById() throws Exception {
		Long userId = 1L;
		this.mockMvc.perform(get("/adminSecure/deleteCustomerById/" + userId)).andExpect(status().isOk())
				.andExpect(view().name("adminSecure/customerlist"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testLoadingSecureProduct() throws Exception {
		this.mockMvc.perform(get("/secure/product")).andExpect(status().isOk())
				.andExpect(view().name("secure/product"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testLoadingAdminSecureProductList() throws Exception {
		this.mockMvc.perform(get("/adminSecure/productlist")).andExpect(status().isOk())
				.andExpect(view().name("adminSecure/productlist"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testDeleteProductById() throws Exception {
		Long userId = (long)1;
		this.mockMvc.perform(get("/adminSecure/deleteProductById/" + userId)).andExpect(status().isOk())
				.andExpect(view().name("adminSecure/productlist"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testEditProductById() throws Exception {
		Long userId = 1L;
		this.mockMvc.perform(get("/adminSecure/editProductById/" + userId)).andExpect(status().isOk())
				.andExpect(view().name("adminSecure/editProduct"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testLoadingSecureShoppingcart() throws Exception {
		this.mockMvc.perform(get("/secure/shoppingcart")).andExpect(status().isOk())
				.andExpect(view().name("secure/shoppingcart"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testLoadingSecureOrder() throws Exception {
		this.mockMvc.perform(get("/secure/order")).andExpect(status().isOk()).andExpect(view().name("secure/order"));
	}

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteFromCart() throws Exception {
        Long productId = (long) 1;

        this.mockMvc.perform(post("/secure/deleteFromCart")
                .param("productId", productId.toString()))
            .andExpect(status().isOk())
            .andExpect(view().name("secure/shoppingcart"));
    }

    @Test
    @WithMockUser(username = "mccries@sheridancollege.ca", roles = "ADMIN")
    public void testCreateOrder() throws Exception {
        // Setup the session with a shopping cart
        MockHttpSession session = new MockHttpSession();
        List<Long> shoppingCart = new CopyOnWriteArrayList<Long>();
        shoppingCart.add(1L); 
        session.setAttribute("currentShoppingCart", shoppingCart);

        this.mockMvc.perform(post("/secure/createOrder").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("secure/order"));
    }

	// post method testing
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testUserRegistration() throws Exception {
		this.mockMvc
				.perform(post("/register").param("username", "newuser@example.com").param("password", "password123"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testUpdateCustomer() throws Exception {
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setFirstName("Sarah");
		customer.setLastName("McCrie");

		this.mockMvc.perform(post("/secure/customer").flashAttr("customer", customer)).andExpect(status().isOk())
				.andExpect(view().name("secure/customer")).andExpect(model().attributeExists("customer"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testEditProduct() throws Exception {
		Product product = new Product();
		product.setProductId(1L);
		product.setProductName("Updated Product Name");
		product.setProductPrice(99.99);

		this.mockMvc.perform(post("/adminSecure/editProduct").flashAttr("product", product)).andExpect(status().isOk())
				.andExpect(view().name("adminSecure/productlist")).andExpect(model().attributeExists("productlist"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testAddProductToShoppingCart() throws Exception {
		MockHttpSession session = new MockHttpSession();
		Long productId = 1L;

		this.mockMvc.perform(post("/secure/shoppingcart").session(session).param("productId", productId.toString()))
				.andExpect(status().isOk()).andExpect(view().name("secure/product"))
				.andExpect(model().attributeExists("productlist"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testDeleteProductFromShoppingCart() throws Exception {
		MockHttpSession session = new MockHttpSession();
		Long productId = 1L;
		List<Long> currentCartIds = new CopyOnWriteArrayList<Long>();
		currentCartIds.add(productId);
		session.setAttribute("currentShoppingCart", currentCartIds);

		this.mockMvc.perform(post("/secure/deleteFromCart").session(session).param("productId", productId.toString()))
				.andExpect(status().isOk()).andExpect(view().name("secure/shoppingcart"))
				.andExpect(model().attributeExists("cartProducts")).andExpect(model().attributeExists("cartTotal"));
	}


}