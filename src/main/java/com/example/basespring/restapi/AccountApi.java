package com.example.basespring.restapi;

import com.example.basespring.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class AccountApi {
    @Autowired
    UserDetailsServiceImpl userDetailsServiceimpl;

    @Autowired
    PasswordEncoder encoder;


}
