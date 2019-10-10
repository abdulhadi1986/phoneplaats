package nl.phoneplaats.phoneplaats.repo;


import org.springframework.data.jpa.repository.JpaRepository;

import nl.phoneplaats.phoneplaats.dto.Order;

public interface OrderRepo extends JpaRepository<Order, Integer> {
	public Order findByOrderId(int id);
	public Order findByFunctionalId(String functionalId);
	public void deleteByOrderId(int id);

}
