package nl.phoneplaats.phoneplaats.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import nl.phoneplaats.phoneplaats.dto.OrderDetail;

public interface OrderDetailRepo extends JpaRepository<OrderDetail, Integer> {
	public OrderDetail findById(int id);
	//public List<OrderDetail> findByOrderId(int id);

}
