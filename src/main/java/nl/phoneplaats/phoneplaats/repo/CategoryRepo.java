package nl.phoneplaats.phoneplaats.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import nl.phoneplaats.phoneplaats.dto.Category;

public interface CategoryRepo extends JpaRepository<Category, Integer> {

}
