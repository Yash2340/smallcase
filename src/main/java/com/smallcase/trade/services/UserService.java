package com.smallcase.trade.services;

import com.smallcase.trade.entities.dao.User;

import java.util.List;

public interface UserService {

    User getUserById(int id);

    User addUser(User user);

    List<User> getUsers();
}
