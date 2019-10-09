package nl.phoneplaats.phoneplaats.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.phoneplaats.phoneplaats.dto.Product;
import nl.phoneplaats.phoneplaats.repo.InventoryRepo;

@Service
public class InventoryDao {
	@Autowired
	private InventoryRepo inventoryRepo;
	
	public List<Product> getAllProducts(int categoryId){
		List<Product> allProducts=new ArrayList<>();
		
		inventoryRepo.findAll().stream().filter(p->p.getStockQuantity()>0).forEach(inv->allProducts.add(inv.getProduct()));
		if (categoryId==0)
			return allProducts;
		for (int i =0; i<allProducts.size();i++) {
			if (allProducts.get(i).getProductCategory().getCategoryId()!=categoryId) {
				allProducts.remove(i);
				i--;
			}
		}
		return allProducts;
	}
	
	public int getProductInventory(Product prod) {
		return inventoryRepo.findByProduct(prod).getStockQuantity();
	}
}
