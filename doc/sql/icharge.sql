/*
 Navicat Premium Data Transfer

 Source Server         : v6
 Source Server Type    : MySQL
 Source Server Version : 80400
 Source Host           : 45.12.52.97:4417
 Source Schema         : icharge

 Target Server Type    : MySQL
 Target Server Version : 80400
 File Encoding         : 65001

 Date: 01/09/2024 02:32:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for c_article
-- ----------------------------
DROP TABLE IF EXISTS `c_article`;
CREATE TABLE `c_article`  (
  `uid` int NOT NULL,
  `title` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章标题',
  `tmp_content_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最新内容版本id',
  `published_content_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '已发布内容版本id',
  `user_id` int NOT NULL COMMENT '所属用户id',
  `column_id` int NULL DEFAULT NULL COMMENT '所属专栏',
  `summary` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文章摘要',
  `cover_url` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面地址',
  `tags` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文章标签json字符串',
  `is_public` tinyint UNSIGNED NOT NULL COMMENT '是否公开访问 0否1是',
  `publish_status` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '发布状态 0未发布 1审核中 2审核失败 3已发布',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A' COMMENT '状态：A启用D删除',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '写作模块-文档信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for c_column
-- ----------------------------
DROP TABLE IF EXISTS `c_column`;
CREATE TABLE `c_column`  (
  `uid` int UNSIGNED NOT NULL,
  `title` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '专栏标题',
  `user_id` int NOT NULL COMMENT '所属用户id',
  `summary` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专栏简介',
  `cover_url` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面地址',
  `dir_content_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目录json数据id',
  `is_public` tinyint UNSIGNED NOT NULL COMMENT '是否公开访问 0否1是',
  `enable_comment` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否开启评论功能 0否1是',
  `auto_publish` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否自动发布 0否1是 需要在公开访问时才能发布',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A' COMMENT '状态：A启用D删除',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '写作模块-专栏信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for c_essay
-- ----------------------------
DROP TABLE IF EXISTS `c_essay`;
CREATE TABLE `c_essay`  (
  `uid` int UNSIGNED NOT NULL,
  `title` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '随笔标题',
  `user_id` int NOT NULL COMMENT '所属用户id',
  `content_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容id',
  `preview_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预览内容，用于显示',
  `publish_status` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '发布状态 1审核中 2审核失败 3发布成功',
  `preview_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '预览图片，数组字符串,最多三张',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A' COMMENT '状态：A启用D删除',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '写作模块-随笔信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for c_interaction_statistic
-- ----------------------------
DROP TABLE IF EXISTS `c_interaction_statistic`;
CREATE TABLE `c_interaction_statistic`  (
  `uid` int NOT NULL COMMENT '关联文章、专栏、随笔的uid',
  `type` tinyint NOT NULL COMMENT '类型 1文章 2专栏 3随笔',
  `like_count` int UNSIGNED NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int UNSIGNED NULL DEFAULT 0 COMMENT '评论数',
  `collect_count` int UNSIGNED NULL DEFAULT 0 COMMENT '收藏数',
  `view_count` int UNSIGNED NULL DEFAULT 0 COMMENT '阅读数',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '写作模块-交互统计(点赞、评论、收藏、浏览、举报等)' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for p_account
-- ----------------------------
DROP TABLE IF EXISTS `p_account`;
CREATE TABLE `p_account`  (
  `uid` bigint UNSIGNED NOT NULL COMMENT '账号id',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号码',
  `email` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱地址',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `qq_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'qq授权id',
  `gitee_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'gitee授权id',
  `github_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'github授权id',
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
-- Table structure for p_relationship
-- ----------------------------
DROP TABLE IF EXISTS `p_relationship`;
CREATE TABLE `p_relationship`  (
  `uid` int NOT NULL COMMENT 'MD5(userId + userId) ',
  `user_id_master` int NOT NULL COMMENT '用户-主',
  `user_id_slave` int NOT NULL COMMENT '用户-从',
  `master_watch_slave` tinyint UNSIGNED NOT NULL COMMENT '主关注从 0否1是',
  `slave_watch_master` tinyint NOT NULL COMMENT '从关注主 0否1是',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户服务-人际关系表' ROW_FORMAT = DYNAMIC;

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
  `industry` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '行业',
  `content_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主页自定义内容id',
  `growth_value` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '成长值',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A' COMMENT '账号状态：A正常使用 S已封禁 D已删除',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户模块-用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for s_comment
-- ----------------------------
DROP TABLE IF EXISTS `s_comment`;
CREATE TABLE `s_comment`  (
  `uid` int NOT NULL COMMENT '评论id',
  `parent_id` int NULL DEFAULT NULL COMMENT '父级评论回复id',
  `user_id` int NOT NULL COMMENT '用户id',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `content_img` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片附件',
  `like_count` int UNSIGNED NULL DEFAULT 0 COMMENT '点赞数',
  `reply_user` int UNSIGNED NULL DEFAULT NULL COMMENT '楼中楼回复：用户id',
  `submit_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评论者ip地址',
  `submit_city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评论者所在城市',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'A正常 D删除 S审核不通过',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社交服务-评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for s_music_playlist
-- ----------------------------
DROP TABLE IF EXISTS `s_music_playlist`;
CREATE TABLE `s_music_playlist`  (
  `uid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'MD5(用户id+音乐id)',
  `music_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '音乐id',
  `user_id` int NOT NULL COMMENT '用户id',
  `music_name` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '音乐名称',
  `author` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '歌手姓名',
  `platform_code` int NULL DEFAULT NULL COMMENT '所在平台code',
  `is_like` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '是否喜欢 0否1是',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '个性化服务-音乐播放列表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for s_personalize_config
-- ----------------------------
DROP TABLE IF EXISTS `s_personalize_config`;
CREATE TABLE `s_personalize_config`  (
  `uid` int NOT NULL COMMENT '人员id',
  `article_default_public` tinyint UNSIGNED NULL DEFAULT 1 COMMENT '文章默认公开 0否1是',
  `column_default_public` tinyint NULL DEFAULT NULL COMMENT '专栏默认公开 0否1是',
  `enable_comment` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '是否开启评论功能 0否1是',
  `auto_publish` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '自动发布/更新至阅读页 0否1是',
  `doc_style_default_font` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '默认字体大小',
  `doc_style_segment_space` tinyint NULL DEFAULT NULL COMMENT '段落间隔 1宽松 0常规 ',
  `doc_style_page_size` tinyint NULL DEFAULT NULL COMMENT '页面布局 1标宽模式 2超宽模式',
  `doc_theme_sync` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '主题同步 0否1是',
  `theme_color` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主题色',
  `background` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '背景色',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'A',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '个性化服务-全局配置' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
