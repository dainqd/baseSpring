package com.example.basespring.restapi;

import com.example.basespring.entities.Accounts;
import com.example.basespring.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
public class AccountApi {
    @Autowired
    UserDetailsServiceImpl userDetailsServiceimpl;

    @Autowired
    PasswordEncoder encoder;

    @GetMapping("list")
    public ResponseEntity<List<Accounts>> getList() {
        return ResponseEntity.ok(userDetailsServiceimpl.findAll());
    }

    @GetMapping("detail/{id}")
    public ResponseEntity<?> getDetail(@PathVariable Long id) {
        Accounts optionalUser = userDetailsServiceimpl.findById(id);
        return ResponseEntity.ok(optionalUser);
    }
}
