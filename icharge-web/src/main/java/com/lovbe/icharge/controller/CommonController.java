package com.lovbe.icharge.controller;

import com.lovbe.icharge.model.base.ResponseBean;
import com.lovbe.icharge.common.service.CommonService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

//import javax.annotation.Resource;

@RestController
public class CommonController {
    @Resource
    CommonService commonService;

    /**
     * 简单文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/common/upload")
    public ResponseBean upload(@RequestParam("file") MultipartFile file) {
//        String publicUrl = commonService.upload(file);
        if (System.currentTimeMillis() % 2 == 0) {
            return ResponseBean.ok("https://sd839j9oj.hd-bkt.clouddn.com/web/theme/1789320408311279616.gif?e=1715442264&token=eNmxT0s01OOlBmM7gwSufviBOs0ndxW7JbPU-odP:wGsZo-ZNQ04ANr0GqafGn6wO-co=");
        } else {
            return ResponseBean.ok("https://sd839j9oj.hd-bkt.clouddn.com/web/theme/1789321857451700224.jpg?e=1715442609&token=eNmxT0s01OOlBmM7gwSufviBOs0ndxW7JbPU-odP:VKRGuXAU_PRtd42eqwm9oE78hNM=");
        }
//        return ResponseBean.ok(publicUrl);
    }
}
