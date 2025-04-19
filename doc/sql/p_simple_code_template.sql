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

 Date: 20/04/2025 00:27:21
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
-- Records of p_simple_code_template
-- ----------------------------
INSERT INTO `p_simple_code_template` VALUES (1, 1, 'mobile-login', '手机登录验证码', '验证码：**code**，**minute**分钟内容有效，您正在进行登录操作，请勿泄露。', '[\"code\", \"minute\"]', 'xxxxxxxxx', 'guoyangyun', '修改为阿里云云市场短信API相关参数', 'A', '2024-09-14 10:37:53', '2025-03-26 21:57:02');
INSERT INTO `p_simple_code_template` VALUES (2, 1, 'reset-password-by-mobile', '测试验证码短信', '验证码：**code**，**minute**分钟内有效，您正在进行密码重置操作，请妥善保管账户信息。', '[\"code\", \"minute\"]', 'xxxxxxxxx', 'guoyangyun', '修改为阿里云云市场短信API相关参数', 'A', '2024-09-29 00:04:01', '2025-03-26 21:57:06');
INSERT INTO `p_simple_code_template` VALUES (3, 2, 'email-login', '邮箱登录验证码', '验证码：{code}，{minute}分钟内有效，您正在进行登录操作，请勿泄露。', '[\"code\", \"minute\"]', '4383920', '163email', '修改为阿里云云市场短信API相关参数', 'A', '2024-09-14 10:37:53', '2025-03-26 21:57:37');
INSERT INTO `p_simple_code_template` VALUES (4, 2, 'reset-password-by-email', '测试验证码短信', '验证码：{code}，{minute}分钟内有效，您正在进行密码重置操作，请妥善保管账户信息。', '[\"code\", \"minute\"]', '4383921', '163email', '修改为阿里云云市场短信API相关参数', 'A', '2024-09-29 00:04:01', '2025-03-26 21:57:37');
INSERT INTO `p_simple_code_template` VALUES (5, 2, 'bind-mobile', '绑定手机号', '验证码：{code}，{minute}分钟内有效，您正在绑定新手机号，请勿泄露。', '[\"code\", \"minute\"]', 'xxxxxxxxx', 'guoyangyun', '修改为阿里云云市场短信API相关参数', 'A', '2025-01-30 16:58:59', '2025-03-28 14:18:52');
INSERT INTO `p_simple_code_template` VALUES (6, 2, 'update-password-by-mobile', '设置密码', '验证码：{code}，{minute}分钟内有效，您正在设置账号登录密码，请勿泄露。', '[\"code\", \"minute\"]', 'xxxxxxxxx', 'guoyangyun', '修改为阿里云云市场短信API相关参数', 'A', '2025-01-31 00:48:48', '2025-03-28 14:19:34');
INSERT INTO `p_simple_code_template` VALUES (18, 2, 'update-password-by-email', '设置密码', '验证码：{code}，{minute}分钟内有效，您正在设置账号登录密码，请勿泄露', '[\"code\", \"minute\"]', '439899', '163email', '修改为阿里云云市场短信API相关参数', 'A', '2025-01-31 00:48:48', '2025-03-26 22:24:41');
INSERT INTO `p_simple_code_template` VALUES (19, 1, 'verify-mobile', '验证新手机号', '验证码：\"\"code\"\"，\"\"minute\"\"分钟内有效，您正在绑定新手机号，请勿泄露。', '[\"code\", \"minute\"]', 'xxxxxxxxx', 'guoyangyun', 'sd', 'A', '2025-01-31 01:21:00', '2025-03-26 22:22:23');
INSERT INTO `p_simple_code_template` VALUES (20, 1, 'update-mobile-by-mobile', '验证手机修改手机号码', '验证码：**code**，**minute**分钟内有效，您正在修改账号绑定的手机号码，请勿泄露。', '[\"code\", \"minute\"]', 'xxxxxxxxx', 'guoyangyun', 'sd', 'A', '2025-02-02 14:54:54', '2025-03-26 22:24:33');
INSERT INTO `p_simple_code_template` VALUES (21, 2, 'update-mobile-by-email', '验证邮箱修改手机号码', '验证码：{code}，{minute}分钟内有效，您正在修改账号绑定的手机号码，请勿泄露。', '[\"code\", \"minute\"]', '232323', '163email', 'sd', 'A', '2025-02-03 10:43:47', '2025-03-26 22:28:05');
INSERT INTO `p_simple_code_template` VALUES (22, 2, 'update-email-by-email', '验证邮箱修改邮箱号码', '验证码：{code}，{minute}分钟内有效，您正在修改账号绑定的邮箱号，请勿泄露。', '[\"code\", \"minute\"]', '2323', '163email', 'sd', 'A', '2025-02-03 11:03:02', '2025-03-26 22:28:30');
INSERT INTO `p_simple_code_template` VALUES (23, 1, 'update-email-by-mobile', '验证手机修改邮箱号码', '验证码：**code**，**minute**分钟内有效，您正在修改账号绑定的邮箱号码，请勿泄露。', '[\"code\", \"minute\"]', 'xxxxxxxxx', 'guoyangyun', 'sd', 'A', '2025-02-03 11:03:02', '2025-03-26 22:24:35');
INSERT INTO `p_simple_code_template` VALUES (24, 2, 'verify-email', '验证新邮箱号', '验证码：{code}，{minute}分钟内有效，您正在绑定新邮箱号，请勿泄露。', '[\"code\", \"minute\"]', '2323', '163email', 'sd', 'A', '2025-02-03 11:03:02', '2025-03-26 22:27:24');
INSERT INTO `p_simple_code_template` VALUES (25, 2, 'bind-email', '绑定邮箱号', '验证码：{code}，{minute}分钟内有效，您正在绑定新邮箱号，请勿泄露。', '[\"code\", \"minute\"]', '4383922', '163email', '修改为阿里云云市场短信API相关参数', 'A', '2025-01-30 16:58:59', '2025-03-26 22:27:19');

SET FOREIGN_KEY_CHECKS = 1;
