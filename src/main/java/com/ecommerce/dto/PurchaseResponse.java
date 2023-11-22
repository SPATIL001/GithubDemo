package com.ecommerce.dto;

import jakarta.annotation.Nonnull;
import lombok.Data;

@Data
public class PurchaseResponse {
	
   @Nonnull
	private String orderTrackingNumber;

}
