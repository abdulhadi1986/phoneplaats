package nl.phoneplaats.phoneplaats.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.phoneplaats.phoneplaats.dto.Inventory;
import nl.phoneplaats.phoneplaats.dto.Order;
import nl.phoneplaats.phoneplaats.dto.OrderDetail;
import nl.phoneplaats.phoneplaats.repo.InventoryRepo;

@Service
public class InventoryServices {
	@Autowired
	InventoryRepo inventoryRepo;
	
	public void updateQuantityAfterOrder(Order order) {
		for (OrderDetail orderItem : order.getOrderDetails()) {
			Inventory inventoryItem = inventoryRepo.findByProduct(orderItem.getProduct());
			inventoryItem.setStockQuantity(inventoryItem.getStockQuantity()- orderItem.getQuantity());
			inventoryRepo.save(inventoryItem);			
		}
	}

}
