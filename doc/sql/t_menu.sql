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

 Date: 15/03/2025 00:29:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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

-- ----------------------------
-- Records of t_menu
-- ----------------------------
INSERT INTO `t_menu` VALUES (1, 'technet', '计算机与网络', '1', NULL, 1, 'A', '2024-10-24 22:23:32', '2025-01-11 23:17:30');
INSERT INTO `t_menu` VALUES (2, 'lang', '编程语言', '1', NULL, 2, 'A', '2024-10-24 22:23:32', '2025-01-11 23:22:47');
INSERT INTO `t_menu` VALUES (3, 'database', '数据库', '1', NULL, 3, 'A', '2024-10-24 22:25:58', '2024-10-24 22:35:25');
INSERT INTO `t_menu` VALUES (4, 'midware', '中间件', '1', NULL, 4, 'A', '2024-10-24 22:26:10', '2025-01-11 23:25:39');
INSERT INTO `t_menu` VALUES (5, 'algthm', '算法', '1', NULL, 5, 'A', '2024-10-24 22:26:37', '2025-01-11 23:27:44');
INSERT INTO `t_menu` VALUES (6, 'basics', '计算机基础', '2', 'technet', 1, 'A', '2024-10-24 22:36:04', '2025-01-12 15:38:10');
INSERT INTO `t_menu` VALUES (7, 'os', '操作系统', '2', 'technet', 2, 'A', '2024-10-24 22:36:04', '2025-01-12 15:38:46');
INSERT INTO `t_menu` VALUES (8, 'ns', '网络安全', '2', 'technet', 3, 'A', '2024-10-24 22:36:04', '2025-01-12 15:39:25');
INSERT INTO `t_menu` VALUES (9, 'vc', '虚拟化与云计算', '2', 'technet', 4, 'A', '2024-10-24 22:57:52', '2025-01-12 15:40:16');
INSERT INTO `t_menu` VALUES (10, 'ai', '人工智能', '2', 'technet', 5, 'A', '2024-10-24 23:00:03', '2025-01-12 15:36:06');
INSERT INTO `t_menu` VALUES (11, 'c', 'C/C++', '2', 'lang', 1, 'A', '2024-10-24 23:06:57', '2025-01-15 00:23:09');
INSERT INTO `t_menu` VALUES (12, 'java', 'Java', '2', 'lang', 2, 'A', '2024-10-24 23:07:42', '2025-01-12 15:36:12');
INSERT INTO `t_menu` VALUES (13, 'javascript', 'Javascript', '2', 'lang', 3, 'A', '2024-10-24 23:08:05', '2025-01-12 15:36:12');
INSERT INTO `t_menu` VALUES (14, 'python', 'Python', '2', 'lang', 4, 'A', '2024-10-24 23:08:25', '2025-01-12 15:36:12');
INSERT INTO `t_menu` VALUES (15, 'golang', 'Golang', '2', 'lang', 5, 'A', '2024-10-24 23:08:45', '2025-01-12 15:36:12');
INSERT INTO `t_menu` VALUES (16, 'mysql', 'Mysql', '2', 'database', 1, 'A', '2024-10-24 23:10:37', '2024-10-25 00:29:50');
INSERT INTO `t_menu` VALUES (17, 'oracle', 'Oracle', '2', 'database', 2, 'A', '2024-10-24 23:10:57', '2024-10-25 00:29:50');
INSERT INTO `t_menu` VALUES (18, 'postgresql', 'PostgreSQ', '2', 'database', 3, 'A', '2024-10-24 23:11:24', '2024-10-25 00:29:50');
INSERT INTO `t_menu` VALUES (19, 'mongodb', 'Mongodb', '2', 'database', 4, 'A', '2024-10-24 23:11:48', '2024-10-25 00:29:50');
INSERT INTO `t_menu` VALUES (20, 'sqlserver', 'SQLServer', '2', 'database', 5, 'A', '2024-10-24 23:12:07', '2024-10-25 00:29:50');
INSERT INTO `t_menu` VALUES (21, 'sqlite', 'SQLite', '2', 'database', 6, 'A', '2024-10-24 23:12:32', '2024-10-25 00:29:50');
INSERT INTO `t_menu` VALUES (22, 'elasticsearch', 'Elasticsearch', '2', 'database', 7, 'A', '2024-10-24 23:12:55', '2024-10-25 00:29:50');
INSERT INTO `t_menu` VALUES (23, 'nginx', 'Nginx', '2', 'midware', 1, 'A', '2024-10-24 23:13:24', '2025-01-12 15:36:17');
INSERT INTO `t_menu` VALUES (24, 'tomcat', 'Tomcat', '2', 'midware', 2, 'A', '2024-10-24 23:13:46', '2025-01-12 15:36:17');
INSERT INTO `t_menu` VALUES (25, 'minio', 'Minio', '2', 'midware', 3, 'A', '2024-10-24 23:14:16', '2025-01-12 15:36:17');
INSERT INTO `t_menu` VALUES (26, 'rabbitmq', 'RabbitMQ', '2', 'midware', 4, 'A', '2024-10-24 23:14:38', '2025-01-12 15:36:17');
INSERT INTO `t_menu` VALUES (27, 'kafka', 'Kafka', '2', 'midware', 5, 'A', '2024-10-24 23:15:00', '2025-01-12 15:36:17');
INSERT INTO `t_menu` VALUES (28, 'elk', 'ELK', '2', 'midware', 6, 'A', '2024-10-24 23:15:25', '2025-01-12 15:36:17');
INSERT INTO `t_menu` VALUES (29, 'alg0101', '算法基础', '2', 'algthm', 1, 'A', '2024-10-24 23:22:17', '2025-01-12 15:41:48');
INSERT INTO `t_menu` VALUES (30, 'ds', '数据结构', '2', 'algthm', 2, 'A', '2024-10-24 23:35:09', '2025-01-12 15:42:29');
INSERT INTO `t_menu` VALUES (31, 'algs', '常见算法', '2', 'algthm', 3, 'A', '2024-10-24 23:36:45', '2025-01-12 15:43:16');
INSERT INTO `t_menu` VALUES (32, 'leetcode', '力扣题解', '2', 'algthm', 4, 'A', '2024-10-24 23:38:12', '2025-01-12 15:36:24');

SET FOREIGN_KEY_CHECKS = 1;
