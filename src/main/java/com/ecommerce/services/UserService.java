package com.ecommerce.services;

import com.ecommerce.model.User_Details;

public interface UserService {

    public User_Details saveUser(User_Details user);

    public User_Details getUserByEmail(String email);

}
