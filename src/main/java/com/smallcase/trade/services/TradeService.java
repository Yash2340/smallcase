package com.smallcase.trade.services;

import com.smallcase.trade.entities.dao.Trade;
import com.smallcase.trade.entities.dto.BooleanResponseDto;
import com.smallcase.trade.entities.dto.TradeRequestDto;

import java.util.List;

public interface TradeService {

    List<Trade> getTradesByUser(Integer userId);

    List<Trade> getTradesByUserAndTicker(Integer userId, String ticker);

    Trade addTrade(Integer userId, TradeRequestDto requestDto, Integer portfolioId);

    BooleanResponseDto deleteTrade(Integer integer, Integer id);

    Trade updateTrade(Integer userId, TradeRequestDto requestDto);
}
