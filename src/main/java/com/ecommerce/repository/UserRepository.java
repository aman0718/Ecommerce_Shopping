package com.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.model.User_Details;

public interface UserRepository extends JpaRepository<User_Details, Integer>{

    public User_Details findByEmail(String email);

}
