package com.smallcase.trade.entities.enums;

public enum TradeType {
    BUY("BUY"),
    SELL("SELL");

    private final String tradeType;

    TradeType(final String tradeType) {
        this.tradeType = tradeType;
    }

    public String getTradeType() {
        return tradeType;
    }
}
