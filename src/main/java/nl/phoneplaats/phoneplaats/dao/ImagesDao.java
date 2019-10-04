package nl.phoneplaats.phoneplaats.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.phoneplaats.phoneplaats.dto.ProductImage;
import nl.phoneplaats.phoneplaats.repo.ImageRepo;

@Service
public class ImagesDao {
	@Autowired
	private ImageRepo imageRepo;
	
	

}
