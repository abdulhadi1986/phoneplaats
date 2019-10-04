package nl.phoneplaats.phoneplaats.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.phoneplaats.phoneplaats.dto.Order;
import nl.phoneplaats.phoneplaats.repo.OrderRepo;

@Service
public class AdminServices {
	@Autowired
	private OrderRepo orderRepo;
	
	public Map<String, List<Order>> getSalesOverview(){
		List<Order> allOrders = orderRepo.findAll();
		Map<String, List<Order>> salesOverview= new HashMap<>();
		Set<String> listOfMonths = new HashSet<String>();
		for (Order order : allOrders) {			
			String month = order.getOrderDate().getMonth().toString() + order.getOrderDate().getYear();			
			System.out.println("keyset now : " + salesOverview.keySet());
			if (listOfMonths.add(month)) {
				System.out.println("set doesn't contain "+ month);
				
				List<Order> monthOrders = new ArrayList<>();
				monthOrders.add(order);
				salesOverview.put(month, monthOrders);
				System.out.println(month+" key is added and empty list created");
			}else {
				salesOverview.get(month).add(order);
				System.out.println("set contain "+ month + " " +salesOverview.get(month));
			}
		}
		
		return salesOverview;
	}

}
