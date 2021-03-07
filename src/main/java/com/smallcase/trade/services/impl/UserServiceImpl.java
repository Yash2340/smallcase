package com.smallcase.trade.services.impl;

import com.smallcase.trade.entities.Data;
import com.smallcase.trade.entities.dao.User;
import com.smallcase.trade.services.UserService;
import com.smallcase.trade.utils.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private ValidationUtil validationUtil;

    @Override
    public User getUserById(int id) {
        validationUtil.validateUserId(id);
        return Data.USER_MAP.get(id);
    }

    @Override
    public User addUser(User user) {
        int id = Data.USER_MAP.size()+1;
        user.setId(id);
        Data.USER_MAP.put(id,user);
        return Data.USER_MAP.get(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(Data.USER_MAP.values());
    }
}
