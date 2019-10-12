package nl.phoneplaats.phoneplaats.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import nl.phoneplaats.phoneplaats.SystemConstants;
import nl.phoneplaats.phoneplaats.dto.Category;
import nl.phoneplaats.phoneplaats.dto.Inventory;
import nl.phoneplaats.phoneplaats.dto.Order;
import nl.phoneplaats.phoneplaats.dto.OrderDetail;
import nl.phoneplaats.phoneplaats.dto.Product;
import nl.phoneplaats.phoneplaats.dto.ProductImage;
import nl.phoneplaats.phoneplaats.repo.CategoryRepo;
import nl.phoneplaats.phoneplaats.repo.ImageRepo;
import nl.phoneplaats.phoneplaats.repo.InventoryRepo;
import nl.phoneplaats.phoneplaats.repo.OrderDetailRepo;
import nl.phoneplaats.phoneplaats.repo.ProductRepo;
import nl.phoneplaats.phoneplaats.services.AdminServices;

@Transactional
@Controller
public class AdminController {
	//this is just a comment from iMac
	@Autowired
	private ProductRepo productRepo;
	
	@Autowired
	private InventoryRepo inventoryRepo;
	
	@Autowired
	private CategoryRepo categoryRepo;
	
	@Autowired
	private ImageRepo imageRepo;
	
	@Autowired
	private AdminServices adminServices;
	
	@Autowired
	private OrderDetailRepo orderDetailRepo;
	
	private static Map<String, List<Order>> salesOverview = new HashMap<>();
	
	private static Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public String logout(HttpSession session) {
		session.invalidate();
		return "login";
	}
	@RequestMapping(value="/admin/mgt/login", method=RequestMethod.GET)
	public String getLoginPage(HttpSession session) {
		
		return "login";
	}
	@RequestMapping(value="/admin/mgt/login", method=RequestMethod.POST)
	public String checkLogin(String userName , String password, HttpSession session) {
		if (userName.equals(SystemConstants.ADMIN_USERNAME) && password.equals(SystemConstants.ADMIN_PASSWORD)) {
			session.setAttribute("adminLoggedIn", "true");
			session.setMaxInactiveInterval(300);
			return "redirect:/admin/mgt";
		}
		return "login";
	}
	
	@RequestMapping(value="/admin/mgt", method=RequestMethod.GET)
	public String getProductsManagement(Model model
										, HttpSession session
										,@RequestParam(value="productId", required=false, defaultValue="0")int productId
										,@RequestParam(value="error", required=false, defaultValue="") String error) {
		logger.debug("the attribute in the session : " +session.getAttribute("adminLoggedIn") );
		if (session.getAttribute("adminLoggedIn") == null || session.getAttribute("adminLoggedIn").equals("false") )
			return "login";
		
		model.addAttribute("allProducts", inventoryRepo.findAll());
		model.addAttribute("categories", categoryRepo.findAll());
		model.addAttribute("product", productId ==0 ?new Product():productRepo.findByProductId(productId));
		model.addAttribute("error", error);
		model.addAttribute("productImage", new ProductImage());
		
		salesOverview= adminServices.getSalesOverview();
				
		return "productsmgt";
	}
	
	@RequestMapping(value="/admin/mgt/saveProduct", method=RequestMethod.POST)
	public String saveProduct(Product product, HttpSession session) {
		if (session.getAttribute("adminLoggedIn") == null || session.getAttribute("adminLoggedIn").equals("false") )
			return "login";
		
		if (product.getProductId() == 0) {
			if (productRepo.findByProductName(product.getProductName()) != null) {
				return "redirect:/admin/mgt?error='product with the same name already exists'";
			}
			//save item to the database and get the generated ID
			productRepo.save(product);
			product.setProductId(productRepo.findByProductName(product.getProductName()).getProductId());
			
			//save item to the inventory 
			Inventory inventoryItem = new Inventory();
			inventoryItem.setProduct(product);
			inventoryItem.setStockQuantity(product.getAvailableQty());
			inventoryRepo.save(inventoryItem);
			
			return "redirect:/admin/mgt/addimage?productId="+product.getProductId()+"&productName='"+product.getProductName()+"'";
		}
		
		productRepo.save(product);
		
		return "redirect:/admin/mgt";
	}
	
	@RequestMapping(value="/admin/mgt/addimage", method=RequestMethod.GET)
	public String getAddImage(@RequestParam("productId")int productId
								, Model model
								,@RequestParam("productName")String productName
								,HttpSession session) {
		if (session.getAttribute("adminLoggedIn") == null || session.getAttribute("adminLoggedIn").equals("false") )
			return "login";
		
		model.addAttribute("productId", productId);
		model.addAttribute("productName", productName);
		return "addimages";
	}
	
	@RequestMapping(value="/admin/mgt/addImages", method=RequestMethod.POST)
	public String uploadImage(@RequestParam("imageFile") MultipartFile[] imageFiles
							, HttpServletRequest request 
							,@RequestParam("productId") int productId
							,HttpSession session) {
		if (session.getAttribute("adminLoggedIn") == null || session.getAttribute("adminLoggedIn").equals("false") )
			return "login";
		
		Product product = productRepo.findByProductId(productId); 
		if (imageRepo.findByProduct(product) != null) {
			for (ProductImage image : imageRepo.findByProduct(product)) {
				image.setProduct(null);
				image.setImageUri("");
				imageRepo.save(image);
			}
		}
		try {
			
			int i =0;
			for (MultipartFile imageFile : imageFiles) {
			ProductImage productImage = new ProductImage();
			byte[] imageByte = imageFile.getBytes();
						
			String folder = request.getServletContext().getRealPath("/WEB-INF/classes/static/img/product");
			Path path = Paths.get(folder + "/" + product.getProductName() +"-"+ i+".jpg");
			logger.debug("the path now is  : "+ path.toString());
			
			
			//populate images list item
			productImage.setProduct(product);
			productImage.setImageUri("img/product/"+ product.getProductName() +"-"+ i+".jpg");
			product.getProductImages().add(productImage);
			imageRepo.save(productImage);
			
			Files.write(path, imageByte);
			
			i++;
			}
		} catch (IOException e) {
			
			logger.debug("error occured: " + e.getMessage());
		}
		return "redirect:/admin/mgt";
	}
	
	@RequestMapping(value="/admin/mgt/editQuantity" , method=RequestMethod.POST)
	public String editQuantity(int productId, int qty, HttpSession session) {
		if (session.getAttribute("adminLoggedIn") == null || session.getAttribute("adminLoggedIn").equals("false") )
			return "login";
		Inventory inventoryItem = inventoryRepo.findByProduct(productRepo.findByProductId(productId));
		inventoryItem.setStockQuantity(qty);
		inventoryRepo.save(inventoryItem);
		//TODO
		return "redirect:/admin/mgt";
	}
	
	@RequestMapping(value="/admin/mgt/editProduct", method=RequestMethod.GET)
	public String editProduct(@RequestParam("productId")int productId, HttpSession session) {
		if (session.getAttribute("adminLoggedIn") == null || session.getAttribute("adminLoggedIn").equals("false") )
			return "login";
		return "redirect:/admin/mgt?productId="+productId;
		
	}
	
	@RequestMapping(value="/admin/mgt/addcategory", method=RequestMethod.GET)
	public String getCategories(Model model, HttpSession session) {
		if (session.getAttribute("adminLoggedIn") == null || session.getAttribute("adminLoggedIn").equals("false") )
			return "login";
		
		model.addAttribute("allCategories", categoryRepo.findAll());
		return "addcategory";		
	}
	
	@RequestMapping(value="/admin/mgt/addcategory", method=RequestMethod.POST)
	public String addCategory(Category category, HttpSession session) {
		if (session.getAttribute("adminLoggedIn") == null || session.getAttribute("adminLoggedIn").equals("false") )
			return "login";
		
		categoryRepo.save(category);
		return "redirect:/admin/mgt/addcategory";		
	}
	
	@RequestMapping(value="/admin/salesoverview", method=RequestMethod.GET)
	public String getSalesOverview(Model model, HttpSession session){
		if (session.getAttribute("adminLoggedIn") == null || session.getAttribute("adminLoggedIn").equals("false") )
			return "login";
		Map<String, String> overviewGroupByMonth = new HashMap<>();
		if (salesOverview.isEmpty())
			salesOverview = adminServices.getSalesOverview();
		for (String month:salesOverview.keySet()) {
			Double subtotal=0.00d;
			for (Order order : salesOverview.get(month)) {
				subtotal += order.getOrderTotal();
			}
			overviewGroupByMonth.put(month, String.valueOf(subtotal));			
		}
		model.addAttribute("allSales", overviewGroupByMonth);
		return "salesoverview";
	}
	
	@RequestMapping(value="/admin/monthlysalesoverview", method=RequestMethod.GET)
	public String getSalesForMonth(@RequestParam("month") String month, Model model, HttpSession session) {
		if (session.getAttribute("adminLoggedIn") == null || session.getAttribute("adminLoggedIn").equals("false") )
			return "login";
		
		List <OrderDetail> allOrders = new ArrayList<>();
		for (Order order : salesOverview.get(month)) {
			allOrders.addAll(orderDetailRepo.findByOrderOrderId(order.getOrderId()));
		}
		model.addAttribute("month", month);
		model.addAttribute("allOrders", allOrders);
		return "monthlysales";
		
	}
	
}
