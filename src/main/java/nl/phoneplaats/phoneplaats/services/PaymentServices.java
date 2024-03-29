package nl.phoneplaats.phoneplaats.services;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import be.feelio.mollie.Client;
import be.feelio.mollie.ClientBuilder;
import be.feelio.mollie.data.common.Amount;
import be.feelio.mollie.data.common.Locale;
import be.feelio.mollie.data.payment.PaymentRequest;
import be.feelio.mollie.data.payment.PaymentResponse;
import be.feelio.mollie.exception.MollieException;
import nl.phoneplaats.phoneplaats.SystemConstants;
import nl.phoneplaats.phoneplaats.dto.Order;
import nl.phoneplaats.phoneplaats.repo.OrderRepo;

@Service
public class PaymentServices {
	private static final Logger logger = LoggerFactory.getLogger(PaymentServices.class);
	@Autowired
	private OrderRepo orderRepo;
	//access token : access_37DBe46Hy6HbmBFquPbnuu4preuMKPvmhmStJ6yv
	//profile id : pfl_DPxajhSzEr
	//access token: access_37DBe46Hy6HbmBFquPbnuu4preuMKPvmhmStJ6yv
	public String createPayment(double orderTotal, Order order, HttpServletRequest request) {
		String redirectURL = request.getProtocol().substring(0,request.getProtocol().indexOf('/'))
				+"://"
				+request.getServerName()
				+ ":"
				+ request.getServerPort()
				+ request.getContextPath()
				+ "/confirmation";
		logger.debug("Redirect URL : " + redirectURL);
		
		DecimalFormat f = new DecimalFormat("##.00");
		
		Client client = new ClientBuilder()
	            .withApiKey(SystemConstants.MOLLIE_TEST_KEY)
	            .build();

		logger.debug("client for payment is created, now creating payment, amount : "+f.format(order.getOrderTotal()));
		PaymentRequest paymentRequest = PaymentRequest.builder()
                .amount(Amount.builder()
                        .currency("EUR")//String.valueOf(Math.round(orderTotal*100d)/100d)
                        .value(f.format(order.getOrderTotal()))
                        .build())
                .description(order.getFunctionalId())
                .redirectUrl(Optional.of(redirectURL+"?orderId="+order.getFunctionalId()))
                .locale(Optional.of(Locale.nl_NL))
                .build();
		// .method(Optional.of(PaymentMethod.IDEAL)) needs to be enabled https://docs.mollie.com/reference/v2/profiles-api/enable-method
			//logger.debug("My profile id : " +client.profiles().getMyProfile().getId());
			
			logger.debug("payment request is creaeted");
			//epaymentRequest.setProfileId(Optional.of("pfl_DPxajhSzEr"));
			try {
				PaymentResponse paymentResponse = client.payments().createPayment(paymentRequest);			
				order.setStatus(paymentResponse.getStatus());
				logger.debug("payment status for the order: "+ order.getStatus());
				
				order.setPaymentId(paymentResponse.getId());
				logger.debug("payment id for the order : " + order + " , " + order.getPaymentId());
				order.setOrderDate(LocalDateTime.now());
				logger.debug("saving order to DB ..");
				orderRepo.save(order);

				logger.debug("the payment : " + paymentResponse.getStatus() + " " +paymentResponse.getAmount()+" "+paymentResponse.getLinks().getStatus());
				logger.debug("the link : "+paymentResponse.getLinks().getCheckout().getHref());
				return paymentResponse.getLinks().getCheckout().getHref();		
			
			}catch (MollieException e) {
				logger.error("ERROR",e);
				logger.debug("deleting order " + order.getFunctionalId());
				orderRepo.deleteByOrderId(order.getOrderId());
				return "shippingInfo?error=PaymentServerError";
			}
	
	}
	
	public boolean isPaymentCompleted(Order order) throws MollieException {
		Client client = new ClientBuilder()
	            .withApiKey(SystemConstants.MOLLIE_TEST_KEY)
	            //.withTestMode(true)
	            //.withOrganizationToken(SystemConstants.MOLLIE_ACCESS_TOKEN)
	            .build();
		
		logger.debug("checking the payment with id: "+order.getPaymentId());
		PaymentResponse paymentResponse = client.payments().getPayment(order.getPaymentId());
		logger.debug("payment status : "+paymentResponse.getStatus());
		if (paymentResponse.getStatus().equalsIgnoreCase("betaald")
				|| paymentResponse.getStatus().equalsIgnoreCase("paid")) {
			order.setStatus("paid");
			orderRepo.save(order);
			return true;
		}
		return false;
		
	}
}//class
