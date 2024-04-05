package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.entity.Product;

public interface ProductRepo extends JpaRepository<Product, Integer>{
	
}
