package com.example.basespring.controller;

import com.example.basespring.dto.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("")
@Slf4j
public class HomeController {
    @GetMapping("")
    public String getList(Model model, @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                          @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        try {
            LoginRequest loginRequest = new LoginRequest();
            model.addAttribute("loginRequest", loginRequest);
            return "/v1/index";
        } catch (Exception e) {
            return "v1/fail";
        }
    }
}
