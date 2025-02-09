package com.lovbe.icharge.common.handler;

import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.ResponseBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

/**
 * 全局异常处理
 *
 * @Author lovbe0210
 * @Version 1.0
 * @createDate 2024/8/13 21:23
 */
@Slf4j
@RestControllerAdvice
public class GlobeExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseBean handleBizException(ServiceException ex) {
        return ResponseBean.error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseBean handleillegaException(IllegalArgumentException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseBean.error(400, ex.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseBean handleException(NoHandlerFoundException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseBean.error(GlobalErrorCodes.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseBean handleException(NoResourceFoundException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseBean.error(GlobalErrorCodes.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseBean handleException(MethodArgumentNotValidException ex) {
        StringBuilder errInfo = new StringBuilder();
        BindingResult bindingResult = ex.getBindingResult();
        if (bindingResult != null && bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            for (int i = 0; i < errors.size(); i++) {
                errInfo.append(errors.get(i).getDefaultMessage());
                if (errors.size() > 1 && i != errors.size() - 1) {
                    errInfo.append(";");
                }
            }
        }
        log.error(errInfo.toString(), ex);
        if (errInfo.toString().contains("请先完成滑块验证")) {
            return ResponseBean.error(GlobalErrorCodes.SLIDER_VERIFY_FAILED, null);
        } else {
            return ResponseBean.error(400, errInfo.toString());
        }
    }

    @ExceptionHandler(Throwable.class)
    public ResponseBean handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseBean.error(GlobalErrorCodes.INTERNAL_SERVER_ERROR, null);
    }

}
