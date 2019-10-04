package nl.phoneplaats.phoneplaats.dto;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="indox_inventory")
public class Inventory {
	@Id
	@Column(name="inventory_id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int inventoryId;
	
	@ManyToOne
	@JoinColumn(name="product_id")
	private Product product;

	@Column(name="inventory_quantity")
	private int stockQuantity;
	
	public int getInventoryId() {
		return inventoryId;
	}
	public void setInventoryId(int inventoryId) {
		this.inventoryId = inventoryId;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	
	public int getStockQuantity() {
		return stockQuantity;
	}
	public void setStockQuantity(int stockQuantity) {
		this.stockQuantity = stockQuantity;
	}
	
	
	

}
