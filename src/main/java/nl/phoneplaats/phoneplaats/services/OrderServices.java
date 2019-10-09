package nl.phoneplaats.phoneplaats.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import nl.phoneplaats.phoneplaats.dto.Customer;
import nl.phoneplaats.phoneplaats.dto.Order;
import nl.phoneplaats.phoneplaats.dto.OrderDetail;
import nl.phoneplaats.phoneplaats.repo.CustomerRepo;
import nl.phoneplaats.phoneplaats.repo.OrderDetailRepo;
import nl.phoneplaats.phoneplaats.repo.OrderRepo;

@Component
public class OrderServices {
	@Autowired
	CustomerRepo customerRepo; 
	@Autowired
	OrderDetailRepo orderDetailRepo;
	@Autowired
	OrderRepo orderRepo; 
	
	/**
	 * sets customer info in the database and assigns them to the order 	
	 * @param order
	 * @param customer
	 * @return
	 */
	public void setCustomerInfo(Order order, Customer customer) {
		customer.setEmail(customer.getEmail().replace(";", ""));
		Customer savedCustomer = customerRepo.findByEmail(customer.getEmail());
		if(savedCustomer == null) {
			customerRepo.save(customer);
			savedCustomer = customerRepo.findByEmail(customer.getEmail());
		}

		order.setCustomer(savedCustomer);	
		
	}
	/**
	 * calculate the cost of shipping via PostNl 
	 * 	max weight 10 kg : 6,95 - 30 kg: 13,25
	 *	max package size : 100 cm X 50cm X 50cm 
	 *	
	 * @param order
	 */
	public static void setShippingCost(Order order) {
		
		//capacity in cm 
		double totalCapacity=0;
		//weight in gram
		double totalWeight=0;
		double totalCost = 0;
		try {
		  //get total weight and total capacity 
			for (OrderDetail orderItem :order.getOrderDetails()) { 
				totalCapacity += (orderItem.getProduct().getPackageWidth()
								* orderItem.getProduct().getPackageHeight()
								* orderItem.getProduct().getPackageLength()); 
				totalWeight +=orderItem.getProduct().getProductWeight(); 
				}
			  
			  //calculate the total accordingly 
			totalCost = 6.95d 
						* Math.ceil(totalCapacity/250000d) 
						* Math.ceil(totalWeight/10000d);
			 
			
			order.setShippingCost(Math.round(totalCost*100d)/100d);
		} catch(Exception e){
			order.setShippingCost(6.95);
		}
	}
	
	public void saveOrderDetails(Order order, List<OrderDetail> orderDetails) {
		if (order.getOrderId()==0)
			return;
		
		for (OrderDetail orderDetail : orderDetails) {
			orderDetailRepo.save(orderDetail);	
		}
		//order.setOrderDetails(orderServices.orderDetailRepo.findByOrderId(order.getOrderId()));
	}
	
	//set a unique functional id that contains the date and random number
	public void setFunctionalId(Order order) {
		int rand = (int)((Math.round(Math.random() * 100d) / 100d)*100);
		String fId = "indx"
				+LocalDate.now().getMonthValue()
				+LocalDate.now().getDayOfMonth()
				+LocalDateTime.now().getHour()
				+LocalDateTime.now().getSecond()
				+"-"
				+rand ;
		order.setFunctionalId(fId);
		
	}
	
	/**
	 * creates functional id for the order 
	 * save the order and its details to the database with status open
	 * @param order
	 */
	public void perpareOrderForPayment(Order order) {
		//generate functional id for the order 
		setFunctionalId(order);
		
		//save the order to database and set status to open
		order.setStatus("open");
				
		//set the order date 
		order.setOrderDate(LocalDateTime.now());
		
		//save order to database 
		orderRepo.save(order);
		int orderId = orderRepo.findByFunctionalId(order.getFunctionalId()).getOrderId();
		order.setOrderId(orderId);
				
	}
	
	public void setOrderTotal(Order order, List<OrderDetail> orderItems) {
		double subTotal=0;
		for(OrderDetail orderDetail: orderItems) {
			subTotal += orderDetail.getProduct().getProductPrice()*orderDetail.getQuantity();
		}
		order.setOrderTotal(Math.round(subTotal*100d)/100d);
		
		
	}
	

}
