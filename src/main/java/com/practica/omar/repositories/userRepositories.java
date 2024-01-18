package com.practica.omar.repositories;

import org.springframework.data.repository.CrudRepository;

import com.practica.omar.model.entities.User;

public interface userRepositories extends CrudRepository<User, Long> {
    


}
