package nl.phoneplaats.phoneplaats.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.phoneplaats.phoneplaats.dto.Product;
import nl.phoneplaats.phoneplaats.repo.ImageRepo;
import nl.phoneplaats.phoneplaats.repo.ProductRepo;

@Service
public class ProductDao {
	@Autowired
	private ProductRepo productRepo;
	@Autowired
	private ImageRepo imageRepo;
	
	
	  public void setProductImages(Product product){
	  product.setProductImages(imageRepo.findByProduct(product)); 
	  }
	 
	
	public Product getProductById(int productId) {
		Product product=new Product();
		product = productRepo.findByProductId(productId);
		if (product == null)
			return null;
		setProductImages(product);
		productRepo.save(product);
		return product;
		
	}
	
	
	
	/*
	 * public List<ProductImage> getProductImages(Product product){
	 * 
	 * List<ProductImage> productImages = entityManager.createQuery(
	 * "select productImage " + "from ProductImage productImage  " +
	 * "join fetch ProductImage.product " + "where productImage.product = :product",
	 * ProductImage.class) .setParameter("product", product) .getResultList();
	 * return productImages; }
	 */
	

}
