package com.ecommerce.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.ecommerce.entity.Customer;

@RepositoryRestResource
public interface CustomerRepo extends JpaRepository<Customer, Long>{
	
	Customer findByEmail(String theEmail);

}
