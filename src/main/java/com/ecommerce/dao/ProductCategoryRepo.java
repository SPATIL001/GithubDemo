package com.ecommerce.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.ecommerce.entity.ProductCategory;


@RepositoryRestResource(collectionResourceRel = "productCategory" , path="product-category")
public interface ProductCategoryRepo extends JpaRepository<ProductCategory, Long> {

}  
