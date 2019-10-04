package nl.phoneplaats.phoneplaats.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import nl.phoneplaats.phoneplaats.dto.Customer;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {
	public Customer findByEmail(String email);

}
