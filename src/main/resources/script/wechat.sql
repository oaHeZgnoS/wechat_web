/*
Navicat MySQL Data Transfer

Source Server         : 42.22
Source Server Version : 50724
Source Host           : 10.12.42.22:3306
Source Database       : wechat

Target Server Type    : MYSQL
Target Server Version : 50724
File Encoding         : 65001

Date: 2020-03-11 23:11:40
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` varchar(255) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `account` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('66666666', '甄姬', '15829059038', '123456');
INSERT INTO `user` VALUES ('88888888', '王昭君', '15829059039', '123456');
INSERT INTO `user` VALUES ('99999999', '刘备', '15829059040', '123456');
