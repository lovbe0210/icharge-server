package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.MenuDTO;
import com.lovbe.icharge.service.CommonService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/24 23:41
 * @Description: 公共接口
 */
@RestController
public class CommonController {
    @Resource
    private CommonService commonService;
    /**
     * description: 获取菜单码表
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @return ResponseBean<LoginUser>
     */
    @PostMapping("/menu")
    public ResponseBean<MenuDTO> getMenuCode() {
        return ResponseBean.ok(commonService.getMenuList());
    }

    /**
     * description: 根据uri判断为1文章还是2专栏
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @return ResponseBean<LoginUser>
     */
    @GetMapping("/router/{dynamicId}")
    public ResponseBean<Integer> getRouterType(@PathVariable("dynamicId") String dynamicId) {
        return ResponseBean.ok(commonService.getRouterDirection(dynamicId));
    }
}
