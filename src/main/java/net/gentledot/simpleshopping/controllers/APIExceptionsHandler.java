package net.gentledot.simpleshopping.controllers;

import net.gentledot.simpleshopping.error.GoodsNotFoundException;
import net.gentledot.simpleshopping.error.MemberNotFoundException;
import net.gentledot.simpleshopping.error.PurchaseNotFoundException;
import net.gentledot.simpleshopping.error.SimpleNotFoundException;
import net.gentledot.simpleshopping.models.response.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class APIExceptionsHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler({
            IllegalStateException.class, IllegalArgumentException.class,
            TypeMismatchException.class, HttpMessageNotReadableException.class,
    })
    public ResponseEntity<ApiResult> handleBadRequestException(Exception e) {
        String message = e.getMessage();
        log.debug("올바르지 않은 요청으로 예외가 발생하였습니다. : {}", message, e);
        return newResponse(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SimpleNotFoundException.class)
    public ResponseEntity<ApiResult> handleNotFoundException(Exception e) {
        String message = e.getMessage();

        if (e instanceof GoodsNotFoundException
                || e instanceof MemberNotFoundException
                || e instanceof PurchaseNotFoundException) {
            log.debug("요청 대상을 찾을 수 없습니다. : {}", message, e);
            return newResponse(message, HttpStatus.NOT_FOUND);
        }

        log.warn("요청 대상을 찾을 수 없음. 예상치 못한 예외가 발생하였습니다. : {}", message, e);
        return newResponse(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<?> handleException(Exception e) {
        log.error("다루지 못한 예외가 발생하였습니다. : {}", e.getMessage(), e);
        return newResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiResult> newResponse(String message, HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(ApiResult.error(message, status), headers, status);
    }
}
