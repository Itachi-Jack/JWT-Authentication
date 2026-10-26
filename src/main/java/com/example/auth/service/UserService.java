package com.example.auth.service;

import com.example.auth.entities.User;
import com.example.auth.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User getUserById(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Id not found"));

        return user;
    }

    public String DeleteById(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Id not found"));

        userRepository.deleteById(id);

        return "Id deleted successfully";
    }

    public User getByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);

    }

    public User saveUser(User user){
        userRepository.save(user);
        return user;
    }


}
