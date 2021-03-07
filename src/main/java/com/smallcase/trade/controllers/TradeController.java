package com.smallcase.trade.controllers;

import com.smallcase.trade.entities.dto.ResponseDTO;
import com.smallcase.trade.entities.dto.TradeRequestDto;
import com.smallcase.trade.services.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/smallcase/trade")
public class TradeController {

    @Autowired
    private TradeService tradeService;

    @GetMapping("/user/{user_id}")
    public ResponseEntity<ResponseDTO> getTradesByUser(@PathVariable("user_id") Integer userId) {
        return ResponseEntity.ok(new ResponseDTO(tradeService.getTradesByUser(userId)));
    }

    @GetMapping("/user/{user_id}/ticker/{ticker}")
    public ResponseEntity<ResponseDTO> getTradesByUserAndTicker(@PathVariable("user_id") Integer userId,
                                                                @PathVariable("ticker") String ticker){
        return ResponseEntity.ok(new ResponseDTO(tradeService.getTradesByUserAndTicker(userId,ticker)));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> addTrade(@RequestBody TradeRequestDto requestDto,
                                                @RequestParam("user_id") Integer userId,
                                                @RequestParam("portfolio_id") Integer portfolioId) {
        return ResponseEntity.ok(new ResponseDTO(tradeService.addTrade(userId,requestDto,portfolioId)));
    }

    @DeleteMapping("/{id}/user/{user_id}")
    public ResponseEntity<ResponseDTO> deleteTrade(@PathVariable("id") Integer id,
                                                   @PathVariable("user_id") Integer userId) {
        return ResponseEntity.ok(new ResponseDTO(tradeService.deleteTrade(id,userId)));
    }

    @PutMapping
    private ResponseEntity<ResponseDTO> updateTrade(@RequestBody TradeRequestDto requestDto,
                                                    @RequestParam("user_id") Integer userId) {
        return ResponseEntity.ok(new ResponseDTO(tradeService.updateTrade(userId,requestDto)));
    }
}
