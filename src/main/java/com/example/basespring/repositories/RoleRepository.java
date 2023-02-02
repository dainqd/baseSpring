package com.example.basespring.repositories;

import com.example.basespring.entities.Roles;
import com.example.basespring.enums.Enums;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByName(Enums.Roles name);
}
