package com.example.demo.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


// jwt 필터를 통과한 뒤 세션 확인 - 필터를 통과하게 되면 일시적으로 세션을 만듦
@Controller
@ResponseBody
public class MainController {

    @GetMapping("/")
    public String mainP() {

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        
        return "Main Controller : "+name;
    }
}