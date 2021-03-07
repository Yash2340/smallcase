package com.smallcase.trade.services.impl;

import com.smallcase.trade.entities.Data;
import com.smallcase.trade.entities.dao.Portfolio;
import com.smallcase.trade.entities.dto.TickerAggregateDto;
import com.smallcase.trade.exceptions.ValidationException;
import com.smallcase.trade.services.PortfolioService;
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
public class PortfolioServiceImpl implements PortfolioService {

    @Autowired
    private ValidationUtil validationUtil;

    @Override
    public List<Portfolio> getAllPortfolioForUser(Integer userId) {
        validationUtil.validateUserId(userId);
        List<Integer> portfolioIds = Data.USER_PORTFOLIO_MAP.getOrDefault(userId,new ArrayList<>());
        return portfolioIds.stream().map(id -> Data.PORTFOLIO_MAP.get(id)).collect(Collectors.toList());
    }

    @Override
    public Portfolio getPortfolio(Integer id, Integer userId) {
        validationUtil.validateUserId(userId);
        Portfolio portfolio =  Data.PORTFOLIO_MAP.getOrDefault(id,null);
        if(Objects.nonNull(portfolio)) {
            return portfolio;
        } else {
            throw new ValidationException("Portfolio not found");
        }
    }

    @Override
    public Double getReturnOnPortfolioForUser(Integer id, Integer userId) {
        validationUtil.validateUserId(userId);
        Portfolio portfolio =  Data.PORTFOLIO_MAP.getOrDefault(id,null);
        if(Objects.nonNull(portfolio)) {
            double returns = 0.0;
            for (TickerAggregateDto dto : portfolio.getTickerAggregateList()) {
                returns = returns + (Data.DEFAULT_CURRENT_PRICE-dto.getAvgBuyPrice())*dto.getShare();
            }
            return returns;
        } else {
            throw new ValidationException("Portfolio not found");
        }
    }

    @Override
    public Portfolio createPortfolio(Integer userId) {
        validationUtil.validateUserId(userId);
        Portfolio portfolio = Portfolio.builder()
                .id(Data.PORTFOLIO_MAP.size()+1)
                .tickerAggregateList(new ArrayList<>())
                .build();
        Data.PORTFOLIO_MAP.put(portfolio.getId(),portfolio);
        List<Integer> ids = Data.USER_PORTFOLIO_MAP.getOrDefault(userId,new ArrayList<>());
        ids.add(portfolio.getId());
        Data.USER_PORTFOLIO_MAP.put(userId,ids);
        return portfolio;
    }
}
