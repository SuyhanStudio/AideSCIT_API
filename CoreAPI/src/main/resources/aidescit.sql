/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MariaDB
 Source Server Version : 50558
 Source Host           : localhost:3306
 Source Schema         : aidescit

 Target Server Type    : MariaDB
 Target Server Version : 50558
 File Encoding         : 65001

 Date: 16/07/2021 17:23:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for class_chart
-- ----------------------------
DROP TABLE IF EXISTS `class_chart`;
CREATE TABLE `class_chart`  (
  `f_id` smallint(6) NOT NULL,
  `s_id` smallint(6) NOT NULL,
  `c_id` tinyint(4) NOT NULL,
  `grade` int(10) NOT NULL,
  `c_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`f_id`, `s_id`, `c_id`, `grade`) USING BTREE,
  UNIQUE INDEX `class_chart`(`c_name`, `f_id`, `s_id`, `c_id`, `grade`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for class_schedule
-- ----------------------------
DROP TABLE IF EXISTS `class_schedule`;
CREATE TABLE `class_schedule`  (
  `t_id` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `t_faculty` smallint(6) NOT NULL,
  `t_specialty` smallint(6) NOT NULL,
  `t_class` tinyint(4) NOT NULL,
  `t_grade` smallint(6) NOT NULL,
  `t_school_year` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `t_semester` tinyint(4) NOT NULL,
  `t_content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `t_expired` int(10) UNSIGNED NOT NULL,
  PRIMARY KEY (`t_id`) USING BTREE,
  UNIQUE INDEX `class_schedule`(`t_id`, `t_faculty`, `t_specialty`, `t_class`, `t_grade`, `t_school_year`, `t_semester`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for faculty_chart
-- ----------------------------
DROP TABLE IF EXISTS `faculty_chart`;
CREATE TABLE `faculty_chart`  (
  `f_id` smallint(6) NOT NULL,
  `f_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`f_id`) USING BTREE,
  UNIQUE INDEX `faculty_chart`(`f_id`, `f_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for hitokoto
-- ----------------------------
DROP TABLE IF EXISTS `hitokoto`;
CREATE TABLE `hitokoto`  (
  `h_id` int(11) NOT NULL AUTO_INCREMENT,
  `h_index` int(255) NOT NULL DEFAULT 0,
  `h_content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `h_type` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `h_from` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `h_from_who` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `h_creator` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `h_creator_uid` int(11) NOT NULL DEFAULT 0,
  `h_reviewer` int(11) NOT NULL,
  `h_insert_at` int(11) NOT NULL,
  `h_length` int(11) NOT NULL,
  PRIMARY KEY (`h_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for news
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news`  (
  `n_id` int(11) NOT NULL,
  `n_type_id` int(11) NOT NULL,
  `n_title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `n_summary` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `n_images` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `n_create_time` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`n_id`, `n_type_id`) USING BTREE,
  UNIQUE INDEX `news`(`n_id`, `n_type_id`, `n_create_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for news_chart
-- ----------------------------
DROP TABLE IF EXISTS `news_chart`;
CREATE TABLE `news_chart`  (
  `n_type_id` int(11) NOT NULL,
  `n_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `n_out` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`n_type_id`) USING BTREE,
  UNIQUE INDEX `news_chart`(`n_type_id`, `n_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for news_headline
-- ----------------------------
DROP TABLE IF EXISTS `news_headline`;
CREATE TABLE `news_headline`  (
  `h_id` int(11) NOT NULL,
  `h_type_id` int(11) NOT NULL,
  `h_image` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `h_expired` int(11) NOT NULL,
  PRIMARY KEY (`h_id`, `h_type_id`) USING BTREE,
  UNIQUE INDEX `news_headline`(`h_id`, `h_type_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sign_keys
-- ----------------------------
DROP TABLE IF EXISTS `sign_keys`;
CREATE TABLE `sign_keys`  (
  `app_key` tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `app_secret` tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `platform` tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `mail` tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `build` int(10) NOT NULL,
  `available` tinyint(1) NULL DEFAULT 1
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for specialty_chart
-- ----------------------------
DROP TABLE IF EXISTS `specialty_chart`;
CREATE TABLE `specialty_chart`  (
  `s_id` smallint(6) NOT NULL,
  `s_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `f_id` smallint(6) NOT NULL,
  PRIMARY KEY (`s_id`, `f_id`) USING BTREE,
  INDEX `specialty_chart`(`s_name`, `s_id`, `f_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for student_achieve
-- ----------------------------
DROP TABLE IF EXISTS `student_achieve`;
CREATE TABLE `student_achieve`  (
  `u_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `a_school_year` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `a_semester` tinyint(4) NOT NULL,
  `a_content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `a_expired` int(10) NOT NULL,
  PRIMARY KEY (`u_id`, `a_school_year`, `a_semester`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `u_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `u_name` tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `u_identify` tinyint(2) NOT NULL DEFAULT 0,
  `u_level` tinyint(2) NOT NULL DEFAULT 0,
  `u_faculty` smallint(6) NOT NULL DEFAULT 0,
  `u_specialty` smallint(6) NOT NULL DEFAULT 0,
  `u_class` tinyint(4) NOT NULL DEFAULT 0,
  `u_grade` smallint(6) NOT NULL DEFAULT 0,
  `u_info_expired` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`u_id`) USING BTREE,
  UNIQUE INDEX `user_info`(`u_id`, `u_identify`, `u_faculty`, `u_specialty`, `u_class`, `u_grade`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for user_session
-- ----------------------------
DROP TABLE IF EXISTS `user_session`;
CREATE TABLE `user_session`  (
  `u_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `u_password` varchar(600) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `u_session` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `u_session_expired` int(11) NOT NULL,
  `u_token_effective` tinyint(1) NOT NULL,
  PRIMARY KEY (`u_id`) USING BTREE,
  UNIQUE INDEX `user_token`(`u_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
