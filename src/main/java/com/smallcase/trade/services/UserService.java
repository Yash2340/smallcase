package com.smallcase.trade.services;

import com.smallcase.trade.entities.dao.User;

public interface UserService {

    User getUserById(int id);

    User addUser(User user);
}
