package nl.phoneplaats.phoneplaats.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import nl.phoneplaats.phoneplaats.dto.Inventory;
import nl.phoneplaats.phoneplaats.dto.Product;

public interface InventoryRepo extends JpaRepository<Inventory, Integer> {
	public Inventory findByProduct(Product product);

}
