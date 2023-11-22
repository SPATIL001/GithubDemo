package com.ecommerce.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ecommerce.dao.CustomerRepo;
import com.ecommerce.dto.PaymentInfo;
import com.ecommerce.dto.Purchase;
import com.ecommerce.dto.PurchaseResponse;
import com.ecommerce.entity.Customer;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import jakarta.transaction.Transactional;

@Service
public class CheckOutServiceImpl implements CheckOutService {
	
	@Autowired
	private CustomerRepo customerRepo;
	
	public CheckOutServiceImpl(CustomerRepo customerRepo ,
			@Value("${stripe.key.secret}") String secretKey) {
		this.customerRepo = customerRepo;
		Stripe.apiKey = secretKey;
	}

	@Override
	@Transactional
	public PurchaseResponse placeOrder(Purchase purchase) {
		
		// retrieve the order info from dto
		Order order = purchase.getOrder();
		//generate tracking id
		String orderTrackingNumber = generateOrderTrackingNumber();
		order.setOrderTrackingNumber(orderTrackingNumber);
		//populate order with order items
		Set<OrderItem> orderItems = purchase.getOrderItems();
		orderItems.forEach(item -> order.add(item));
		//populate order with shippingAddress and bilingAddress
		order.setBillingAddress(purchase.getBillingAddress());
		order.setShippingAddress(purchase.getShippingAddress());
		//populate Customer with order
		Customer customer = purchase.getCustomer();
		//check this is existing customer
		String theEmail = customer.getEmail();
	    Customer customerFromdb = customerRepo.findByEmail(theEmail);
	    if(customerFromdb != null) {
	    	customer = customerFromdb;
	    }
		customer.add(order);
		//save to the database
		customerRepo.save(customer);
		//return response
		return new PurchaseResponse(orderTrackingNumber);
	}

	private String generateOrderTrackingNumber() {
		//generate a random UUID number  version:4
		// Universally Unique IDentifier Hard to guess:random/unique
		return UUID.randomUUID().toString();
	}

	@Override
	public PaymentIntent createPaymentIntent(PaymentInfo paymentInfo) throws StripeException {
		List<String> paymentMethodTypes = new ArrayList<>();
		paymentMethodTypes.add("card");
		
		Map<String, Object> params = new HashMap<>();
		params.put("amount", paymentInfo.getAmount());
		params.put("currency", paymentInfo.getCurrency());
		params.put("payment_method_types", paymentMethodTypes);
		params.put("description", "Ecommerece Purchase");
		params.put("receipt_email", paymentInfo.getReceiptEmail());
		return PaymentIntent.create(params);
			
	}

}
