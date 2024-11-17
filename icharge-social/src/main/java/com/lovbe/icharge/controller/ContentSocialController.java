package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.model.base.ResponseBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/7 8:54
 * @Description: 内容相关互动 社交 互动
 */
@Slf4j
@RestController
public class ContentSocialController {

    @PostMapping("/upload")
    public ResponseBean upload() {
        return new ResponseBean();
    }
}
