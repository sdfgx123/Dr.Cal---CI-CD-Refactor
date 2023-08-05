package com.fc.mini3server._core.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fc.mini3server._core.handler.exception.*;
import com.fc.mini3server._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(Exception400.class)
    public ResponseEntity<?> badRequest(Exception400 e){
        return new ResponseEntity<>(e.body(), e.status());
    }

    @ExceptionHandler(Exception401.class)
    public ResponseEntity<?> unAuthorized(Exception401 e){
        return new ResponseEntity<>(e.body(), e.status());
    }

    @ExceptionHandler(Exception403.class)
    public ResponseEntity<?> forbidden(Exception403 e){
        return new ResponseEntity<>(e.body(), e.status());
    }

    @ExceptionHandler(Exception404.class)
    public ResponseEntity<?> notFound(Exception404 e){
        return new ResponseEntity<>(e.body(), e.status());
    }

    @ExceptionHandler(Exception500.class)
    public ResponseEntity<?> serverError(Exception500 e){
        return new ResponseEntity<>(e.body(), e.status());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiUtils.ApiResult<?>> badCredentialsException(BadCredentialsException e) {
        return new ResponseEntity<>(ApiUtils.error("이메일 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ApiUtils.ApiResult<?>> invalidFormatException(InvalidFormatException e) {
        return new ResponseEntity<>(ApiUtils.error("요청 형식이 잘못됐습니다. 올바른 직급 또는 권한 이름을 입력했는지 확인하십시오.", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ApiUtils.ApiResult<?>> httpMessageConversionException(HttpMessageConversionException e) {
        return new ResponseEntity<>(ApiUtils.error("요청 형식이 잘못됐습니다. 올바른 직급 또는 권한 이름을 입력했는지 확인하십시오.", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiUtils.ApiResult<?>> constraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>(ApiUtils.error("중복된 이메일 입니다. 다른 이메일을 사용하십시오.", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiUtils.ApiResult<?>> dataIntegrityViolationException(DataIntegrityViolationException e) {
        return new ResponseEntity<>(ApiUtils.error("중복된 이메일 입니다. 다른 이메일을 사용하십시오.", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiUtils.ApiResult<?>> dataConversionsFailedException(MethodArgumentTypeMismatchException e){
        return new ResponseEntity<>(ApiUtils.error(Message.METHOD_ARGUMENT_TYPE_MISMATCH, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> unknownServerError(Exception e){
        ApiUtils.ApiResult<?> apiResult = ApiUtils.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(apiResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
