package com.smallcase.trade.controllers;

import com.smallcase.trade.entities.dto.ResponseDTO;
import com.smallcase.trade.services.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/smallcase/portfolio")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @GetMapping("/user/{user_id}")
    public ResponseEntity<ResponseDTO> getAllPortfolioForUser(@PathVariable("user_id") Integer userId) {
        return ResponseEntity.ok(new ResponseDTO(portfolioService.getAllPortfolioForUser(userId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getPortfolioForUser(@PathVariable("id") Integer id,
                                                           @RequestParam("user_id") Integer userId) {
        return ResponseEntity.ok(new ResponseDTO(portfolioService.getPortfolio(id,userId)));
    }

    @GetMapping("/{id}/returns")
    public ResponseEntity<ResponseDTO> getReturnOnPortfolioForUser(@PathVariable("id") Integer id,
                                                                   @RequestParam("user_id") Integer userId) {
        return ResponseEntity.ok(new ResponseDTO(portfolioService.getReturnOnPortfolioForUser(id,userId)));
    }

    @PostMapping
    private ResponseEntity<ResponseDTO> createPortfolio(@RequestParam("user_id") Integer userId) {
        return ResponseEntity.ok(new ResponseDTO(portfolioService.createPortfolio(userId)));
    }
}
