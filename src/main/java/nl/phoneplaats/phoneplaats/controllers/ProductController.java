package nl.phoneplaats.phoneplaats.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import nl.phoneplaats.phoneplaats.dao.InventoryDao;
import nl.phoneplaats.phoneplaats.dao.ProductDao;
import nl.phoneplaats.phoneplaats.dto.Product;
import nl.phoneplaats.phoneplaats.services.EmailServices;
import nl.phoneplaats.phoneplaats.services.GeneralServices;
import nl.phoneplaats.phoneplaats.services.ProductServices;

@Controller
public class ProductController {
	
	@Autowired
	private InventoryDao inventoryDao;
	@Autowired
	private ProductDao productDao;
	@Autowired
	private ProductServices productServices;
	@Autowired
	private GeneralServices generalServices;
	@Autowired
	private EmailServices emailService;
	
	private static Logger logger = LoggerFactory.getLogger(ProductController.class);
	
	@RequestMapping(value= {"/","/home","/products","/index"}, method=RequestMethod.GET)
	public String getAllProducts(Model model 
								, HttpSession session
								,@RequestParam(value="categoryId", required=false, defaultValue="0") int categoryId) {
		
		generalServices.setPageHeader(model, session);
		List<Product> allProducts = new ArrayList<>();
		
		for (Product product:inventoryDao.getAllProducts(categoryId)) {
			productDao.setProductImages(product);
			allProducts.add(product);
		}
		
		model.addAttribute("allProducts", allProducts);
		
		return "home";
	}
	
	@RequestMapping(value="/productDetails", method=RequestMethod.GET)
	public String getProductDetails(Model model, @RequestParam(value="prodId", required=true) int prodId,
												@RequestParam(value="error", required=false, defaultValue="0") int error
												,HttpSession session) {
			
		generalServices.setPageHeader(model, session);
			Product product = productServices.setProductInfo(prodId);
			model.addAttribute("product", product);
			if(error == 1) {
				model.addAttribute("quantityerror", "Het aantal beschikbare artikelen op dit moment is("
						+ inventoryDao.getProductInventory(product)+")");	
			}
			logger.debug("product images: " + product.getProductImages());
			
			model.addAttribute("formattedSpec", productServices.getFormattedSpec(product));
						
		return "productDetails";
	}
	
	@RequestMapping(value="/contact", method=RequestMethod.GET)
	public String getContactUs(Model model , HttpSession session, @RequestParam(value="message", required=false, defaultValue="") String message) {
		generalServices.setPageHeader(model, session);
		String messageParam="";
		String errorMessage="";
		if (message.contentEquals("sent")) {
			messageParam= "Uw message is verzonden. U krijgt een reactie van ons z.s.m.";
		}else if (message.equals("messinginfo")) {
			errorMessage= "Alle velden moet ingevuld worden!";
		}
		
		model.addAttribute("messageParam", messageParam);
		model.addAttribute("errorMessage", errorMessage);
		return "contact-ons";
		
	}
	
	@RequestMapping(value="/sendContactMessage" , method=RequestMethod.POST)
	public String sendMessageFromContact(String name, String email, String subject, String message) {
		if(name.trim().isEmpty() || email.trim().isEmpty()||subject.trim().isEmpty() || message.trim().isEmpty())
			return "redirect:/contact?message=messinginfo";
		EmailServices.sendEmailFromContactPage(name, email, subject, message);
		return "redirect:/contact?message=sent";
	}
	
}
