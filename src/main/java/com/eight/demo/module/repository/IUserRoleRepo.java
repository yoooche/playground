package com.eight.demo.module.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.eight.demo.module.model.UserRole;

public interface IUserRoleRepo extends JpaRepository<UserRole, Integer> {

    @Query(value = "select ur.userId from UserRole ur where ur.roleId = :roleId")
    List<Integer> findUserIdByRoleId(Integer roleId);
}
