package com.juangomez.todoapp.repository;

import com.juangomez.todoapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByUsername(String userName);

    User findByUsername(String userName);

    User findByEmail(String email);
}
