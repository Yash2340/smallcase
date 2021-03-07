package com.smallcase.trade.utils;

import com.smallcase.trade.entities.Data;
import com.smallcase.trade.entities.dto.TradeRequestDto;
import com.smallcase.trade.entities.enums.TradeType;
import com.smallcase.trade.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class ValidationUtil {

    public void validateTradeRequest(TradeRequestDto requestDto) {
        if (Objects.isNull(requestDto.getTradeType()) || !Data.TRADE_TYPE.contains(requestDto.getTradeType())) {
            throw new ValidationException("Invalid Trade type");
        }
        if (requestDto.getTradeType().equalsIgnoreCase(TradeType.BUY.getTradeType()) &&
                (Objects.isNull(requestDto.getPrice()) || requestDto.getPrice()<=0)) {
            throw new ValidationException("Invalid price");
        }
        if (Objects.isNull(requestDto.getTickerSymbol()) || requestDto.getTickerSymbol().isEmpty()) {
            throw new ValidationException("Invalid ticker symbol");
        }
        if (Objects.isNull(requestDto.getShare()) || requestDto.getShare()<=0) {
            throw new ValidationException("Invalid share value");
        }
    }

    public void validateUserId(Integer userId) {
        if (!Data.USER_MAP.containsKey(userId)) {
            throw new ValidationException("User not found");
        }
    }
}
