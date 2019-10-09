package nl.phoneplaats.phoneplaats.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import nl.phoneplaats.phoneplaats.dto.Product;
import nl.phoneplaats.phoneplaats.dto.ProductImage;

public interface ImageRepo extends JpaRepository<ProductImage, Integer> {
	public List<ProductImage> findByProduct(Product product);
}
