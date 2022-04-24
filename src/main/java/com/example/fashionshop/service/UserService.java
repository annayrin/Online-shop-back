package com.example.fashionshop.service;

import com.example.fashionshop.model.User;

import java.util.List;

public interface UserService {

    User create(User user);

    User getById(String id);

    User getByIdForSingUp(String id);

    boolean isExists(String id);

    List<User> getAll();

    void delete(String id);
}
