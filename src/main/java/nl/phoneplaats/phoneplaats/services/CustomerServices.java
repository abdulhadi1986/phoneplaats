package nl.phoneplaats.phoneplaats.services;

import org.springframework.stereotype.Component;

import nl.phoneplaats.phoneplaats.dto.Customer;

@Component
public class CustomerServices {
	public static boolean isValidCustomer(Customer customer) {
		/*
		if ((customer.getEmail()==null || customer.getEmail().trim().isEmpty())
				|| (customer.getPostcode()==null ||customer.getPostcode().trim().isEmpty())
				|| (customer.getHouseNo()==null || customer.getHouseNo().trim().isEmpty())
				|| (customer.getLastName()==null || customer.getLastName().trim().isEmpty())
				)
			return false; 
		if (customer.getEmail().matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$"))
			return false;
		*/
		return true;
	}

}
