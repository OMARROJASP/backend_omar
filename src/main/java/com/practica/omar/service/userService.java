package com.practica.omar.service;

import java.util.List;
import java.util.Optional;

import com.practica.omar.model.entities.User;

public interface userService {

    List<User> findAll();

    Optional<User> findById(Long id);

    User save(User user);

    Optional<User> update(User user, Long id);

    void remove(Long id);

}
