package com.smallcase.trade.entities;

import com.smallcase.trade.entities.dao.Portfolio;
import com.smallcase.trade.entities.dao.Trade;
import com.smallcase.trade.entities.dao.User;
import com.smallcase.trade.entities.enums.TradeType;

import java.util.*;

public class Data {
    public static HashMap<Integer, User> USER_MAP = new HashMap<>();
    public static HashMap<Integer, Trade> TRADE_MAP = new HashMap<>();
    public static HashMap<Integer, Portfolio> PORTFOLIO_MAP = new HashMap<>();
    public static HashMap<Integer, List<Integer>> USER_PORTFOLIO_MAP = new HashMap<>();
    public static HashMap<Integer, List<Integer>> PORTFOLIO_TRADE_MAP = new HashMap<>();
    public static final Set<String> TRADE_TYPE = new HashSet<>(
            Arrays.asList(
                    TradeType.BUY.getTradeType(),
                    TradeType.SELL.getTradeType()
            ));
    public static final double DEFAULT_CURRENT_PRICE=100.0;
}
