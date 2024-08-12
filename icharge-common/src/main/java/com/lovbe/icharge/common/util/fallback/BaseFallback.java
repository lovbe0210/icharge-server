package com.lovbe.icharge.common.util.fallback;


import com.lovbe.icharge.common.exception.ServiceErrorCodeConstants;
import com.lovbe.icharge.common.model.base.ResponseBean;

public class BaseFallback<T> {
    public ResponseBean<T> getFallBack() {
        return new ResponseBean<T>(ServiceErrorCodeConstants.FEIGN_FAILED_REQUEST);
    }
}
