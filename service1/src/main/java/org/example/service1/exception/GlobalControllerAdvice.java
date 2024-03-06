package org.example.service1.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.service1.result.CommonResult;
import org.example.service1.result.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(BaseException.class)
    public Object handleBaseException(BaseException ex) {
        log.error("业务错误", ex);
        final ResultCode resultCode = ex.getResultCode();
        final Object message = ex.getMsg();
        return ResponseEntity.status(HttpStatus.valueOf(resultCode.getStatus())).body(CommonResult.error(resultCode, message));
    }

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception ex) {
        log.error("系统错误", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CommonResult.error(ResultCode.ERROR));
    }

}
