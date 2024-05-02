package com.example.demo.controller;

import com.example.demo.model.dto.response.AdvisorResDto;
import com.example.demo.service.AdvisorService;
import com.example.demo.util.SecurityUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/advisor")
public class AdvisorController {

    @Autowired
    private AdvisorService advisorService;

    @GetMapping("/getAdvice")
    public AdvisorResDto getAdvice() {
        String username = SecurityUtil.getUsername();
        return advisorService.getAdvice(username);
    }
}
