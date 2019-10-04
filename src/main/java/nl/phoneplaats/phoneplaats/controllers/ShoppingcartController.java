package nl.phoneplaats.phoneplaats.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import be.feelio.mollie.exception.MollieException;
import nl.phoneplaats.phoneplaats.dao.InventoryDao;
import nl.phoneplaats.phoneplaats.dao.ProductDao;
import nl.phoneplaats.phoneplaats.dto.Customer;
import nl.phoneplaats.phoneplaats.dto.Order;
import nl.phoneplaats.phoneplaats.dto.OrderDetail;
import nl.phoneplaats.phoneplaats.dto.Product;
import nl.phoneplaats.phoneplaats.repo.OrderRepo;
import nl.phoneplaats.phoneplaats.services.CustomerServices;
import nl.phoneplaats.phoneplaats.services.EmailServices;
import nl.phoneplaats.phoneplaats.services.GeneralServices;
import nl.phoneplaats.phoneplaats.services.InventoryServices;
import nl.phoneplaats.phoneplaats.services.OrderServices;
import nl.phoneplaats.phoneplaats.services.PaymentServices;
import nl.phoneplaats.phoneplaats.services.ProductServices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ShoppingcartController {
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private InventoryDao inventoryDao;
	@Autowired
	private ProductServices productServices;
	@Autowired
	private OrderRepo orderRepo;
	@Autowired
	private OrderServices orderServices;
	@Autowired
	private PaymentServices paymentServices;
	@Autowired
	private GeneralServices generalServices;
	@Autowired
	private InventoryServices inventoryServices;
	
	private Order order;
	private List<OrderDetail> shoppingCartItems ;
	private static Logger logger = LoggerFactory.getLogger(ShoppingcartController.class);
	
	private void setSessionVariables(HttpSession session) {
		if(session.getAttribute("order") == null) {
			order = new Order(); 
			order.setOrderDetails(new ArrayList<OrderDetail>());
			session.setAttribute("order", order);
		}
		order = (Order) session.getAttribute("order") ;
		shoppingCartItems = order.getOrderDetails();
		
	}
		
	@RequestMapping(value="/shoppingcart", method=RequestMethod.GET)
	public String getShoppingCartPage(Model model, HttpSession session) {
		logger.debug("Getting ShoppingCart");
		logger.info("info Getting shoppingcart");
		Order order = (Order) session.getAttribute("order");
		if (order == null || order.getOrderDetails()==null) {
			return "redirect:/products";
		}
		generalServices.setPageHeader(model, session);
		setSessionVariables(session);
		shoppingCartItems = order.getOrderDetails();
		model.addAttribute("order", this.order);
		
		orderServices.setOrderTotal(order, shoppingCartItems);
		
		model.addAttribute("orderSubtotal", order.getOrderTotal());
		model.addAttribute("shippingCost", 0);
		model.addAttribute("total", order.getOrderTotal());
		
		return "shoppingcart";
	}

	@RequestMapping(value="/addToShoppingCart" , method=RequestMethod.POST)
	public String addToShoppingCart(Model model, Product product, int quantity, HttpSession session) {
		setSessionVariables(session);
		
		shoppingCartItems = order.getOrderDetails()!=null?order.getOrderDetails():new ArrayList<>();
		//check if the ordered quantity is correct
		if (quantity <=0 && quantity > inventoryDao.getProductInventory(product)) {
			return "redirect:productDetails?prodId="+product.getProductId()+"&error=1";
		}
		
		OrderDetail orderItem = new OrderDetail();
		
		//set the full information about the product before adding it to the order
		Product prod= productServices.setProductInfo(product.getProductId());
		orderItem.setProduct(prod);
		orderItem.setQuantity(quantity);
		
		generalServices.addItemToShoppingcart(shoppingCartItems, orderItem);
					
		order.setOrderDetails(shoppingCartItems);
		
		orderServices.setOrderTotal(order, shoppingCartItems);

		session.setAttribute("order", order);
		
		return "redirect:shoppingcart";		
	}
	
	@RequestMapping(value="/delorderitem", method=RequestMethod.GET)
	public String deleteOrderItem(@RequestParam (value="prodId", required=true, defaultValue="0")int prodId, Model model) {
		
		Product prod= productDao.getProductById(prodId);
		for (int i=0; i<shoppingCartItems.size(); i++) {
			if (shoppingCartItems.get(i).getProduct().equals(prod)) {
				shoppingCartItems.remove(shoppingCartItems.get(i));
				i--;
			}
		}
		
		return "redirect:shoppingcart";
		
	}
	
	
	  @RequestMapping(value="/addProductsToOrder", method=RequestMethod.POST)
	  public String addProductsToOrder(@ModelAttribute Order order, HttpSession session) { 
		  if (order.getOrderDetails().size()==0) {
			  return "redirect:shoppingcart";
		  }
		  this.order.setOrderDetails(order.getOrderDetails());
		  session.setAttribute("order", this.order);
		  		 		  
		  return"redirect:shippingInfo"; 
	  }
	  
	
	  @RequestMapping (value="/shippingInfo", method=RequestMethod.GET) 
	  public String checkout(Model model
			  		, HttpSession session
			  		,@RequestParam(value="error", required=false, defaultValue="")String error) {
		Order order = (Order) session.getAttribute("order");
		generalServices.setPageHeader(model, session);
		if (order.getOrderDetails().size()==0) {
			return "redirect:products";
		}
		
		orderServices.setOrderTotal(order, order.getOrderDetails());
		order.setShippingCost(0.00d);
		//OrderServices.setShippingCost(0.00);
		
		Customer customer = new Customer();
		String errorMessage="";
		if(!error.isEmpty()) {
			if(error.equals("PersonalInfoError")) {
				errorMessage = "Er is een fout in uw gegevens! Controleer uw gegevens en probeer het nog een keer";

			}else if (error.equals("PaymentError")) {
				errorMessage = "Er is een fout opgetreden de betaling server, probeer het nog een keer";	
			}else if (error.equals("PaymentServerError")){
				errorMessage = "Er is een fout opgetreden de betaling server, probeer het nog een keer";	
			}else {
				errorMessage = "Er is een onbekende fout opgetreden! probeer het nog een keer.";
			}
			customer = order.getCustomer();
			model.addAttribute("errorMessage", errorMessage);
		}
		order.setCustomer(customer);
		
		session.setAttribute("order", order);

		model.addAttribute("customer", customer);		
		model.addAttribute("orderSubtotal", order.getOrderTotal());
		model.addAttribute("shippingCost", order.getShippingCost());
		model.addAttribute("total", order.getOrderTotal());
		model.addAttribute("errorMessage", errorMessage);
		
	  
	  return"shippinginfo"; 
	  }	 
	
	
	@RequestMapping(value="/checkout" , method=RequestMethod.POST)
	public String doCheckout(@ModelAttribute Customer customer
							, HttpSession session
							,String extraNotes
							,String bankInfo) {
		
		Order order = (Order) session.getAttribute("order");
		
		//check the info filled by the customer
		if (!CustomerServices.isValidCustomer(customer)) {
			return "redirect:shippingInfo?error=PersonalInfoError";
		}
		
		//fill in all required order data 
		try {
			orderServices.setCustomerInfo(order, customer);
			orderServices.perpareOrderForPayment(order);
			
		}catch (Exception e) {
			return "redirect:shippingInfo?error=OtherError";
		}
		
		String returnedURL="";
		
		try {
			returnedURL = paymentServices.createPayment(order.getOrderTotal()+order.getShippingCost(), order);
		}catch (MollieException e) {
			return "redirect:shippingInfo?error=PaymentServerError";
		}catch (Exception e) {
			return "redirect:shippingInfo?error=OtherError";
		}	
		
		session.setAttribute("orderFunctionalId", order.getFunctionalId());
		

		logger.debug("payment succeeded: now redirecting");
		return "redirect:"+returnedURL;
				
	}
	

	@RequestMapping(value="/confirmation", method = RequestMethod.GET)
	public String getConfirmation(@RequestParam(name="orderId", required=true)String orderId
									, Model model
									,HttpSession session) {
		Order order = orderRepo.findByFunctionalId(orderId);
		generalServices.setPageHeader(model, session);
		boolean isPaymentSuccessful=false;
		
		try {
			if(paymentServices.isPaymentCompleted(order)) {
				isPaymentSuccessful = true;
				session.removeAttribute("order");
				
			}
		} catch (MollieException e) {
			e.printStackTrace();
			
		}
		if (!isPaymentSuccessful)
			return "redirect:shippingInfo?error=PaymentError";
	
		try {
			EmailServices.sendOrderConfirmationToCustomer(order);
			EmailServices.sendOrderToSeller(order);			
		} catch (MessagingException e) {
			logger.error("error happened while sending email for confirmation");
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("ERROR happened while sending email");
			e.printStackTrace();
		}
		
		model.addAttribute("order", order);
		inventoryServices.updateQuantityAfterOrder(order);
		order.setStatus("Paid");
		orderRepo.save(order);
				
		return "confirmation"; 
		
	}
}
