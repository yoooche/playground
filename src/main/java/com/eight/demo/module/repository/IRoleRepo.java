package com.eight.demo.module.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eight.demo.module.model.Role;

public interface IRoleRepo extends JpaRepository<Role, Integer> {

    Optional<Role> findByRoleType(String roleType);
}
