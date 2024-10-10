package com.lovbe.icharge.common.config;

import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @description: 服务启动成功之后进行以下工作：...
 * @author: Lvhl
 * @date: 2021/10/12 20:40
 */
@Component
@Slf4j
public class ContextRefreshedListener implements ApplicationRunner {
    @Value("${global.param.snowflakeId}")
    private int snowflakeId;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 创建 IdGeneratorOptions 对象，可在构造函数中输入 WorkerId：
        IdGeneratorOptions options = new IdGeneratorOptions((short) snowflakeId);
        // options.WorkerIdBitLength = 10; // 默认值6，限定 WorkerId 最大值为2^6-1，即默认最多支持64个节点。
        // options.SeqBitLength = 6; // 默认值6，限制每毫秒生成的ID个数。若生成速度超过5万个/秒，建议加大 SeqBitLength 到 10。
         options.BaseTime = 1723568222473L; // 如果要兼容老系统的雪花算法，此处应设置为老系统的BaseTime。

        // 保存参数（务必调用，否则参数设置不生效）：
        YitIdHelper.setIdGenerator(options);
        // 以上过程只需全局一次，且应在生成ID之前完成。
    }
}
