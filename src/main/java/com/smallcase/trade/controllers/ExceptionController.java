package com.smallcase.trade.controllers;

import com.smallcase.trade.entities.dto.ResponseDTO;
import com.smallcase.trade.exceptions.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseDTO handleTaskStateTransitionExceptions(
            final HttpServletRequest req,
            final ValidationException ex) {
        return ResponseDTO.of(500,
                ex.getMessage(), null);
    }
}
