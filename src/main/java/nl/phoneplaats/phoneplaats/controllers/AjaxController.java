package nl.phoneplaats.phoneplaats.controllers;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nl.phoneplaats.phoneplaats.dto.Order;
import nl.phoneplaats.phoneplaats.dto.Product;
import nl.phoneplaats.phoneplaats.repo.InventoryRepo;

@RestController
@RequestMapping("/shoppingcart")
public class AjaxController {
	@Autowired
	private InventoryRepo inventoryRepo;
	
	private static Logger logger = LoggerFactory.getLogger(AjaxController.class);
	
	@PostMapping(value = "/deleteFromCartAjax")
	public String testAjax(@RequestBody int prodId, HttpSession session) {	
		  Order order = (Order) session.getAttribute("order");
		  Product product = new Product();
		  for (int i = 0 ; i < order.getOrderDetails().size(); i++) { 
			  if (order.getOrderDetails().get(i).getProduct().getProductId() == prodId) {
				  product = order.getOrderDetails().get(i).getProduct();
				  	order.getOrderDetails().remove(i); 
			  	} 
			  }
		  
		  logger.debug("product deleted from the cart: " + product.getProductName());
		  
		  session.setAttribute("order", order);
		 
		return "secceeded";
		
	}
	
	
	@PostMapping(value = "/increaseAmountAjax")
	public String increaseAmountAjax(@RequestBody int prodId, HttpSession session) {	
		  Order order = (Order) session.getAttribute("order");
		  Product product = new Product();
		  for (int i = 0 ; i < order.getOrderDetails().size(); i++) { 
			  if (order.getOrderDetails().get(i).getProduct().getProductId() == prodId) {
				  product = order.getOrderDetails().get(i).getProduct();
				  if (order.getOrderDetails().get(i).getQuantity()+1 <= inventoryRepo.findByProduct(product).getStockQuantity()){
					  order.getOrderDetails().get(i).setQuantity(order.getOrderDetails().get(i).getQuantity()+1);
					  }
				  } 
			  }
		  
		  logger.debug("quantity increased in the cart: " + product.getProductName());
		  
		  session.setAttribute("order", order);
		 
		return "secceeded";
		
	}
	
	@PostMapping(value = "/decreaseAmountAjax")
	public String decreaseAmountAjax(@RequestBody int prodId, HttpSession session) {	
		  Order order = (Order) session.getAttribute("order");
		  Product product = new Product();
		  for (int i = 0 ; i < order.getOrderDetails().size(); i++) { 
			  if(order.getOrderDetails().get(i).getProduct().getProductId() == prodId) {
				  if(order.getOrderDetails().get(i).getQuantity()-1 >= 0) {
					  product = order.getOrderDetails().get(i).getProduct();					  
					  order.getOrderDetails().get(i).setQuantity(order.getOrderDetails().get(i).getQuantity()-1);
					  }			  
				  }
			  }
		  
		  logger.debug("quantity decreased in the cart: " + product.getProductName());
		  
		  session.setAttribute("order", order);
		 
		return "secceeded";
		
	}

}
