package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class BaseTest {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void encryptedPwd() {
        String rawPassword = "shin";
        String encryptedPassword = passwordEncoder.encode(rawPassword);
        System.out.println(encryptedPassword);
    }
}
