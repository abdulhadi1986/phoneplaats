package nl.phoneplaats.phoneplaats.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import nl.phoneplaats.phoneplaats.dto.Category;
import nl.phoneplaats.phoneplaats.dto.Product;
@Component
public interface ProductRepo extends JpaRepository<Product, Integer> {
	Product findByProductId(int id);
	List<Product> findByProductCategory(Category category);
	List<Product> findByProductCategoryCategoryId(int categoryId);
	Product findByProductName(String productName);

}
