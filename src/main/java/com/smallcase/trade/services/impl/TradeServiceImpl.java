package com.smallcase.trade.services.impl;

import com.smallcase.trade.entities.Data;
import com.smallcase.trade.entities.dao.Portfolio;
import com.smallcase.trade.entities.dao.Trade;
import com.smallcase.trade.entities.dto.BooleanResponseDto;
import com.smallcase.trade.entities.dto.TickerAggregateDto;
import com.smallcase.trade.entities.dto.TradeRequestDto;
import com.smallcase.trade.entities.enums.TradeType;
import com.smallcase.trade.exceptions.ValidationException;
import com.smallcase.trade.services.TradeService;
import com.smallcase.trade.utils.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TradeServiceImpl implements TradeService {

    @Autowired
    private ValidationUtil validationUtil;

    @Override
    public List<Trade> getTradesByUser(Integer userId) {
        validationUtil.validateUserId(userId);
        List<Integer> portfolioIds = Data.USER_PORTFOLIO_MAP.getOrDefault(userId, new ArrayList<>());
        List<Integer> tradeIds = new ArrayList<>();
        portfolioIds.forEach(id -> {tradeIds.addAll(Data.PORTFOLIO_TRADE_MAP.getOrDefault(id,new ArrayList<>()));});
        return tradeIds.stream()
                .map(id -> Data.TRADE_MAP.get(id))
                .filter(Trade::getIsActive).collect(Collectors.toList());
    }

    @Override
    public List<Trade> getTradesByUserAndTicker(Integer userId, String ticker) {
        validationUtil.validateUserId(userId);
        List<Integer> portfolioIds = Data.USER_PORTFOLIO_MAP.getOrDefault(userId, new ArrayList<>());
        List<Integer> tradeIds = new ArrayList<>();
        portfolioIds.forEach(id -> {tradeIds.addAll(Data.PORTFOLIO_TRADE_MAP.getOrDefault(id,new ArrayList<>()));});
        return tradeIds.stream().map(id -> Data.TRADE_MAP.get(id))
                .filter(trade -> trade.getTickerSymbol().equalsIgnoreCase(ticker))
                .filter(Trade::getIsActive).collect(Collectors.toList());
    }

    @Override
    public Trade addTrade(Integer userId, TradeRequestDto requestDto, Integer portfolioId) {
        validationUtil.validateUserId(userId);
        if (!Data.PORTFOLIO_MAP.containsKey(portfolioId)) {
            throw new ValidationException("Portfolio not found");
        }
        validationUtil.validateTradeRequest(requestDto);
        Trade trade = Trade.builder()
                .id(Data.TRADE_MAP.size()+1)
                .price(requestDto.getPrice())
                .tradeType(TradeType.valueOf(requestDto.getTradeType()))
                .tickerSymbol(requestDto.getTickerSymbol())
                .share(requestDto.getShare())
                .isActive(true)
                .build();
        updatePortfolio(trade,portfolioId);
        Data.TRADE_MAP.put(trade.getId(),trade);
        return trade;
    }

    private void updatePortfolio(Trade trade, Integer portfolioId) {
        Portfolio portfolio = Data.PORTFOLIO_MAP.get(portfolioId);
        List<TickerAggregateDto> allTickerList = portfolio.getTickerAggregateList();
        List<TickerAggregateDto> dtoList = allTickerList
                .stream()
                .filter(t -> t.getTickerSymbol().equalsIgnoreCase(trade.getTickerSymbol()))
                .collect(Collectors.toList());
        if (dtoList.isEmpty()) {
            if (trade.getTradeType().equals(TradeType.SELL)) {
                throw new ValidationException("Cannot sell trade because not present in portfolio");
            }
            TickerAggregateDto dto = TickerAggregateDto.builder()
                    .tickerSymbol(trade.getTickerSymbol())
                    .share(trade.getShare())
                    .avgBuyPrice(trade.getPrice())
                    .build();
            allTickerList.add(dto);
            portfolio.setTickerAggregateList(allTickerList);
            Data.PORTFOLIO_MAP.put(portfolioId,portfolio);
        } else {
            TickerAggregateDto dto = dtoList.get(0);
            if (trade.getTradeType().equals(TradeType.SELL)) {
                if (dto.getShare()<trade.getShare()) {
                    throw new ValidationException("Trade share cannot be greater than the portfolio ticker share for selling");
                }
                int shareLeft = dto.getShare()-trade.getShare();
                dto.setShare(shareLeft);
            } else {
                int shares = dto.getShare() + trade.getShare();
                double wtAvg = (trade.getShare()*trade.getPrice() + dto.getAvgBuyPrice()*dto.getShare())/shares;
                dto.setShare(shares);
                dto.setAvgBuyPrice(wtAvg);
            }
            if (dto.getShare()<=0) {
                portfolio.getTickerAggregateList().remove(dto);
            }
        }
        List<Integer> tradeIds = Data.PORTFOLIO_TRADE_MAP.getOrDefault(portfolioId,new ArrayList<>());
        tradeIds.add(trade.getId());
        Data.PORTFOLIO_TRADE_MAP.put(portfolioId,tradeIds);
    }

    @Override
    public BooleanResponseDto deleteTrade(Integer id, Integer userId) {
        validationUtil.validateUserId(userId);
        Trade trade = Data.TRADE_MAP.getOrDefault(id,null);
        if (Objects.isNull(trade)) {
            throw new ValidationException("Trade not found");
        }
        trade.setIsActive(false);
        List<Integer> portfolioIds = Data.USER_PORTFOLIO_MAP.getOrDefault(userId, new ArrayList<>())
                .stream()
                .filter(p ->
                        Data.PORTFOLIO_TRADE_MAP.get(p)
                                .stream().anyMatch(t -> t.intValue() == id.intValue()))
                .collect(Collectors.toList());
        if (portfolioIds.isEmpty()) {
            throw new ValidationException("No portfolio contain this trade");
        }
        List<Integer> tradeIds = Data.PORTFOLIO_TRADE_MAP.get(portfolioIds.get(0))
                .stream()
                .filter(t -> t.intValue()!=id.intValue())
                .collect(Collectors.toList());
        Data.PORTFOLIO_TRADE_MAP.put(portfolioIds.get(0),tradeIds);
        revertPortfolio(trade, portfolioIds.get(0));
        return BooleanResponseDto.builder().isUpdated(true).build();
    }

    private void revertPortfolio(Trade tradeDeleted, Integer id) {
        Portfolio portfolio = Data.PORTFOLIO_MAP.get(id);
        TickerAggregateDto dto = portfolio.getTickerAggregateList().stream()
                .filter(t -> t.getTickerSymbol().equalsIgnoreCase(tradeDeleted.getTickerSymbol()))
                .collect(Collectors.toList()).get(0);
        List<Integer> tradeIds = Data.PORTFOLIO_TRADE_MAP.get(id);
        List<Trade> tradeList = tradeIds.stream()
                .map(i -> Data.TRADE_MAP.get(i))
                .filter(trade -> trade.getTradeType().equals(tradeDeleted.getTradeType()))
                .filter(Trade::getIsActive).collect(Collectors.toList());
        if (!tradeList.isEmpty()) {
            int shares = 0;
            double avg = 0;
            for (Trade trade : tradeList) {
                if (trade.getTradeType().equals(TradeType.SELL)) {
                    shares = shares - trade.getShare();
                } else {
                    avg = (avg * shares + trade.getPrice() * trade.getShare()) / (shares + trade.getShare());
                    shares = shares + trade.getShare();
                }
            }
            dto.setAvgBuyPrice(avg);
            dto.setShare(shares);
        } else {
            portfolio.getTickerAggregateList().remove(dto);
        }
    }

    @Override
    public Trade updateTrade(Integer userId, TradeRequestDto requestDto) {
        try {
            validationUtil.validateUserId(userId);
            validationUtil.validateTradeRequest(requestDto);
            Trade trade = Data.TRADE_MAP.getOrDefault(requestDto.getId(), null);
            if (Objects.nonNull(trade)) {
                trade.setTradeType(TradeType.valueOf(requestDto.getTradeType().toUpperCase()));
                trade.setPrice(requestDto.getPrice());
                trade.setTickerSymbol(requestDto.getTickerSymbol());
                trade.setShare(requestDto.getShare());
                trade.setIsActive(requestDto.getIsActive());
                Data.TRADE_MAP.put(requestDto.getId(), trade);
                List<Integer> portfolioIds = Data.USER_PORTFOLIO_MAP.getOrDefault(userId, new ArrayList<>())
                        .stream()
                        .filter(p ->
                                Data.PORTFOLIO_TRADE_MAP.get(p)
                                        .stream().anyMatch(t -> t.intValue() == requestDto.getId().intValue()))
                        .collect(Collectors.toList());
                revertPortfolio(trade, portfolioIds.get(0));
                return trade;
            } else {
                throw new ValidationException("Trade not found");
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ValidationException("Trade not found in any portfolio");
        }
    }
}
