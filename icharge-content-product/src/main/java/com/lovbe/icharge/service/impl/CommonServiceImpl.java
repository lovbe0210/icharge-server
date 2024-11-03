package com.lovbe.icharge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lovbe.icharge.entity.dto.MenuDTO;
import com.lovbe.icharge.dao.CommonDao;
import com.lovbe.icharge.service.CommonService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/24 23:50
 * @Description: MS
 */
@Service
public class CommonServiceImpl implements CommonService {
    @Resource
    private CommonDao commonDao;

    @Override
    public List<MenuDTO> getMenuList() {
        return commonDao.selectList(new LambdaQueryWrapper<MenuDTO>()
                .eq(MenuDTO::getStatus, "A")
                .orderByAsc(MenuDTO::getType)
                .orderByAsc(MenuDTO::getSort));
    }
}
