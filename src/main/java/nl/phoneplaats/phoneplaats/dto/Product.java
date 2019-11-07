package nl.phoneplaats.phoneplaats.dto;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="products")
public class Product {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="prod_id")
	private int productId;
	
	@Column(name="prod_name")
	private String productName;
	
	@Column(name="prod_short_desc")
	private String productShortDesc;
	
	@Column(name="prod_description")
	private String productDescription;
	
	@Column(name="prod_price")
	private Double productPrice;
	
	@Column(name="prod_sale_price")
	private Double productSalePrice;
	
	@Column(name="prod_unit")
	private String productUnit;
	
	@ManyToOne
	@JoinColumn(name="prod_category")
	private Category productCategory;
	
	@Column(name="prod_color")
	private String productColor;
	
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="prod_id")
	List<ProductImage> productImages; 
	
	@Column(name="prod_weight")
	private Double productWeight;
	
	@Column(name="prod_ind_postbus_size")
	private Boolean isProstbusSize;
	
	@Column(name="prod_available_qty")
	private Integer availableQty;
	
	@Column(name="package_width")
	private Double packageWidth;
	
	@Column (name="package_length")
	private Double packageLength;
	
	@Column (name="package_height")
	private Double packageHeight; 
			
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + productId;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (productId != other.productId)
			return false;
		return true;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductShortDesc() {
		return productShortDesc;
	}
	public void setProductShortDesc(String productShortDesc) {
		this.productShortDesc = productShortDesc;
	}
	public String getProductDescription() {
		return productDescription;
	}
	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}
	public Double getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(Double productPrice) {
		this.productPrice = productPrice;
	}
	public Double getProductSalePrice() {
		return productSalePrice;
	}
	public void setProductSalePrice(Double productSalePrice) {
		this.productSalePrice = productSalePrice;
	}
	public String getProductUnit() {
		return productUnit;
	}
	public void setProductUnit(String productUnit) {
		this.productUnit = productUnit;
	}
	public Category getProductCategory() {
		return productCategory;
	}
	public void setProductCategory(Category productCategory) {
		this.productCategory = productCategory;
	}
	public String getProductColor() {
		return productColor;
	}
	public void setProductColor(String productColor) {
		this.productColor = productColor;
	}
	public List<ProductImage> getProductImages() {
		return productImages;
	}
	public void setProductImages(List<ProductImage> productImages) {
		this.productImages = productImages;
	}
	public Double getProductWeight() {
		return productWeight;
	}
	public void setProductWeight(Double productWeight) {
		this.productWeight = productWeight;
	}
	public Boolean getIsProstbusSize() {
		return isProstbusSize;
	}
	public void setIsProstbusSize(Boolean isProstbusSize) {
		this.isProstbusSize = isProstbusSize;
	}	
	
		
	public void setAvailableQty(Integer availableQty) {
		this.availableQty = availableQty;
	}
	public Integer getAvailableQty() {
		return this.availableQty;
	}
	public Double getPackageWidth() {
		return packageWidth;
	}
	public void setPackageWidth(Double packageWidth) {
		this.packageWidth = packageWidth;
	}
	public Double getPackageLength() {
		return packageLength;
	}
	public void setPackageLength(Double packageLength) {
		this.packageLength = packageLength;
	}
	public Double getPackageHeight() {
		return packageHeight;
	}
	public void setPackageHeight(Double packageHeight) {
		this.packageHeight = packageHeight;
	}
	@Override
	public String toString() {
		return "Product [productName=" + productName + "qty:"+availableQty+ "]";
	}
	
	
}
