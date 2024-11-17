<p align="center">
	<svg t="1731815215475" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5447" id="mx_n_1731815215476" width="200" height="200"><path d="M671.266133 41.1648L182.340267 285.525333c-57.992533 29.013333-70.621867 106.496-24.746667 152.405334l244.6336 244.394666c45.8752 45.841067 123.392 33.245867 152.3712-24.746666l244.394667-488.994134c40.891733-81.749333-45.909333-168.277333-127.658667-127.419733" fill="#FF5656" p-id="5448"></path><path d="M351.778133 982.084267l488.994134-244.394667c58.026667-29.013333 70.621867-106.496 24.746666-152.405333L620.885333 340.992c-45.8752-45.909333-123.357867-33.28-152.3712 24.746667l-244.394666 488.925866c-40.8576 81.8176 45.909333 168.311467 127.658666 127.453867" fill="#00B96B" p-id="5449" data-spm-anchor-id="a313x.manage_type_myprojects.0.i3.3be33a81626KS8" class=""></path></svg>
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">IT充电站</h1>
<h4 align="center">基于 Vue/iView UI 和 Spring Boot/Spring Cloud & Alibaba 前后端分离的分布式微服务架构</h4>


## 平台简介

it充电站，可以是你的私人博客，用来记录你的成长轨迹，也可以是一个知识社区，共同见证你我的成长，还可以是你生活的一角，记你所想，随笔一生，记录生活的点点滴滴！
在这里，没有CSDN那样烦人的广告，也没有掘金那样的包揽万象，但却有着语雀一样简约大方，有着博客园一样的高质量，同时还可以沉浸式满足你的专注写作，也可以自定义主题满足你的天马行空！

* 采用前后端分离的模式，微服务版本前端(https://gitee.com/lovbe0210/it-charge-station)。
* 后端采用Spring Boot、Spring Cloud & Alibaba。
* 注册中心、配置中心选型Nacos，权限认证使用Gateway + Redis。
* 流量控制框架选型Sentinel，分布式事务选型Seata。

## 系统模块

~~~
com.lovbe     
├── icharge-gateway             // 网关模块 [8080]
├── icharge-auth                // 认证中心 [9200]
├── icharge-common              // 通用模块
    └── pom.xml                 // 公共依赖管理
├── icharge-user                // 用户模块
├── icharge-content-product     // 内容生产模块
├── icharge-storage             // 文件存储模块
├── icharge-social              // 社交模块
├── pom.xml                     // 依赖管理
~~~

## 架构图

![技术结构](./技术架构图.png)
