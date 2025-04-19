/*
 Navicat Premium Data Transfer

 Source Server         : 10.2.2.14-proxysql
 Source Server Type    : MySQL
 Source Server Version : 80037
 Source Host           : 10.2.2.14:4417
 Source Schema         : icharge

 Target Server Type    : MySQL
 Target Server Version : 80037
 File Encoding         : 65001

 Date: 20/04/2025 00:20:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for c_article
-- ----------------------------
DROP TABLE IF EXISTS `c_article`;
CREATE TABLE `c_article`  (
  `uid` bigint NOT NULL,
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章标题',
  `uri` char(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '展示路径',
  `latest_content_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最新内容版本id',
  `published_content_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '已发布内容版本id',
  `user_id` bigint NOT NULL COMMENT '所属用户id',
  `column_id` bigint NULL DEFAULT NULL COMMENT '所属专栏',
  `words_num` int NOT NULL DEFAULT 0 COMMENT '文章字数',
  `auto_summary` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否自动生成文档摘要 0否1是',
  `summary` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文章摘要',
  `cover_url` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面地址',
  `tags` json NULL COMMENT '文章标签json字符串',
  `first_category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '一级分类',
  `second_category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '二级分类',
  `body_font_size` tinyint NULL DEFAULT NULL COMMENT '正文字体大小',
  `is_public` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否公开访问 0否1是',
  `publish_status` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '发布状态 0未发布 1审核中 2审核失败 3已发布',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '首次发布时间',
  `sort` int UNSIGNED NULL DEFAULT NULL COMMENT '置顶排序，如果为空则取消置顶，数字越大排序越大',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A' COMMENT '状态：A启用D删除S违规封禁',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `URI_IDX`(`status` ASC, `uri` ASC) USING BTREE,
  INDEX `URI_USERID_STATUS_IDX`(`user_id` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '写作模块-文档信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for c_column
-- ----------------------------
DROP TABLE IF EXISTS `c_column`;
CREATE TABLE `c_column`  (
  `uid` bigint NOT NULL,
  `title` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '专栏标题',
  `uri` char(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '展示路径',
  `user_id` bigint NOT NULL COMMENT '所属用户id',
  `synopsis` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专栏简介',
  `cover_url` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面地址',
  `home_content_status` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '专栏首页内容审核状态 0未提交 2失败 3成功',
  `home_content_id` bigint NULL DEFAULT NULL COMMENT '专栏首页内容id',
  `dir_content_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目录json数据id',
  `is_public` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否公开访问 0否1是',
  `enable_comment` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否开启评论功能 0否1是',
  `auto_publish` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否自动发布 0否1是 需要在公开访问时才能发布',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A' COMMENT '状态：A启用D删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `URI_STATUS_IDX`(`status` ASC, `uri` ASC) USING BTREE,
  INDEX `USER_STATUS_IDX`(`user_id` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '写作模块-专栏信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for c_content
-- ----------------------------
DROP TABLE IF EXISTS `c_content`;
CREATE TABLE `c_content`  (
  `uid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'id',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'A',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '写作模块-文章内容或目录内容（非结构化数据）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for c_create_record
-- ----------------------------
DROP TABLE IF EXISTS `c_create_record`;
CREATE TABLE `c_create_record`  (
  `uid` bigint NOT NULL COMMENT '文章、专栏、随笔id',
  `target_type` tinyint NOT NULL COMMENT '创作类型1发布文章 2创建专栏 3发布随笔',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `USER_TIME_IDX`(`user_id` ASC, `update_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '写作模块-创作记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for c_essay
-- ----------------------------
DROP TABLE IF EXISTS `c_essay`;
CREATE TABLE `c_essay`  (
  `uid` bigint UNSIGNED NOT NULL,
  `title` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '随笔标题',
  `user_id` bigint NOT NULL COMMENT '所属用户id',
  `content_id` bigint NOT NULL COMMENT '内容id',
  `preview_content` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预览内容，用于显示',
  `preview_img` json NULL COMMENT '预览图片，数组字符串,最多三张',
  `words_num` int UNSIGNED NULL DEFAULT 0 COMMENT '统计字数',
  `is_public` tinyint UNSIGNED NULL DEFAULT 1 COMMENT '是否公开可见',
  `publish_status` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '发布状态 1审核中 2审核失败 3发布成功',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A' COMMENT '状态：A启用D删除',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `PUBLIC_PUBLISH_STATUS_TIME_IDX`(`is_public` DESC, `publish_status` DESC, `status` ASC, `create_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '写作模块-随笔信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for i_music_playlist
-- ----------------------------
DROP TABLE IF EXISTS `i_music_playlist`;
CREATE TABLE `i_music_playlist`  (
  `uid` bigint NOT NULL COMMENT 'MD5(用户id+音乐id)',
  `music_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '音乐id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `music_name` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '音乐名称',
  `author` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '歌手姓名',
  `music_cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '音乐封面',
  `duration` int NOT NULL COMMENT '歌曲时长统一处理为秒',
  `platform_code` int NULL DEFAULT NULL COMMENT '所在平台code',
  `is_like` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '是否喜欢 0否1是',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态 A正常启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '个性化模块-音乐播放列表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for i_personalize_config
-- ----------------------------
DROP TABLE IF EXISTS `i_personalize_config`;
CREATE TABLE `i_personalize_config`  (
  `uid` bigint NOT NULL COMMENT '人员id',
  `config_from` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '配置更新原则：配置项冲突时以本地或云端为主 1本地 0云端',
  `content_default_public` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '文章默认公开 0否1是',
  `enable_comment` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否开启评论功能 0否1是',
  `auto_publish` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '自动发布/更新至阅读页 0否1是',
  `doc_style_default_font` tinyint UNSIGNED NOT NULL COMMENT '默认字体大小',
  `doc_style_page_size` tinyint NOT NULL DEFAULT 0 COMMENT '页面布局 0标宽模式 1超宽模式',
  `doc_theme_sync` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '主题同步 0否1是',
  `custom_theme` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '自定义主题',
  `flag_content` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'flag内容',
  `music_play` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '音乐播放相关',
  `domain_hotmap` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '个人主页是否展示创作指数',
  `domain_column` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '个人主页是否展示公开专栏',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '个性化模块-全局配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for p_account
-- ----------------------------
DROP TABLE IF EXISTS `p_account`;
CREATE TABLE `p_account`  (
  `uid` bigint UNSIGNED NOT NULL COMMENT '账号id=userId',
  `mobile` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号码',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱地址',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `qq_open_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'qq授权id',
  `wechat_open_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信授权id',
  `github_open_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'github授权id',
  `google_open_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'google授权id',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '账号信息说明',
  `login_count` int NULL DEFAULT NULL COMMENT '登陆次数',
  `last_login_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后登录时间',
  `last_login_ip` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后登陆ip',
  `login_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登陆user-agent',
  `login_os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登陆的操作系统',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A' COMMENT '账号状态：A正常使用 S已封禁 D已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户模块-账号信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for p_browse_history
-- ----------------------------
DROP TABLE IF EXISTS `p_browse_history`;
CREATE TABLE `p_browse_history`  (
  `uid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'targetId+date',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `history_date` date NOT NULL COMMENT '历史记录日期',
  `target_id` bigint NOT NULL COMMENT '记录id',
  `target_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '记录类型 1文章 2专栏 3随笔',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `USER_TIME_IDX`(`target_type` ASC, `user_id` ASC, `update_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户模块-浏览历史' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for p_collect_item
-- ----------------------------
DROP TABLE IF EXISTS `p_collect_item`;
CREATE TABLE `p_collect_item`  (
  `uid` bigint NOT NULL COMMENT '收藏明细id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `target_id` bigint NOT NULL COMMENT '目标id（文章或专栏id）',
  `target_type` tinyint NOT NULL COMMENT '收藏类型 1文章 2专栏',
  `tags` json NULL COMMENT '收藏标签分类',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A' COMMENT 'A启用 D删除',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `TARGET_IDX`(`user_id` ASC, `target_id` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户模块-收藏夹明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for p_collect_tags
-- ----------------------------
DROP TABLE IF EXISTS `p_collect_tags`;
CREATE TABLE `p_collect_tags`  (
  `uid` bigint NOT NULL COMMENT '收藏分组id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `title` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态 A正常D删除',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户模块-收藏夹标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for p_creation_index
-- ----------------------------
DROP TABLE IF EXISTS `p_creation_index`;
CREATE TABLE `p_creation_index`  (
  `uid` bigint NOT NULL COMMENT '每个用户每天的创作指数',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `creation_score` tinyint NOT NULL COMMENT '创作指数不活跃-活跃 0-5级',
  `record_date` date NOT NULL COMMENT '统计日期',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'A',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户模块-创作指数统计' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for p_encorage_log
-- ----------------------------
DROP TABLE IF EXISTS `p_encorage_log`;
CREATE TABLE `p_encorage_log`  (
  `uid` bigint NOT NULL,
  `user_id` bigint NOT NULL COMMENT '所属人员id',
  `behavior_type` tinyint NOT NULL COMMENT '动作行为类型 1发布文章 2内容获得点赞 3文档获得精选 4内容获得评论 5新增粉丝',
  `target_id` bigint NOT NULL COMMENT '动作对象id，可以是文档id',
  `target_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '动作对象名称，这里只做记录',
  `encourage_score` tinyint NOT NULL COMMENT '激励分数',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `USER_TIME`(`user_id` ASC, `create_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户模块-激励获取记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for p_growth_stats
-- ----------------------------
DROP TABLE IF EXISTS `p_growth_stats`;
CREATE TABLE `p_growth_stats`  (
  `uid` bigint NOT NULL,
  `user_id` bigint NOT NULL COMMENT '用户id',
  `range_type` tinyint NOT NULL COMMENT '统计时间范围 1近一年 0历史',
  `creation_days` int NULL DEFAULT 0 COMMENT '创作天数',
  `creation_words` int NULL DEFAULT 0 COMMENT '创作字数',
  `update_contents` int NULL DEFAULT 0 COMMENT '内容更新',
  `harvest_likes` int NULL DEFAULT 0 COMMENT '收获点赞',
  `article_total` int NULL DEFAULT 0 COMMENT '文章总数',
  `column_total` int NULL DEFAULT 0 COMMENT '专栏总数',
  `essay_total` int NULL DEFAULT 0 COMMENT '随笔总数',
  `most_words_column_id` bigint NULL DEFAULT NULL COMMENT '字数最多的专栏id',
  `most_words_article_id` bigint NULL DEFAULT NULL COMMENT '字数最多的文章id',
  `public_articles` int NULL DEFAULT 0 COMMENT '公开文档数',
  `article_views` int NULL DEFAULT 0 COMMENT '阅读量',
  `article_features` int NULL DEFAULT 0 COMMENT '文章收录精选次数',
  `content_likes` int NULL DEFAULT 0 COMMENT '公开文章点赞量',
  `content_comments` int NULL DEFAULT 0 COMMENT '收获评论量',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户模块-成长统计' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for p_simple_code_template
-- ----------------------------
DROP TABLE IF EXISTS `p_simple_code_template`;
CREATE TABLE `p_simple_code_template`  (
  `uid` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `type` tinyint NOT NULL COMMENT '模板类型 1手机 2邮箱',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板编码',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板名称',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板内容',
  `params` json NOT NULL COMMENT '参数数组',
  `api_template_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '短信 API 的模板编号',
  `channel_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '短信渠道编码',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'A' COMMENT '开启状态：A正常使用 S已停用 D已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户模块-验证码模板' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for p_user
-- ----------------------------
DROP TABLE IF EXISTS `p_user`;
CREATE TABLE `p_user`  (
  `uid` bigint UNSIGNED NOT NULL COMMENT '用户信息id关联账号表id',
  `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名/昵称',
  `level` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '等级',
  `domain` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '个人主页路径',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像路径',
  `tags` json NULL COMMENT '个人标签',
  `introduction` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个人简介',
  `location` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '位置',
  `industry` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '行业',
  `content_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主页自定义内容id',
  `content_status` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '主页自定义内容审核状态 0未审核  2失败 3成功',
  `growth_value` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '成长值',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A' COMMENT '账号状态：A正常使用 S已封禁 D已删除',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `USER_DOMAIN_IDX`(`domain` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户模块-用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for p_vcode_log
-- ----------------------------
DROP TABLE IF EXISTS `p_vcode_log`;
CREATE TABLE `p_vcode_log`  (
  `uid` bigint NOT NULL,
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `scene_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '场景code 对应templateCode',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱地址',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号码',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发送标题',
  `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发送内容',
  `send_status` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '发送状态 0待发送 2发送失败 3发送成功',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A' COMMENT '数据状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户模块-验证码发送记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_chat_logs
-- ----------------------------
DROP TABLE IF EXISTS `s_chat_logs`;
CREATE TABLE `s_chat_logs`  (
  `uid` bigint NOT NULL COMMENT '服务端生成的聊天记录id',
  `client_msg_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '客户端生成的聊天记录id',
  `send_id` bigint NOT NULL COMMENT '发送者id 如果是系统提醒则为0',
  `recv_id` bigint NOT NULL COMMENT '接收者id',
  `recv_type` tinyint(1) NOT NULL COMMENT '接受者类型1私聊 2群聊',
  `sender_platform_id` int NOT NULL COMMENT '发送者平台 1web 2h5 3app',
  `content_type` int NOT NULL COMMENT '101 文字消息 102图片 103站内文章 104链接 111撤回消息',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `send_time` datetime NOT NULL COMMENT '发送时间',
  `send_user_delete` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '发送人删除 0否1是',
  `recv_user_delete` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '接收人删除 0否1是',
  `read_status` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '阅读状态 是否已读 0否1是',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A' COMMENT 'A正常 D删除 S撤回',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `recv_id`(`send_time` ASC, `recv_id` ASC) USING BTREE,
  INDEX `send_id`(`send_time` ASC, `send_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社交模块-聊天记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_comment
-- ----------------------------
DROP TABLE IF EXISTS `s_comment`;
CREATE TABLE `s_comment`  (
  `uid` bigint NOT NULL COMMENT '评论id',
  `target_id` bigint NOT NULL COMMENT '评论对象id',
  `target_type` tinyint NOT NULL COMMENT '评论对象类型 1文章2专栏3随笔4评论',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父级评论回复id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `content_img_url` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片地址',
  `reply_user_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '楼中楼回复：用户id',
  `submit_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评论者ip地址',
  `submit_city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评论者所在城市',
  `is_top` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否置顶 0否1是',
  `is_feature` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否精选 0否1是',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A' COMMENT 'A正常 D删除 S审核不通过',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `TARGET_IDX`(`target_id` ASC, `status` ASC, `create_time` ASC) USING BTREE,
  INDEX `PARENT_IDX`(`parent_id` ASC, `status` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社交服务-评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for s_conversation
-- ----------------------------
DROP TABLE IF EXISTS `s_conversation`;
CREATE TABLE `s_conversation`  (
  `uid` bigint NOT NULL COMMENT '会话id',
  `owner_user_id` bigint NOT NULL COMMENT '所属用户id',
  `target_user_id` bigint NOT NULL COMMENT '会话用户id',
  `is_one_way` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否单向会话会话 0否1是 对方主动回复或关注你前，最多发送1条消息',
  `is_not_disturb` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '是否开启免打扰 0否1是',
  `is_pinned` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '是否置顶 0否1是',
  `is_shield` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '是否屏蔽会话用户 0否1是',
  `unread_count` int NULL DEFAULT 0 COMMENT '未读数',
  `update_unread_count_time` datetime NULL DEFAULT NULL COMMENT '未读统计时间',
  `min_chat_log_seq` bigint NULL DEFAULT NULL COMMENT '聊天记录最小序列',
  `last_msg_id` bigint NULL DEFAULT NULL COMMENT '最后一条消息id',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `OWNER_STATUS_IDX`(`owner_user_id` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社交模块-聊天会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_interaction_statistic
-- ----------------------------
DROP TABLE IF EXISTS `s_interaction_statistic`;
CREATE TABLE `s_interaction_statistic`  (
  `uid` bigint UNSIGNED NOT NULL COMMENT '关联文章、专栏、随笔和评论的uid',
  `type` tinyint UNSIGNED NOT NULL COMMENT '类型 0用户 1文章 2专栏 3随笔 4评论',
  `like_count` int UNSIGNED NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int UNSIGNED NULL DEFAULT 0 COMMENT '评论数',
  `collect_count` int UNSIGNED NULL DEFAULT 0 COMMENT '收藏数',
  `view_count` int UNSIGNED NULL DEFAULT 0 COMMENT '阅读数',
  `follow_count` int UNSIGNED NULL DEFAULT 0 COMMENT '关注数',
  `fans_count` int UNSIGNED NULL DEFAULT 0 COMMENT '粉丝数',
  `is_feature` int UNSIGNED NULL DEFAULT 0 COMMENT '是否被收录精选 0否1是',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `TYPE_STATUS_IDX`(`type` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社交模块-交互统计(点赞、评论、收藏、浏览、举报等)' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for s_like_item
-- ----------------------------
DROP TABLE IF EXISTS `s_like_item`;
CREATE TABLE `s_like_item`  (
  `uid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'user_id+\'_\'+target_id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `target_id` bigint NOT NULL COMMENT '点赞对象id',
  `target_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '点赞类型 1文章 2专栏 3随笔 4评论',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `TARGET_IDX`(`user_id` ASC, `target_id` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社交模块-点赞明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_notice_config
-- ----------------------------
DROP TABLE IF EXISTS `s_notice_config`;
CREATE TABLE `s_notice_config`  (
  `uid` bigint NOT NULL COMMENT '人员id',
  `new_msg_dot` tinyint(1) NOT NULL DEFAULT 1 COMMENT '新消息红点提醒',
  `msg_count` tinyint(1) NOT NULL DEFAULT 1 COMMENT '新消息展示数量统计',
  `comment_msg_accept` tinyint(1) NOT NULL DEFAULT 1 COMMENT '评论回复提醒人员范围 1 所有人 0关注的人 -1不接受任何消息提醒',
  `like_msg_accept` tinyint(1) NOT NULL DEFAULT 1 COMMENT '点赞消息提薪人员范围 1 所有人 0关注的人 -1不接受任何消息提醒',
  `new_follower_msg` tinyint(1) NOT NULL DEFAULT 1 COMMENT '他人关注是否提醒 0否1是',
  `system_notice` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否开启系统通知 0否1是',
  `enable_chat_message` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否开启私聊消息 0否1是',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社交模块-消息通知设置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_notices
-- ----------------------------
DROP TABLE IF EXISTS `s_notices`;
CREATE TABLE `s_notices`  (
  `uid` bigint NOT NULL,
  `user_id` bigint NOT NULL COMMENT '用户id',
  `notice_type` tinyint NOT NULL COMMENT '通知类型 1评论 2回复 3点赞 4新增关注 5文章审核结果 6随笔审核结果 0系统消息',
  `target_id` bigint NOT NULL DEFAULT 0 COMMENT '点赞或评论对象id, 其他为0',
  `action_user_id` bigint NOT NULL COMMENT '动作人，如果是系统通知则为0',
  `comment_id` bigint NULL DEFAULT NULL COMMENT '评论id 当type=1和2时不为空',
  `reply_id` bigint NULL DEFAULT NULL COMMENT '回复id 当type=2时不为空',
  `notice_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核结果内容 当type=5或6时有此参数',
  `read_status` tinyint NOT NULL DEFAULT 0 COMMENT '阅读状态 0未读 1已读',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A' COMMENT '状态',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `TYPE_TUSERID_IDX`(`notice_type` ASC, `action_user_id` ASC) USING BTREE,
  INDEX `TYPE_USERID_IDX`(`user_id` ASC, `notice_type` ASC, `read_status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社交模块-通知明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_relationship
-- ----------------------------
DROP TABLE IF EXISTS `s_relationship`;
CREATE TABLE `s_relationship`  (
  `uid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键 大id+\'_\'+小id',
  `user_id_master` bigint UNSIGNED NOT NULL COMMENT '较大id作为master',
  `user_id_slave` bigint UNSIGNED NOT NULL COMMENT '较小id作为slave',
  `master_watch_slave` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT 'master是否关注slave 0否1是',
  `slave_watch_master` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT 'slave是否关注master 0否1是',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `MASTER_USER_IDX`(`user_id_master` ASC) USING BTREE,
  INDEX `SLAVE_USER_IDX`(`user_id_slave` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社交模块-人际关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_menu`;
CREATE TABLE `t_menu`  (
  `uid` bigint NOT NULL,
  `menu_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单code',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单类型 1一级菜单 2二级菜单',
  `parent_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父级菜单',
  `sort` tinyint NOT NULL COMMENT '排序字段',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统码表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
