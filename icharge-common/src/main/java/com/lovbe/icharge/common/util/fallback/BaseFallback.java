package com.lovbe.icharge.common.util.fallback;


import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.model.base.ResponseBean;

public class BaseFallback<T> {
    public ResponseBean<T> getFallBack() {
        return new ResponseBean<T>(ServiceErrorCodes.FEIGN_FAILED_REQUEST);
    }
}
