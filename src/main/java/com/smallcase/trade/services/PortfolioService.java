package com.smallcase.trade.services;

import com.smallcase.trade.entities.dao.Portfolio;

import java.util.List;

public interface PortfolioService {

    List<Portfolio> getAllPortfolioForUser(Integer userId);

    Portfolio getPortfolio(Integer id, Integer userId);

    Double getReturnOnPortfolioForUser(Integer id, Integer userId);

    Portfolio createPortfolio(Integer userId);
}
