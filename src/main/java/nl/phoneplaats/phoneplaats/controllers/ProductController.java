package nl.phoneplaats.phoneplaats.controllers;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
public class ProductController implements ErrorController{
	
	@Autowired
	private InventoryDao inventoryDao;
	@Autowired
	private ProductDao productDao;
	@Autowired
	private ProductServices productServices;
	@Autowired
	private GeneralServices generalServices;

	
	private static Logger logger = LoggerFactory.getLogger(ProductController.class);
	private static final String ERROR_PATH = "/error";
	
	@RequestMapping(value= {"/","/home","/products","/index"}, method=RequestMethod.GET)
	public String getAllProducts(Model model 
								, HttpSession session
								,@RequestParam(value="categoryId", required=false, defaultValue="0") int categoryId) {
		try {
			generalServices.setPageHeader(model, session);
			List<Product> allProducts = new ArrayList<>();
			
			for (Product product:inventoryDao.getAllProducts(categoryId)) {
				productDao.setProductImages(product);
				allProducts.add(product);
			}
			
			model.addAttribute("allProducts", allProducts);		
			
			return "home";
			
		}catch(Exception e) {
			logger.debug("ERROR", e);
			return "error";
		}
		
	}
	
	@RequestMapping(value="/productDetails", method=RequestMethod.GET)
	public String getProductDetails(Model model, @RequestParam(value="prodId", required=true) int prodId,
												@RequestParam(value="error", required=false, defaultValue="0") int error
												,HttpSession session) {
		
		try {
			generalServices.setPageHeader(model, session);
			Product product = productServices.setProductInfo(prodId);
			model.addAttribute("product", product);
			logger.debug("getting product details: "+ product.getProductName());
			if(error == 1) {
				model.addAttribute("quantityerror", "Het aantal beschikbare artikelen op dit moment is("
						+ inventoryDao.getProductInventory(product)+")");
				
			}
					
			model.addAttribute("formattedSpec", productServices.getFormattedSpec(product));
							
			return "productDetails";
			
		}catch(Exception e) {
			logger.debug("ERROR", e);
			return "error";
		}
		
	}
	
	@RequestMapping(value="/contact", method=RequestMethod.GET)
	public String getContactUs(Model model , HttpSession session, @RequestParam(value="message", required=false, defaultValue="") String message) {
		try {
			generalServices.setPageHeader(model, session);
			String messageParam="";
			String errorMessage="";
			if (message.contentEquals("sent")) {
				messageParam= "Uw message is verzonden. U krijgt een reactie van ons z.s.m.";
			}else if (message.equals("messinginfo")) {
				logger.debug("sedning messge from contact us page failed because of empty fields");
				errorMessage= "Alle velden moet ingevuld worden!";
			}
			
			model.addAttribute("messageParam", messageParam);
			model.addAttribute("errorMessage", errorMessage);
			return "contact-ons";
			
		}catch(Exception e) {
			logger.debug("ERROR", e);
			return "error";
		}	
		
	}
	
	@RequestMapping(value="/sendContactMessage" , method=RequestMethod.POST)
	public String sendMessageFromContact(String name, String email, String subject, String message) {
		try {
			if(name.trim().isEmpty() || email.trim().isEmpty()||subject.trim().isEmpty() || message.trim().isEmpty())
				return "redirect:/contact?message=messinginfo";
			EmailServices.sendEmailFromContactPage(name, email, subject, message);
			return "redirect:/contact?message=sent";
		}catch(Exception e) {
			logger.debug("ERROR", e);
			return "error";
		}	
		
	}
	
	@GetMapping("/overons")
	public String getAboutUs(Model model, HttpSession session) {
		generalServices.setPageHeader(model, session);
		return "overons";
	}
	
	@RequestMapping(ERROR_PATH)
	public String errorHandler(HttpServletRequest request, Model model, HttpSession session) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		generalServices.setPageHeader(model, session);
		if (status != null) {
			generalServices.setPageHeader(model, session);
			Integer statusCode = Integer.valueOf(status.toString()); 
			logger.debug("Error Code: " + HttpStatus.NOT_FOUND.value() + " , the catched code : "+ statusCode);
			if (statusCode == HttpStatus.NOT_FOUND.value()) {
				return "404-error";
			}
		}
		
		return "error";
	}

	@Override
	public String getErrorPath() {		
		return ERROR_PATH;
	}
	
}
