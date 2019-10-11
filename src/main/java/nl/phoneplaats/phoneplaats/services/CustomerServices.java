package nl.phoneplaats.phoneplaats.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import nl.phoneplaats.phoneplaats.dto.Customer;

@Component
public class CustomerServices {
	private static final Logger logger = LoggerFactory.getLogger(CustomerServices.class);
	
	
	public static boolean isValidCustomer(Customer customer) {		
		if ((customer.getEmail()==null || customer.getEmail().trim().isEmpty())
				|| (customer.getPostcode()==null ||customer.getPostcode().trim().isEmpty())
				|| (customer.getHouseNo()==null || customer.getHouseNo().trim().isEmpty())
				|| (customer.getLastName()==null || customer.getLastName().trim().isEmpty())
				) {
			logger.debug("personal info are not valid : " + customer);
			return false;
		}
		if (!customer.getEmail().matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$")) {
			logger.debug("Invalid email address : "+ customer.getEmail());
			return false;
		}
		
		logger.debug("customer is valid: " + customer);
		
		return true;
	}

}
