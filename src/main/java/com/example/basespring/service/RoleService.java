package com.example.basespring.service;

import com.example.basespring.entities.Roles;
import com.example.basespring.enums.Enums;
import com.example.basespring.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {
    @Autowired
    RoleRepository roleRepository;

    public Optional<Roles> findByName(Enums.Roles name) {
        return roleRepository.findByName(name);
    }
}
