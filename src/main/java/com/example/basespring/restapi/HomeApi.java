package com.example.basespring.restapi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class HomeApi {
    @GetMapping("/hello")
    public ResponseEntity<?> getList(){
        return ResponseEntity.ok("Hello world");
    }
}
