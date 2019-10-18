package nl.phoneplaats.phoneplaats.services;

import java.io.IOException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sun.mail.smtp.SMTPMessage;

import nl.phoneplaats.phoneplaats.SystemConstants;
import nl.phoneplaats.phoneplaats.dto.Order;
import nl.phoneplaats.phoneplaats.dto.OrderDetail;

@Service
public class EmailServices {
	private static final Logger logger = LoggerFactory.getLogger(EmailServices.class);
	
	public static void sendOrderToSeller(Order order) throws MessagingException {
		Session session = buildGoogleSession();
		String emailTitle = "New Order op phoneplaats.nl - "+order.getOrderDetails().size() +" item(s)";
		String orderItems=""; 
		for (OrderDetail orderDetail : order.getOrderDetails()) {
			orderItems+= orderDetail.getProduct().getProductName() +" amount: "+ orderDetail.getQuantity()+ "\n";
		}
		String emailBody = "order no. " + order.getFunctionalId() 
						+"\n"
						+orderItems
						+"\n"
						+"Order Total : " + order.getOrderTotal()
						+"\n"
						+"customer : "
						+order.getCustomer().getSalutation() +" "+ order.getCustomer().getFirstName() +" "+order.getCustomer().getLastName()
						+"\n"
						+"Address : "
						+order.getCustomer().getStreet() + " "+order.getCustomer().getHouseNo()
						+"\n"
						+order.getCustomer().getPostcode() + " "+ order.getCustomer().getCity();
			      
	    Message simpleMessage = buildSimpleMessage(session, emailTitle,emailBody);
		sendMessageToAddress(simpleMessage, SystemConstants.INFO_EMAIL);
	}
	
	public static void sendOrderConfirmationToCustomer(Order order) throws MessagingException, IOException {
		Session session = buildGoogleSession();
		String emailTitle = "Bevestiging van uw bestellen op PhonePlaats.nl orderNo. "+order.getFunctionalId();
		String emailBody = getEmailBodyOrderConfirmation(order);
			      
	    Message simpleMessage = buildMessageWithEmbeddedImage(session, order, emailTitle,emailBody);
	    sendMessageToAddress(simpleMessage, order.getCustomer().getEmail());
	}
	
	/**
	   * Build a Session object for an SMTP server that requires both TSL and
	   * authentication. This uses Gmail as an example of such a server
	   * 
	   * @return a Session for sending email
	   */
	  public static Session buildGoogleSession() {
	    Properties mailProps = new Properties();
	    mailProps.put("mail.transport.protocol", "smtp");
	    mailProps.put("mail.host", "smtp.gmail.com");
	    mailProps.put("mail.from", SystemConstants.NO_REPLY_EMAIL);
	    mailProps.put("mail.smtp.starttls.enable", "true");
	    mailProps.put("mail.smtp.port", "587");
	    mailProps.put("mail.smtp.auth", "true");
	  
	    // final, because we're using it in the closure below
	    final PasswordAuthentication usernamePassword = new PasswordAuthentication(
	    		SystemConstants.NO_REPLY_EMAIL, SystemConstants.NO_REPLY_PASSWORD);
	    Authenticator auth = new Authenticator() {
	      protected PasswordAuthentication getPasswordAuthentication() {
	        return usernamePassword;
	      }
	    };
	    Session session = Session.getInstance(mailProps, auth);
	    session.setDebug(true);
	    return session;

	  }
	  
	  /**
	   * Build a simple text message - no attachments.
	   * 
	   * @param session
	   * @return a multipart MIME message with only one part, a simple text message.
	   * @throws MessagingException
	   */
	  public static Message buildSimpleMessage(Session session, String emailTitle, String emailBody)
	      throws MessagingException {
	    SMTPMessage m = new SMTPMessage(session);
	    MimeMultipart content = new MimeMultipart();
	    MimeBodyPart mainPart = new MimeBodyPart();
	    mainPart.setText(emailBody);
	    content.addBodyPart(mainPart);
	    m.setContent(content);
	    m.setSubject(emailTitle);
	    return m;
	  }
	  
	  /**
	   * Send the message with Transport.send(Message, Address[])
	   * 
	   * @param message
	   * @param recipient
	   * @throws MessagingException
	   */
	  public static void sendMessageToAddress(Message message, String recipient)
	      throws MessagingException {
	    InternetAddress[] recipients = { new InternetAddress(recipient) };
	    Transport.send(message, recipients);
	  }
	  
	  public static Message buildMessageWithEmbeddedImage(Session session, Order order, String emailTitle, String emailBody)
		      throws MessagingException, IOException {
		    SMTPMessage m = new SMTPMessage(session);
		    MimeMultipart content = new MimeMultipart("related");
		    
		    
		    
		    
		    // HTML part
		    MimeBodyPart textPart = new MimeBodyPart();
		    textPart.setText(emailBody,"US-ASCII", "html");
		    content.addBodyPart(textPart);

		// Image part
		// ContentID is used by both parts
		/*
		 * String cid = String.valueOf(((Math.round(Math.random() * 100d) / 100d)*100));
		 * MimeBodyPart imagePart = new MimeBodyPart();
		 * imagePart.attachFile("img/logo-sm.png"); imagePart.setContentID("<" + cid +
		 * ">"); imagePart.setDisposition(MimeBodyPart.INLINE);
		 * content.addBodyPart(imagePart);
		 */
		    
		    m.setContent(content);
		    m.setSubject(emailTitle);
		    return m;
		  }
	  
	  private static String getEmailBodyOrderConfirmation(Order order) {
			String orderOverview="";
			for (OrderDetail orderDetail : order.getOrderDetails()) {
				  orderOverview += "<li> <p>Artikelnaam: "+ orderDetail.getProduct().getProductName()+"</p>"
			  					+"<p>  - Qty: "+orderDetail.getQuantity() +"</p>" 
			  					+"<p>  - Prijs per stuk: "+orderDetail.getProduct().getProductPrice() +"</p> </li>";
			}
			
			String emailBody = "<html><head>"
				      + "<title>PhonePlaats.nl</title>"
				      + "</head>\n"
				      + "<body><div><b>Beste "+order.getCustomer().getSalutation() + " "+ order.getCustomer().getLastName() +"</b></div>"
				      + "<div>Hartelijk dank voor jouw bestelling bij <strong>PhonePlaats.nl</strong>. </br>"
					  + "Jouw bestelnummer is <strong>" + order.getFunctionalId()+ "</strong> ." 
					  + "<p>Uw bestelling is ontvangen en het wordt nu in behandeling.</p>"
					  +"</br>"
					  +"</br>\r\n" + 
					  "	<h3> Overzicht van de bestelling</h3>\r\n" + 
					  "	</br>\r\n" + 
					  "	\r\n" + 
					  "	<ul>"+ 
					  orderOverview+ 
					  "	\r\n" + 
					  "    </ul>\r\n" + 
					  "	  <ui><li> <h4>Verzendkosten: Gratis</h4></li>\r\n" + 
					  "	  <li> <h4>Totaal: "+order.getOrderTotal()+ "</h4></li>\r\n" + 
					  "	 </ul> 	"+
					  "</br>"+
					  "<div>"+
	                      "<h3>Contact ons</h3>"+    
	                      "<p>Voor alle vragen kunt u contact opnemen met ons klantservice op de onderstaande contactgegevens of u kunt een drict bericht sturen</p>"+
	                      "<ul>"+
	                          "<li><a href=\"#\">" + SystemConstants.INFO_EMAIL + "</a></li>"+
	                          "<li>"+ SystemConstants.INFO_PHONE + "</li>"+
	                          "<li> Address : "+ SystemConstants.INFO_ADDRESS + "</li>"+
	                      "</ul>"+             
                      "</div>"+	
                      "</br>"+
				      "</body></html>";
			return emailBody;
		}
	  
	  public static void sendEmailFromContactPage(String senderName, String senderEmail, String subject, String message) {
			logger.debug("sending message from Contact Us Page " + senderEmail + " " + subject);
		  	Session session = buildGoogleSession();
			String emailTitle = "New message from phoneplaats.nl with subject: - "+subject;
			
			String emailBody = "from: "+senderName +"\n"+ 
								"email: "+senderEmail+"\n"+
								"subject: " +subject+ "\n"+
								message;
				      
		    Message simpleMessage;
			try {
				simpleMessage = buildSimpleMessage(session, emailTitle,emailBody);
				sendMessageToAddress(simpleMessage, SystemConstants.INFO_EMAIL);
			} catch (MessagingException e) {
				logger.debug("error sending message from Contact Us page " + e.getMessage());
				e.printStackTrace();
			}
			
		  
	  }
	  
	  public static void sendEmailForConfirmationFailure(Order order) {
		  Session session = buildGoogleSession();
		  String emailTitle = "Please check the order " + order.getFunctionalId() ;
		  String emailBody = "The payment was successfull but the client received no confirmation please check the database for more info";
		  Message simpleMessage;
		  try {
				simpleMessage = buildSimpleMessage(session, emailTitle, emailBody);
				sendMessageToAddress(simpleMessage, SystemConstants.INFO_EMAIL);
		  }catch (MessagingException e) {
				logger.debug("error sending message with failure confirmation" + e.getMessage());
				e.printStackTrace();
		  }
		  
	  }
}
