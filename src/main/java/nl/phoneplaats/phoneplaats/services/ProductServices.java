package nl.phoneplaats.phoneplaats.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import be.feelio.mollie.Client;
import be.feelio.mollie.ClientBuilder;
import be.feelio.mollie.data.payment.PaymentRequest;
import nl.phoneplaats.phoneplaats.dao.ProductDao;
import nl.phoneplaats.phoneplaats.dto.Category;
import nl.phoneplaats.phoneplaats.dto.Product;
import nl.phoneplaats.phoneplaats.repo.CategoryRepo;
import nl.phoneplaats.phoneplaats.repo.ImageRepo;
import nl.phoneplaats.phoneplaats.repo.InventoryRepo;
import nl.phoneplaats.phoneplaats.repo.ProductRepo;

@Component
public class ProductServices {
	@Autowired
	private ProductDao productDao;
	@Autowired
	private ImageRepo imageRepo;
	@Autowired
	private InventoryRepo inventoryRepo;
	@Autowired
	private CategoryRepo categoryRepo;
	
	@Autowired
	private ProductRepo productRepo;
	
	public Product setProductInfo(int productId) {
		Product product = new Product();
		product = productDao.getProductById(productId);
		product.setProductImages(imageRepo.findByProduct(product));
		setProductQuantity(product);
		return product;
	}
	
	public void setProductQuantity(Product product) {
		product.setAvailableQty(inventoryRepo.findByProduct(product).getStockQuantity());
	}
	
	public Map<Category, List<Product>> getAllCategoriesAndProducts(){
		Map<Category, List<Product>> categoriesAndProducts = new HashMap<>();
		for (Category cat : categoryRepo.findAll()) {
			List<Product> productList= productRepo.findByProductCategory(cat);
			if (productList != null) {
				categoriesAndProducts.put(cat,productList);
			}
			
		}
		return categoriesAndProducts;
	}
	
	/**
	 * returns an array of specifications 
	 * @param product
	 * @return
	 */
	public String[] getFormattedSpec(Product product) {
		return product.getProductDescription().split(";");
	}
}
