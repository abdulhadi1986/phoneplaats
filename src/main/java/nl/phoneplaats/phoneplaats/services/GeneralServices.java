package nl.phoneplaats.phoneplaats.services;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import nl.phoneplaats.phoneplaats.dto.Category;
import nl.phoneplaats.phoneplaats.dto.Order;
import nl.phoneplaats.phoneplaats.dto.OrderDetail;
import nl.phoneplaats.phoneplaats.dto.Product;

@Service
public class GeneralServices {
	@Autowired
	private ProductServices productServices;
	@Autowired
	private OrderServices orderServices;
	
	private static final Logger logger = LoggerFactory.getLogger(GeneralServices.class);
	
	public void setPageHeader(Model model, HttpSession session) {
		Order order = (Order) session.getAttribute("order");
		int shoppingItemsCount = 0;
		double total = 0.00d;
		
		if(order != null && order.getOrderDetails()!=null) {
			orderServices.setOrderTotal(order, order.getOrderDetails());
			shoppingItemsCount =order.getOrderDetails().size();
			total = order.getOrderTotal();
			
		}		
		model.addAttribute("shoppingItemsCount",shoppingItemsCount);
		model.addAttribute("orderTotal", total);	
		
		Map<Category,List<Product>> categoryMap = productServices.getAllCategoriesAndProducts();
		model.addAttribute("categoryMap",categoryMap);
		
	}
	
	/**
	 * add a product to the shopping cart or increases 
	 * the ordered quantity of an existed one
	 * @param list of OrderDetail objects 
	 * @param OrderDetail to be added
	 */
	public void addItemToShoppingcart(List<OrderDetail> shoppingCartItems, OrderDetail orderItem){
		//check if the cart already contains similar product then increase the amount instead of adding new item
		boolean productExists = false;		
		logger.debug("adding item to shopping cart: " + orderItem);
		for (int i=0 ; i <shoppingCartItems.size(); i++) {			
			if (shoppingCartItems.get(i).getProduct().equals(orderItem.getProduct())) {
				shoppingCartItems.get(i).setQuantity(shoppingCartItems.get(i).getQuantity()+orderItem.getQuantity());
				productExists=true;
				break;
			}			
		}
		if(shoppingCartItems.size()==0 || !productExists)
		shoppingCartItems.add(orderItem);
		logger.debug("shopping cart now : " + shoppingCartItems);
	}
	

}
