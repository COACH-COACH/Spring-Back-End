package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.Payment;
import com.example.demo.model.entity.User;

public interface PaymentRepo extends JpaRepository<Payment, Integer> {

	Optional<User> findByUser(User user);
	
	List<Payment> findByUser_Id(int userId);

}
