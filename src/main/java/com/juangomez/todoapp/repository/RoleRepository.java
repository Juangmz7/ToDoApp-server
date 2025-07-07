package com.juangomez.todoapp.repository;

import com.juangomez.todoapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String s);
}
