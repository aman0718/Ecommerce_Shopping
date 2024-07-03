package com.ecommerce.CategoryServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.model.User_Details;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User_Details saveUser(User_Details user) {
        user.setRole("ROLE_USER");
        String encodePass = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePass);
        User_Details saveUser = userRepository.save(user);

        return saveUser;
    }

    @Override
    public User_Details getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
