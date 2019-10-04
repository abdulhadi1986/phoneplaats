package nl.phoneplaats.phoneplaats.dto;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="product_images")
public class ProductImage {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="image_id")
	private int imageId;
	
	@ManyToOne
	@JoinColumn(name="prod_id")
	private Product product;
	
		
	@Column(name="prod_uri")
	private String imageUri;
	
	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	
	public String getImageUri() {
		return imageUri;
	}

	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
	}
	
	

}
