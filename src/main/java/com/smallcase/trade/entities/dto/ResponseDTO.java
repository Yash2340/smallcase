package com.smallcase.trade.entities.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ResponseDTO {
    private int status;
    private String message;
    private Object data;

    public ResponseDTO(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ResponseDTO(Object data) {
        this.status = HttpStatus.OK.value();
        this.message = HttpStatus.OK.getReasonPhrase();
        this.data = data;
    }

    public static <T> ResponseDTO of(int status, String message, Object data) {
        return ResponseDTO.builder().status(status).message(message).data(data).build();
    }

    public static <T> ResponseDTO ok(String message) {
        return ResponseDTO.builder().status(200).message(message).build();
    }

    public static <T> ResponseDTO ok(String message, T data) {
        return ResponseDTO.builder().status(200).message(message).data(data).build();
    }
}
