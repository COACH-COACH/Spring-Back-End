package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.entity.User;

public interface UserRepo extends JpaRepository<User, Integer> {
	
	Boolean existsByLoginId(String LoginId);
	
	//username을 받아 DB 테이블에서 회원을 조회하는 메소드 작성
    User findByLoginId(String LoginId);
}
