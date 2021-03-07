package com.smallcase.trade.controllers;

import com.smallcase.trade.entities.dao.User;
import com.smallcase.trade.entities.dto.ResponseDTO;
import com.smallcase.trade.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/smallcase/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getUserById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(new ResponseDTO(userService.getUserById(id)));
    }

    @PostMapping()
    public ResponseEntity<ResponseDTO> getUserById(@RequestBody User user) {
        return ResponseEntity.ok(new ResponseDTO(userService.addUser(user)));
    }
}
