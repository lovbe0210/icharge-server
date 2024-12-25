package com.lovbe.icharge.entity.vo;

import com.lovbe.icharge.entity.dto.BrowseHistoryDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/12/25 23:59
 * @Description: MS
 */
@Data
@Accessors(chain = true)
public class BrowseHistoryVo {
    /**
     * 历史记录日期
     */
    private Date historyDate;
    /**
     * 当日历史记录list
     */
    private List<BrowseHistoryDTO> list;
}
