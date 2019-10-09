package nl.phoneplaats.phoneplaats.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="orders")
public class Order {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="order_id")
	private int orderId;
	@Column(name="func_id")
	private String functionalId;
	@Column(name="order_date")
	private LocalDateTime orderDate;
	@Column(name="order_total")
	private double orderTotal;
	@Column(name="payment_id")
	private String paymentId;
	@Column(name="status")
	private String status;
	
	@ManyToOne 
	@JoinColumn(name="customer_id") 
	private Customer customer;
	 
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="order_id")
	private List<OrderDetail> orderDetails;
	
	@Column(name="shipping_cost")
	private Double shippingCost;
	
	@Column(name="shippment_date")
	private LocalDate shippmentDate;

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public double getOrderTotal() {
		return orderTotal;
	}

	public void setOrderTotal(double orderTotal) {
		this.orderTotal = orderTotal;
	}

	public List<OrderDetail> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(List<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Double getShippingCost() {
		return shippingCost;
	}

	public void setShippingCost(Double shippingCost) {
		this.shippingCost = shippingCost;
	}
	

	public String getFunctionalId() {
		return functionalId;
	}

	public void setFunctionalId(String functionalId) {
		this.functionalId = functionalId;
	}
	
	

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
			
	public LocalDate getShippmentDate() {
		return shippmentDate;
	}

	public void setShippmentDate(LocalDate shippmentDate) {
		this.shippmentDate = shippmentDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((functionalId == null) ? 0 : functionalId.hashCode());
		result = prime * result + orderId;
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
		Order other = (Order) obj;
		if (functionalId == null) {
			if (other.functionalId != null)
				return false;
		} else if (!functionalId.equals(other.functionalId))
			return false;
		if (orderId != other.orderId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Order [" + functionalId + "]";
	}

	
	

}
