package com.practica.omar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.practica.omar.model.entities.User;
import com.practica.omar.repositories.userRepositories;

@Service
public class UserServiceImpl implements userService {

    @Autowired
    private userRepositories userRepositories;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public List<User> findAll() {
         return (List<User>)  userRepositories.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepositories.findById(id);
    }

    @Override
    public User save(User user) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepositories.save(user);
    }

    @Override
    public Optional<User> update(User user, Long id) {
        Optional<User> optional = this.findById(id);
        User userOptional = null;
        if(optional.isPresent()){
            User userdb = optional.orElseThrow();
            userdb.setUsername(user.getUsername());
            userdb.setEmail(user.getEmail());
            userOptional = this.save(userdb);
        }
        return Optional.ofNullable(userOptional);
     }

    @Override
    public void remove(Long id) {
        userRepositories.deleteById(id);
    }

}
