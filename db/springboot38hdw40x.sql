/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50726 (5.7.26)
 Source Host           : localhost:3306
 Source Schema         : springboot38hdw40x

 Target Server Type    : MySQL
 Target Server Version : 50726 (5.7.26)
 File Encoding         : 65001

 Date: 12/12/2025 10:10:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for aboutus
-- ----------------------------
DROP TABLE IF EXISTS `aboutus`;
CREATE TABLE `aboutus`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `title` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标题',
  `subtitle` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '副标题',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '内容',
  `picture1` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '图片1',
  `picture2` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '图片2',
  `picture3` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '图片3',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '关于我们' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of aboutus
-- ----------------------------
INSERT INTO `aboutus` VALUES (1, '2023-02-28 18:57:46', '关于我们', 'ABOUT US', '不管你想要怎样的生活，你都要去努力争取，不多尝试一些事情怎么知道自己适合什么、不适合什么呢?\n你说你喜欢读书，让我给你列书单，你还问我哪里有那么多时间看书;你说自己梦想的职业是广告文案，问我如何成为一个文案，应该具备哪些素质;你说你计划晨跑，但总是因为学习、工作辛苦或者身体不舒服第二天起不了床;你说你一直梦想一个人去长途旅行，但是没钱，父母觉得危险。其实，我已经厌倦了你这样说说而已的把戏，我觉得就算我告诉你如何去做，你也不会照做，因为你根本什么都不做。', 'upload/aboutus_picture1.jpg', 'upload/aboutus_picture2.jpg', 'upload/aboutus_picture3.jpg');

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `userid` bigint(20) NOT NULL COMMENT '用户id',
  `address` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '地址',
  `name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '收货人',
  `phone` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '电话',
  `isdefault` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否默认地址[是/否]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1680527122071 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '地址' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` VALUES (1, '2023-02-28 18:57:46', 11, '宇宙银河系金星1号', '金某', '13823888881', '否');
INSERT INTO `address` VALUES (2, '2023-02-28 18:57:46', 12, '宇宙银河系木星1号', '木某', '13823888882', '是');
INSERT INTO `address` VALUES (3, '2023-02-28 18:57:46', 13, '宇宙银河系水星1号', '水某', '13823888883', '是');
INSERT INTO `address` VALUES (4, '2023-02-28 18:57:46', 14, '宇宙银河系火星1号', '火某', '13823888884', '是');
INSERT INTO `address` VALUES (5, '2023-02-28 18:57:46', 15, '宇宙银河系土星1号', '土某', '13823888885', '是');
INSERT INTO `address` VALUES (6, '2023-02-28 18:57:46', 16, '宇宙银河系月球1号', '月某', '13823888886', '是');
INSERT INTO `address` VALUES (7, '2023-02-28 18:57:46', 17, '宇宙银河系黑洞1号', '黑某', '13823888887', '是');
INSERT INTO `address` VALUES (8, '2023-02-28 18:57:46', 18, '宇宙银河系地球1号', '地某', '13823888888', '是');
INSERT INTO `address` VALUES (1680272975730, '2023-03-31 22:29:34', 11, '33', '用户账号1', '13689659652', '是');
INSERT INTO `address` VALUES (1680527122070, '2023-04-03 21:05:21', 1680527067622, '333', '111', '13696869654', '是');

-- ----------------------------
-- Table structure for buhuotixing
-- ----------------------------
DROP TABLE IF EXISTS `buhuotixing`;
CREATE TABLE `buhuotixing`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `fuzhuangbianhao` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服装编号',
  `fuzhuangmingcheng` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服装名称',
  `shangpinfenlei` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品分类',
  `gongyingshangmingcheng` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '供应商名称',
  `dangqiankucun` int(11) NULL DEFAULT NULL COMMENT '当前库存',
  `kucunyuzhi` int(11) NULL DEFAULT 10 COMMENT '库存阈值',
  `buhuoshuliang` int(11) NULL DEFAULT NULL COMMENT '建议补货数量',
  `tixingzhuangtai` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '待处理' COMMENT '提醒状态：待处理、已完成、已取消',
  `chuangjianren` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `beizhu` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `wanchengshijian` datetime NULL DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_fuzhuangbianhao`(`fuzhuangbianhao`) USING BTREE,
  INDEX `idx_tixingzhuangtai`(`tixingzhuangtai`) USING BTREE,
  INDEX `idx_addtime`(`addtime`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '补货提醒' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of buhuotixing
-- ----------------------------
INSERT INTO `buhuotixing` VALUES (1, '2025-12-10 16:22:44', '2365', '改良汉服', '汉服', '万达', 5, 10, 25, '待处理', '系统自动', '库存不足，需要及时补货', NULL);
INSERT INTO `buhuotixing` VALUES (2, '2025-12-10 16:22:44', '服装编号1', '服装名称1', '商品分类1', '供应商名称1', 3, 10, 27, '待处理', '系统自动', '库存严重不足', NULL);

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tablename` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'tejiashangpin' COMMENT '商品表名',
  `userid` bigint(20) NOT NULL COMMENT '用户id',
  `goodid` bigint(20) NOT NULL COMMENT '商品id',
  `goodname` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `picture` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '图片',
  `buynumber` int(11) NOT NULL COMMENT '购买数量',
  `price` float NULL DEFAULT NULL COMMENT '单价',
  `discountprice` float NULL DEFAULT NULL COMMENT '会员价',
  `goodtype` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1680185272851 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '购物车表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES (1680185272850, '2023-03-30 22:07:52', 'tejiashangpin', 11, 41, '物价名称1', 'upload/tejiashangpin_fengmian1.jpg', 1, 99.9, 0, NULL);

-- ----------------------------
-- Table structure for chanpinfenlei
-- ----------------------------
DROP TABLE IF EXISTS `chanpinfenlei`;
CREATE TABLE `chanpinfenlei`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `chanpinfenlei` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产品分类',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '产品分类' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chanpinfenlei
-- ----------------------------
INSERT INTO `chanpinfenlei` VALUES (21, '2023-02-28 18:57:46', '产品分类1');
INSERT INTO `chanpinfenlei` VALUES (22, '2023-02-28 18:57:46', '产品分类2');
INSERT INTO `chanpinfenlei` VALUES (23, '2023-02-28 18:57:46', '产品分类3');
INSERT INTO `chanpinfenlei` VALUES (24, '2023-02-28 18:57:46', '产品分类4');
INSERT INTO `chanpinfenlei` VALUES (25, '2023-02-28 18:57:46', '产品分类5');
INSERT INTO `chanpinfenlei` VALUES (26, '2023-02-28 18:57:46', '产品分类6');
INSERT INTO `chanpinfenlei` VALUES (27, '2023-02-28 18:57:46', '产品分类7');
INSERT INTO `chanpinfenlei` VALUES (28, '2023-02-28 18:57:46', '产品分类8');

-- ----------------------------
-- Table structure for chat
-- ----------------------------
DROP TABLE IF EXISTS `chat`;
CREATE TABLE `chat`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `userid` bigint(20) NOT NULL COMMENT '用户id',
  `adminid` bigint(20) NULL DEFAULT NULL COMMENT '管理员id',
  `ask` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '提问',
  `reply` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '回复',
  `isreply` int(11) NULL DEFAULT NULL COMMENT '是否回复',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1680527088063 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '在线咨询' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chat
-- ----------------------------
INSERT INTO `chat` VALUES (51, '2023-02-28 18:57:46', 1, 1, '提问1', '回复1', 1);
INSERT INTO `chat` VALUES (52, '2023-02-28 18:57:46', 2, 2, '提问2', '回复2', 2);
INSERT INTO `chat` VALUES (53, '2023-02-28 18:57:46', 3, 3, '提问3', '回复3', 3);
INSERT INTO `chat` VALUES (54, '2023-02-28 18:57:46', 4, 4, '提问4', '回复4', 4);
INSERT INTO `chat` VALUES (55, '2023-02-28 18:57:46', 5, 5, '提问5', '回复5', 5);
INSERT INTO `chat` VALUES (56, '2023-02-28 18:57:46', 6, 6, '提问6', '回复6', 6);
INSERT INTO `chat` VALUES (57, '2023-02-28 18:57:46', 7, 7, '提问7', '回复7', 7);
INSERT INTO `chat` VALUES (58, '2023-02-28 18:57:46', 8, 8, '提问8', '回复8', 8);
INSERT INTO `chat` VALUES (1680272959578, '2023-03-31 22:29:19', 11, NULL, '33', NULL, 0);
INSERT INTO `chat` VALUES (1680273059327, '2023-03-31 22:30:58', 11, NULL, '333', NULL, 0);
INSERT INTO `chat` VALUES (1680273067850, '2023-03-31 22:31:07', 11, 1, NULL, '55', NULL);
INSERT INTO `chat` VALUES (1680527088062, '2023-04-03 21:04:47', 1680527067622, NULL, '33', NULL, 1);

-- ----------------------------
-- Table structure for chukuxinxi
-- ----------------------------
DROP TABLE IF EXISTS `chukuxinxi`;
CREATE TABLE `chukuxinxi`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `fuzhuangbianhao` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服装编号',
  `fuzhuangmingcheng` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服装名称',
  `gongyingshangmingcheng` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '供应商名称',
  `shangpinfenlei` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品分类',
  `lianxidianhua` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `fuzhuangtupian` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '服装图片',
  `yanse` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '颜色',
  `chicun` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '尺寸',
  `fuzhuangkucun` int(11) NULL DEFAULT NULL COMMENT '出库数量',
  `zhanghao` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '出库账号',
  `xingming` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '出库人',
  `jiaohuoshijian` date NULL DEFAULT NULL COMMENT '交货时间',
  `kehumingcheng` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '客户名称',
  `payment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'UNPAID' COMMENT '付款状态：PAID-已付款, UNPAID-未付款',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '付款时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '出库信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chukuxinxi
-- ----------------------------
INSERT INTO `chukuxinxi` VALUES (1, '2025-01-02 14:58:49', '服装编号1', '服装名称1', '供应商名称1', '商品分类1', '联系电话1', 'upload/chukuxinxi_fuzhuangtupian1.jpg,upload/chukuxinxi_fuzhuangtupian2.jpg,upload/chukuxinxi_fuzhuangtupian3.jpg', '颜色1', '尺寸1', 1, '出库账号1', '出库人1', '2025-01-02', '客户名称1', 'UNPAID', NULL);
INSERT INTO `chukuxinxi` VALUES (2, '2025-01-02 14:58:49', '服装编号2', '服装名称2', '供应商名称2', '商品分类2', '联系电话2', 'upload/chukuxinxi_fuzhuangtupian2.jpg,upload/chukuxinxi_fuzhuangtupian3.jpg,upload/chukuxinxi_fuzhuangtupian4.jpg', '颜色2', '尺寸2', 2, '出库账号2', '出库人2', '2025-01-02', '客户名称2', 'UNPAID', NULL);
INSERT INTO `chukuxinxi` VALUES (3, '2025-01-02 14:58:49', '服装编号3', '服装名称3', '供应商名称3', '商品分类3', '联系电话3', 'upload/chukuxinxi_fuzhuangtupian3.jpg,upload/chukuxinxi_fuzhuangtupian4.jpg,upload/chukuxinxi_fuzhuangtupian5.jpg', '颜色3', '尺寸3', 3, '出库账号3', '出库人3', '2025-01-02', '客户名称3', 'UNPAID', NULL);
INSERT INTO `chukuxinxi` VALUES (4, '2025-01-02 14:58:49', '服装编号4', '服装名称4', '供应商名称4', '商品分类4', '联系电话4', 'upload/chukuxinxi_fuzhuangtupian4.jpg,upload/chukuxinxi_fuzhuangtupian5.jpg,upload/chukuxinxi_fuzhuangtupian6.jpg', '颜色4', '尺寸4', 4, '出库账号4', '出库人4', '2025-01-02', '客户名称4', 'UNPAID', NULL);
INSERT INTO `chukuxinxi` VALUES (5, '2025-01-02 14:58:49', '服装编号5', '服装名称5', '供应商名称5', '商品分类5', '联系电话5', 'upload/chukuxinxi_fuzhuangtupian5.jpg,upload/chukuxinxi_fuzhuangtupian6.jpg,upload/chukuxinxi_fuzhuangtupian7.jpg', '颜色5', '尺寸5', 5, '出库账号5', '出库人5', '2025-01-02', '客户名称5', 'UNPAID', NULL);
INSERT INTO `chukuxinxi` VALUES (6, '2025-01-02 14:58:49', '服装编号6', '服装名称6', '供应商名称6', '商品分类6', '联系电话6', 'upload/chukuxinxi_fuzhuangtupian6.jpg,upload/chukuxinxi_fuzhuangtupian7.jpg,upload/chukuxinxi_fuzhuangtupian8.jpg', '颜色6', '尺寸6', 6, '出库账号6', '出库人6', '2025-01-02', '客户名称6', 'UNPAID', NULL);
INSERT INTO `chukuxinxi` VALUES (7, '2025-01-02 14:58:49', '服装编号7', '服装名称7', '供应商名称7', '商品分类7', '联系电话7', 'upload/chukuxinxi_fuzhuangtupian7.jpg,upload/chukuxinxi_fuzhuangtupian8.jpg,upload/chukuxinxi_fuzhuangtupian1.jpg', '颜色7', '尺寸7', 7, '出库账号7', '出库人7', '2025-01-02', '客户名称7', 'UNPAID', NULL);
INSERT INTO `chukuxinxi` VALUES (8, '2025-01-02 14:58:49', '服装编号8', '服装名称8', '供应商名称8', '商品分类8', '联系电话8', 'upload/chukuxinxi_fuzhuangtupian8.jpg,upload/chukuxinxi_fuzhuangtupian1.jpg,upload/chukuxinxi_fuzhuangtupian2.jpg', '颜色8', '尺寸8', 8, '出库账号8', '出库人8', '2025-01-02', '客户名称8', 'UNPAID', NULL);
INSERT INTO `chukuxinxi` VALUES (9, '2025-01-02 15:03:47', '2365', '改良汉服', '万达', '汉服', '13613945847', 'upload/1735801402261.jpg', '蓝色', '均码', 65, '账号1', '李月', '2025-01-02', '李月', 'UNPAID', NULL);

-- ----------------------------
-- Table structure for config
-- ----------------------------
DROP TABLE IF EXISTS `config`;
CREATE TABLE `config`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '配置参数名称',
  `value` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配置参数值',
  `url` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '外链或展示用url',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '配置文件' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of config
-- ----------------------------
INSERT INTO `config` VALUES (1, 'picture1', 'upload/picture1.jpg', NULL);
INSERT INTO `config` VALUES (2, 'picture2', 'upload/picture2.jpg', NULL);
INSERT INTO `config` VALUES (3, 'picture3', 'upload/picture3.jpg', NULL);

-- ----------------------------
-- Table structure for dinghuoxinxi
-- ----------------------------
DROP TABLE IF EXISTS `dinghuoxinxi`;
CREATE TABLE `dinghuoxinxi`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `fuzhuangbianhao` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服装编号',
  `fuzhuangmingcheng` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服装名称',
  `shangpinfenlei` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品分类',
  `gongyingshangbianhao` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '供应商编号',
  `gongyingshangmingcheng` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '供应商名称',
  `lianxidianhua` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `fuzhuangtupian` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '服装图片',
  `yanse` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '颜色',
  `chicun` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '尺寸',
  `fuzhuangkucun` int(11) NULL DEFAULT NULL COMMENT '订货数量',
  `xiaoshoudanjia` double NULL DEFAULT NULL COMMENT '销售单价',
  `zongjine` double NULL DEFAULT NULL COMMENT '总金额',
  `zhanghao` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '出库账号',
  `xingming` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '出库人',
  `dinghuoshijian` date NULL DEFAULT NULL COMMENT '订货时间',
  `kehumingcheng` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '客户名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订货信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dinghuoxinxi
-- ----------------------------
INSERT INTO `dinghuoxinxi` VALUES (1, '2025-01-02 14:58:49', '服装编号1', '服装名称1', '商品分类1', '供应商编号1', '供应商名称1', '联系电话1', 'upload/dinghuoxinxi_fuzhuangtupian1.jpg,upload/dinghuoxinxi_fuzhuangtupian2.jpg,upload/dinghuoxinxi_fuzhuangtupian3.jpg', '颜色1', '尺寸1', 1, 1, 1, '出库账号1', '出库人1', '2025-01-02', '客户名称1');
INSERT INTO `dinghuoxinxi` VALUES (2, '2025-01-02 14:58:49', '服装编号2', '服装名称2', '商品分类2', '供应商编号2', '供应商名称2', '联系电话2', 'upload/dinghuoxinxi_fuzhuangtupian2.jpg,upload/dinghuoxinxi_fuzhuangtupian3.jpg,upload/dinghuoxinxi_fuzhuangtupian4.jpg', '颜色2', '尺寸2', 2, 2, 2, '出库账号2', '出库人2', '2025-01-02', '客户名称2');
INSERT INTO `dinghuoxinxi` VALUES (3, '2025-01-02 14:58:49', '服装编号3', '服装名称3', '商品分类3', '供应商编号3', '供应商名称3', '联系电话3', 'upload/dinghuoxinxi_fuzhuangtupian3.jpg,upload/dinghuoxinxi_fuzhuangtupian4.jpg,upload/dinghuoxinxi_fuzhuangtupian5.jpg', '颜色3', '尺寸3', 3, 3, 3, '出库账号3', '出库人3', '2025-01-02', '客户名称3');
INSERT INTO `dinghuoxinxi` VALUES (4, '2025-01-02 14:58:49', '服装编号4', '服装名称4', '商品分类4', '供应商编号4', '供应商名称4', '联系电话4', 'upload/dinghuoxinxi_fuzhuangtupian4.jpg,upload/dinghuoxinxi_fuzhuangtupian5.jpg,upload/dinghuoxinxi_fuzhuangtupian6.jpg', '颜色4', '尺寸4', 4, 4, 4, '出库账号4', '出库人4', '2025-01-02', '客户名称4');
INSERT INTO `dinghuoxinxi` VALUES (5, '2025-01-02 14:58:49', '服装编号5', '服装名称5', '商品分类5', '供应商编号5', '供应商名称5', '联系电话5', 'upload/dinghuoxinxi_fuzhuangtupian5.jpg,upload/dinghuoxinxi_fuzhuangtupian6.jpg,upload/dinghuoxinxi_fuzhuangtupian7.jpg', '颜色5', '尺寸5', 5, 5, 5, '出库账号5', '出库人5', '2025-01-02', '客户名称5');
INSERT INTO `dinghuoxinxi` VALUES (6, '2025-01-02 14:58:49', '服装编号6', '服装名称6', '商品分类6', '供应商编号6', '供应商名称6', '联系电话6', 'upload/dinghuoxinxi_fuzhuangtupian6.jpg,upload/dinghuoxinxi_fuzhuangtupian7.jpg,upload/dinghuoxinxi_fuzhuangtupian8.jpg', '颜色6', '尺寸6', 6, 6, 6, '出库账号6', '出库人6', '2025-01-02', '客户名称6');
INSERT INTO `dinghuoxinxi` VALUES (7, '2025-01-02 14:58:49', '服装编号7', '服装名称7', '商品分类7', '供应商编号7', '供应商名称7', '联系电话7', 'upload/dinghuoxinxi_fuzhuangtupian7.jpg,upload/dinghuoxinxi_fuzhuangtupian8.jpg,upload/dinghuoxinxi_fuzhuangtupian1.jpg', '颜色7', '尺寸7', 7, 7, 7, '出库账号7', '出库人7', '2025-01-02', '客户名称7');
INSERT INTO `dinghuoxinxi` VALUES (8, '2025-01-02 14:58:49', '服装编号8', '服装名称8', '商品分类8', '供应商编号8', '供应商名称8', '联系电话8', 'upload/dinghuoxinxi_fuzhuangtupian8.jpg,upload/dinghuoxinxi_fuzhuangtupian1.jpg,upload/dinghuoxinxi_fuzhuangtupian2.jpg', '颜色8', '尺寸8', 8, 8, 8, '出库账号8', '出库人8', '2025-01-02', '客户名称8');
INSERT INTO `dinghuoxinxi` VALUES (9, '2025-01-02 15:04:12', '2365', '改良汉服', '汉服', '123456', '万达', '13613945847', 'upload/1735801402261.jpg', '蓝色', '均码', 10, 50, 500, '111', '李月', '2025-01-02', '李月');

-- ----------------------------
-- Table structure for discusstejiashangpin
-- ----------------------------
DROP TABLE IF EXISTS `discusstejiashangpin`;
CREATE TABLE `discusstejiashangpin`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `refid` bigint(20) NOT NULL COMMENT '关联表id',
  `userid` bigint(20) NOT NULL COMMENT '用户id',
  `avatarurl` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '头像',
  `nickname` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '评论内容',
  `reply` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '回复内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '特价商品评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of discusstejiashangpin
-- ----------------------------

-- ----------------------------
-- Table structure for discusszuixinshangshi
-- ----------------------------
DROP TABLE IF EXISTS `discusszuixinshangshi`;
CREATE TABLE `discusszuixinshangshi`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `refid` bigint(20) NOT NULL COMMENT '关联表id',
  `userid` bigint(20) NOT NULL COMMENT '用户id',
  `avatarurl` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '头像',
  `nickname` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '评论内容',
  `reply` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '回复内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '最新上市评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of discusszuixinshangshi
-- ----------------------------

-- ----------------------------
-- Table structure for gongyingshang
-- ----------------------------
DROP TABLE IF EXISTS `gongyingshang`;
CREATE TABLE `gongyingshang`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gongyingshangid` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '供应商id',
  `gongyingshangbianhao` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '供应商编号',
  `gongyingshangmingcheng` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '供应商名称',
  `dizhi` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址',
  `lianxidianhua` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `fuzeren` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '负责人',
  `gongyingshangxiangqing` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '供应商详情',
  `tianjiashijian` date NULL DEFAULT NULL COMMENT '添加时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '供应商' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gongyingshang
-- ----------------------------
INSERT INTO `gongyingshang` VALUES (1, '2025-01-02 14:58:49', '供应商id1', '供应商编号1', '供应商名称1', '地址1', '联系电话1', '负责人1', '供应商详情1', '2025-01-02');
INSERT INTO `gongyingshang` VALUES (2, '2025-01-02 14:58:49', '供应商id2', '供应商编号2', '供应商名称2', '地址2', '联系电话2', '负责人2', '供应商详情2', '2025-01-02');
INSERT INTO `gongyingshang` VALUES (3, '2025-01-02 14:58:49', '供应商id3', '供应商编号3', '供应商名称3', '地址3', '联系电话3', '负责人3', '供应商详情3', '2025-01-02');
INSERT INTO `gongyingshang` VALUES (4, '2025-01-02 14:58:49', '供应商id4', '供应商编号4', '供应商名称4', '地址4', '联系电话4', '负责人4', '供应商详情4', '2025-01-02');
INSERT INTO `gongyingshang` VALUES (5, '2025-01-02 14:58:49', '供应商id5', '供应商编号5', '供应商名称5', '地址5', '联系电话5', '负责人5', '供应商详情5', '2025-01-02');
INSERT INTO `gongyingshang` VALUES (6, '2025-01-02 14:58:49', '供应商id6', '供应商编号6', '供应商名称6', '地址6', '联系电话6', '负责人6', '供应商详情6', '2025-01-02');
INSERT INTO `gongyingshang` VALUES (7, '2025-01-02 14:58:49', '供应商id7', '供应商编号7', '供应商名称7', '地址7', '联系电话7', '负责人7', '供应商详情7', '2025-01-02');
INSERT INTO `gongyingshang` VALUES (8, '2025-01-02 14:58:49', '供应商id8', '供应商编号8', '供应商名称8', '地址8', '联系电话8', '负责人8', '供应商详情8', '2025-01-02');
INSERT INTO `gongyingshang` VALUES (9, '2025-01-02 15:03:06', '123', '123456', '万达', '深圳', '13613945847', '李凯凯', '<p><strong style=\"background-color: rgb(225, 225, 225); color: rgb(19, 39, 73);\">基于SpringBoot的仓库管理系统设计与实现基于SpringBoot的仓库管理系统设计与实现基于SpringBoot的仓库管理系统设计与实现</strong></p>', '2025-01-02');

-- ----------------------------
-- Table structure for news
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `title` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标题',
  `introduction` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '简介',
  `picture` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '图片',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 109 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '优惠资讯' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of news
-- ----------------------------
INSERT INTO `news` VALUES (101, '2023-02-28 18:57:46', '有梦想，就要努力去实现', '不管你想要怎样的生活，你都要去努力争取，不多尝试一些事情怎么知道自己适合什么、不适合什么呢?你说你喜欢读书，让我给你列书单，你还问我哪里有那么多时间看书;你说自己梦想的职业是广告文案，问我如何成为一个文案，应该具备哪些素质;你说你计划晨跑，但总是因为学习、工作辛苦或者身体不舒服第二天起不了床;你说你一直梦想一个人去长途旅行，但是没钱，父母觉得危险。', 'upload/news_picture1.jpg', '<p>不管你想要怎样的生活，你都要去努力争取，不多尝试一些事情怎么知道自己适合什么、不适合什么呢?</p><p>你说你喜欢读书，让我给你列书单，你还问我哪里有那么多时间看书;你说自己梦想的职业是广告文案，问我如何成为一个文案，应该具备哪些素质;你说你计划晨跑，但总是因为学习、工作辛苦或者身体不舒服第二天起不了床;你说你一直梦想一个人去长途旅行，但是没钱，父母觉得危险。其实，我已经厌倦了你这样说说而已的把戏，我觉得就算我告诉你如何去做，你也不会照做，因为你根本什么都不做。</p><p>真正有行动力的人不需要别人告诉他如何做，因为他已经在做了。就算碰到问题，他也会自己想办法，自己动手去解决或者主动寻求可以帮助他的人，而不是等着别人为自己解决问题。</p><p>首先要学习独立思考。花一点时间想一下自己喜欢什么，梦想是什么，不要别人说想环游世界，你就说你的梦想是环游世界。</p><p>很多人说现实束缚了自己，其实在这个世界上，我们一直都可以有很多选择，生活的决定权也—直都在自己手上，只是我们缺乏行动力而已。</p><p>如果你觉得安于现状是你想要的，那选择安于现状就会让你幸福和满足;如果你不甘平庸，选择一条改变、进取和奋斗的道路，在这个追求的过程中，你也一样会感到快乐。所谓的成功，即是按照自己想要的生活方式生活。最糟糕的状态，莫过于当你想要选择一条不甘平庸、改变、进取和奋斗的道路时，却以一种安于现状的方式生活，最后抱怨自己没有得到想要的人生。</p><p>因为喜欢，你不是在苦苦坚持，也因为喜欢，你愿意投入时间、精力，长久以往，获得成功就是自然而然的事情。</p>');
INSERT INTO `news` VALUES (102, '2023-02-28 18:57:46', '又是一年毕业季', '又是一年毕业季，感慨万千，还记的自己刚进学校那时候的情景，我拖着沉重的行李箱站在偌大的教学楼前面，感叹自己未来的日子即将在这个陌生的校园里度过，而如今斗转星移，浮光掠影，弹指之间，那些青葱岁月如同白驹过隙般悄然从指缝溜走。过去的种种在胸口交集纠结，像打翻的五味瓶，甜蜜，酸楚，苦涩，一并涌上心头。', 'upload/news_picture2.jpg', '<p>又是一年毕业季，感慨万千，还记的自己刚进学校那时候的情景，我拖着沉重的行李箱站在偌大的教学楼前面，感叹自己未来的日子即将在这个陌生的校园里度过，而如今斗转星移，浮光掠影，弹指之间，那些青葱岁月如同白驹过隙般悄然从指缝溜走。</p><p>过去的种种在胸口交集纠结，像打翻的五味瓶，甜蜜，酸楚，苦涩，一并涌上心头。一直都是晚会的忠实参与者，无论是台前还是幕后，忽然间，角色转变，那种感觉确实难以用语言表达。</p><p>	过去的三年，总是默默地期盼着这个好雨时节，因为这时候，会有灿烂的阳光，会有满目的百花争艳，会有香甜的冰激凌，这是个毕业的季节，当时不经世事的我们会殷切地期待学校那一大堆的活动，期待穿上绚丽的演出服或者礼仪服，站在大礼堂镁光灯下尽情挥洒我们的澎拜的激情。</p><p>百感交集，隔岸观火与身临其境的感觉竟是如此不同。从来没想过一场晚会送走的是我们自己的时候会是怎样的感情，毕业就真的意味着结束吗?倔强的我们不愿意承认，谢谢学弟学妹们慷慨的将这次的主题定为“我们在这里”。我知道，这可能是他们对我们这些过来人的尊敬和施舍。</p><p>没有为这场晚会排练、奔波，没有为班级、学生会、文学院出点力，还真有点不习惯，百般无奈中，用“工作忙”个万能的借口来搪塞自己，欺骗别人。其实自己心里明白，那只是在逃避，只是不愿面对繁华落幕后的萧条和落寞。大四了，大家各奔东西，想凑齐班上的人真的是难上加难，敏燕从越南回来，刚落地就匆匆回了学校，那么恋家的人也启程回来了，睿睿学姐也是从家赶来跟我们团圆。大家—如既往的寒暄、打趣、调侃对方，似乎一切又回到了当初的单纯美好。</p><p>看着舞台上活泼可爱的学弟学妹们，如同一群机灵的小精灵，清澈的眼神，稚嫩的肢体，轻快地步伐，用他们那热情洋溢的舞姿渲染着在场的每一个人，我知道，我不应该羡慕嫉妒他们，不应该顾自怜惜逝去的青春，不应该感叹夕阳无限好，曾经，我们也拥有过，曾经，我们也年轻过，曾经，我们也灿烂过。我深深地告诉自己，人生的每个阶段都是美的，年轻有年轻的活力，成熟也有成熟的魅力。多—份稳重、淡然、优雅，也是漫漫时光掠影遗留下的.珍贵赏赐。</p>');
INSERT INTO `news` VALUES (103, '2023-02-28 18:57:46', '挫折路上，坚持常在心间', '回头看看，你会不会发现，曾经的你在这里摔倒过;回头看看，你是否发现，一次次地重复着，却从没爬起过。而如今，让我们把视线转向前方，那一道道金色的弧线，是流星飞逝的痕迹，或是成功运行的轨道。今天的你，是否要扬帆起航，让幸福来敲门?清晨的太阳撒向大地，神奇的宇宙赋予它神奇的色彩，大自然沐浴着春光，世界因太阳的照射而精彩，林中百鸟啾啾，河水轻轻流淌，汇成清宁的山间小调。', 'upload/news_picture3.jpg', '<p>回头看看，你会不会发现，曾经的你在这里摔倒过;回头看看，你是否发现，一次次地重复着，却从没爬起过。而如今，让我们把视线转向前方，那一道道金色的弧线，是流星飞逝的痕迹，或是成功运行的轨道。今天的你，是否要扬帆起航，让幸福来敲门?</p><p>清晨的太阳撒向大地，神奇的宇宙赋予它神奇的色彩，大自然沐浴着春光，世界因太阳的照射而精彩，林中百鸟啾啾，河水轻轻流淌，汇成清宁的山间小调。</p><p>是的，面对道途上那无情的嘲讽，面对步伐中那重复的摔跤，面对激流与硬石之间猛烈的碰撞，我们必须选择那富于阴雨，却最终见到彩虹的荆棘路。也许，经历了那暴风雨的洗礼，我们便会变得自信，幸福也随之而来。</p><p>司马迁屡遭羞辱，却依然在狱中撰写《史记》，作为一名史学家，不因王权而极度赞赏，也不因卑微而极度批判，然而他在坚持自己操守的同时，却依然要受统治阶级的阻碍，他似乎无权选择自己的本职。但是，他不顾于此，只是在面对道途的阻隔之时，他依然选择了走下去的信念。终于一部开山巨作《史记》诞生，为后人留下一份馈赠，也许在他完成毕生的杰作之时，他微微地笑了，没有什么比梦想实现更快乐的了......</p><p>	或许正如“长风破浪会有时，直挂云帆济沧海”一般，欣欣然地走向看似深渊的崎岖路，而在一番耕耘之后，便会发现这里另有一番天地。也许这就是困难与快乐的交融。</p><p>也许在形形色色的社会中，我们常能看到一份坚持，一份自信，但这里却还有一类人。这类人在暴风雨来临之际，只会闪躲，从未懂得这也是一种历炼，这何尝不是一份快乐。在阴暗的角落里，总是独自在哭，带着伤愁，看不到一点希望。</p><p>我们不能堕落于此，而要像海燕那般，在苍茫的大海上，高傲地飞翔，任何事物都无法阻挡，任何事都是幸福快乐的。</p>');
INSERT INTO `news` VALUES (104, '2023-02-28 18:57:46', '挫折是另一个生命的开端', '当遇到挫折或失败，你是看见失败还是看见机会?挫折是我们每个人成长的必经之路，它不是你想有就有，想没有就没有的。有句名言说的好，如果你想一生摆脱苦难，你就得是神或者是死尸。这句话形象地说明了挫折是伴随着人生的，是谁都逃不掉的。', 'upload/news_picture4.jpg', '<p>当遇到挫折或失败，你是看见失败还是看见机会?</p><p>挫折是我们每个人成长的必经之路，它不是你想有就有，想没有就没有的。有句名言说的好，如果你想一生摆脱苦难，你就得是神或者是死尸。这句话形象地说明了挫折是伴随着人生的，是谁都逃不掉的。</p><p>人生在世，从古到今，不分天子平民，机遇虽有不同，但总不免有身陷困境或遭遇难题之处，这时候唯有通权达变，才能使人转危为安，甚至反败为胜。</p><p>大部分的人，一生当中，最痛苦的经验是失去所爱的人，其次是丢掉一份工作。其实，经得起考验的人，就算是被开除也不会惊慌，要学会面对。</p><p>	“塞翁失马，焉知非福。”人生的道路，并不是每一步都迈向成功，这就是追求的意义。我们还要认识到一点，挫折作为一种情绪状态和一种个人体验，各人的耐受性是大不相同的，有的人经历了一次次挫折，就能够坚忍不拔，百折不挠;有的人稍遇挫折便意志消沉，一蹶不振。所以，挫折感是一种主观感受，因为人的目的和需要不同，成功标准不同，所以同一种活动对于不同的人可能会造成不同的挫折感受。</p><p>凡事皆以平常心来看待，对于生命顺逆不要太执著。能够“破我执”是很高层的人生境界。</p><p>人事的艰难就是一种考验。就像—支剑要有磨刀来磨，剑才会利:一块璞玉要有粗石来磨，才会发出耀眼的光芒。我们能够做到的，只是如何减少、避免那些由于自身的原因所造成的挫折，而在遇到痛苦和挫折之后，则力求化解痛苦，争取幸福。我们要知道，痛苦和挫折是双重性的，它既是我们人生中难以完全避免的，也是我们在争取成功时，不可缺少的一种动力。因为我认为，推动我们奋斗的力量，不仅仅是对成功的渴望，还有为摆脱痛苦和挫折而进行的奋斗。</p>');
INSERT INTO `news` VALUES (105, '2023-02-28 18:57:46', '你要去相信，没有到不了的明天', '有梦想就去努力，因为在这一辈子里面，现在不去勇敢的努力，也许就再也没有机会了。你要去相信，一定要相信，没有到不了的明天。不要被命运打败，让自己变得更强大。不管你现在是一个人走在异乡的街道上始终没有找到一丝归属感，还是你在跟朋友们一起吃饭开心址笑着的时候闪过一丝落寞。', 'upload/news_picture5.jpg', '<p>有梦想就去努力，因为在这一辈子里面，现在不去勇敢的努力，也许就再也没有机会了。你要去相信，一定要相信，没有到不了的明天。不要被命运打败，让自己变得更强大。</p><p>不管你现在是一个人走在异乡的街道上始终没有找到一丝归属感，还是你在跟朋友们一起吃饭开心址笑着的时候闪过一丝落寞。</p><p>	不管你现在是在图书馆里背着怎么也看不进去的英语单词，还是你现在迷茫地看不清未来的方向不知道要往哪走。</p><p>不管你现在是在努力着去实现梦想却没能拉近与梦想的距离，还是你已经慢慢地找不到自己的梦想了。</p><p>你都要去相信，没有到不了的明天。</p><p>	有的时候你的梦想太大，别人说你的梦想根本不可能实现;有的时候你的梦想又太小，又有人说你胸无大志;有的时候你对死党说着将来要去环游世界的梦想，却换来他的不屑一顾，于是你再也不提自己的梦想;有的时候你突然说起将来要开个小店的愿望，却发现你讲述的那个人，并没有听到你在说什么。</p><p>不过又能怎么样呢，未来始终是自己的，梦想始终是自己的，没有人会来帮你实现它。</p><p>也许很多时候我们只是需要朋友的一句鼓励，一句安慰，却也得不到。但是相信我，世界上还有很多人，只是想要和你说说话。</p><p>因为我们都一样。一样的被人说成固执，一样的在追逐他们眼里根本不在意的东西。</p><p>所以，又有什么关系呢，别人始终不是你、不能懂你的心情，你又何必多去解释呢。这个世界会来阻止你，困难也会接踵而至，其实真正关键的只有自己，有没有那个倔强。</p><p>这个世界上没有不带伤的人，真正能治愈自己的，只有自己。</p>');
INSERT INTO `news` VALUES (106, '2023-02-28 18:57:46', '离开是一种痛苦，是一种勇气，但同样也是一个考验，是一个新的开端', '无穷无尽是离愁，天涯海角遍寻思。当离别在即之时，当面对着相濡以沫兄弟般的朋友时，当面对着经历了四年的磨合而形成的真挚友谊之时，我内心激动无语，说一声再见，道一声珍重都很难出口。回想自己四年大学的风风雨雨，回想我们曾经共同经历的岁月流年，我感谢大家的相扶相依，感谢朋友们的莫大支持与帮助。虽然舍不得，但离别的脚步却不因我们的挚情而停滞。', 'upload/news_picture6.jpg', '<p>无穷无尽是离愁，天涯海角遍寻思。当离别在即之时，当面对着相濡以沫兄弟般的朋友时，当面对着经历了四年的磨合而形成的真挚友谊之时，我内心激动无语，说一声再见，道一声珍重都很难出口。回想自己四年大学的风风雨雨，回想我们曾经共同经历的岁月流年，我感谢大家的相扶相依，感谢朋友们的莫大支持与帮助。虽然舍不得，但离别的脚步却不因我们的挚情而停滞。离别的确是一种痛苦，但同样也是我们走入社会，走向新环境、新领域的一个开端，希望大家在以后新的工作岗位上能够确定自己的新起点，坚持不懈，向着更新、更高的目标前进，因为人生最美好的东西永远都在最前方!</p><p>忆往昔峥嵘岁月，看今朝潮起潮落，望未来任重而道远。作为新时代的我们，就应在失败时，能拼搏奋起，去谱写人生的辉煌。在成功时，亦能居安思危，不沉湎于一时的荣耀、鲜花和掌声中，时时刻刻怀着一颗积极寻找自己新的奶酪的心，处变不惊、成败不渝，始终踏着自己坚实的步伐，从零开始，不断向前迈进，这样才能在这风起云涌、变幻莫测的社会大潮中成为真正的弄潮儿!</p>');
INSERT INTO `news` VALUES (107, '2023-02-28 18:57:46', 'Leave未必是一种痛苦', '无穷无尽是离愁，天涯海角遍寻思。当离别在即之时，当面对着相濡以沫兄弟般的朋友时，当面对着经历了四年的磨合而形成的真挚友谊之时，我内心激动无语，说一声再见，道一声珍重都很难出口。回想自己四年大学的风风雨雨，回想我们曾经共同经历的岁月流年，我感谢大家的相扶相依，感谢朋友们的莫大支持与帮助。虽然舍不得，但离别的脚步却不因我们的挚情而停滞。', 'upload/news_picture7.jpg', '<p>无穷无尽是离愁，天涯海角遍寻思。当离别在即之时，当面对着相濡以沫兄弟般的朋友时，当面对着经历了四年的磨合而形成的真挚友谊之时，我内心激动无语，说一声再见，道一声珍重都很难出口。回想自己四年大学的风风雨雨，回想我们曾经共同经历的岁月流年，我感谢大家的相扶相依，感谢朋友们的莫大支持与帮助。虽然舍不得，但离别的脚步却不因我们的挚情而停滞。离别的确是一种痛苦，但同样也是我们走入社会，走向新环境、新领域的一个开端，希望大家在以后新的工作岗位上能够确定自己的新起点，坚持不懈，向着更新、更高的目标前进，因为人生最美好的东西永远都在最前方!</p><p>忆往昔峥嵘岁月，看今朝潮起潮落，望未来任重而道远。作为新时代的我们，就应在失败时，能拼搏奋起，去谱写人生的辉煌。在成功时，亦能居安思危，不沉湎于一时的荣耀、鲜花和掌声中，时时刻刻怀着一颗积极寻找自己新的奶酪的心，处变不惊、成败不渝，始终踏着自己坚实的步伐，从零开始，不断向前迈进，这样才能在这风起云涌、变幻莫测的社会大潮中成为真正的弄潮儿!</p>');
INSERT INTO `news` VALUES (108, '2023-02-28 18:57:46', '坚持才会成功', '回头看看，你会不会发现，曾经的你在这里摔倒过;回头看看，你是否发现，一次次地重复着，却从没爬起过。而如今，让我们把视线转向前方，那一道道金色的弧线，是流星飞逝的痕迹，或是成功运行的轨道。今天的你，是否要扬帆起航，让幸福来敲门?清晨的太阳撒向大地，神奇的宇宙赋予它神奇的色彩，大自然沐浴着春光，世界因太阳的照射而精彩，林中百鸟啾啾，河水轻轻流淌，汇成清宁的山间小调。', 'upload/news_picture8.jpg', '<p>回头看看，你会不会发现，曾经的你在这里摔倒过;回头看看，你是否发现，一次次地重复着，却从没爬起过。而如今，让我们把视线转向前方，那一道道金色的弧线，是流星飞逝的痕迹，或是成功运行的轨道。今天的你，是否要扬帆起航，让幸福来敲门?</p><p>清晨的太阳撒向大地，神奇的宇宙赋予它神奇的色彩，大自然沐浴着春光，世界因太阳的照射而精彩，林中百鸟啾啾，河水轻轻流淌，汇成清宁的山间小调。</p><p>是的，面对道途上那无情的嘲讽，面对步伐中那重复的摔跤，面对激流与硬石之间猛烈的碰撞，我们必须选择那富于阴雨，却最终见到彩虹的荆棘路。也许，经历了那暴风雨的洗礼，我们便会变得自信，幸福也随之而来。</p><p>司马迁屡遭羞辱，却依然在狱中撰写《史记》，作为一名史学家，不因王权而极度赞赏，也不因卑微而极度批判，然而他在坚持自己操守的同时，却依然要受统治阶级的阻碍，他似乎无权选择自己的本职。但是，他不顾于此，只是在面对道途的阻隔之时，他依然选择了走下去的信念。终于一部开山巨作《史记》诞生，为后人留下一份馈赠，也许在他完成毕生的杰作之时，他微微地笑了，没有什么比梦想实现更快乐的了......</p><p>	或许正如“长风破浪会有时，直挂云帆济沧海”一般，欣欣然地走向看似深渊的崎岖路，而在一番耕耘之后，便会发现这里另有一番天地。也许这就是困难与快乐的交融。</p><p>也许在形形色色的社会中，我们常能看到一份坚持，一份自信，但这里却还有一类人。这类人在暴风雨来临之际，只会闪躲，从未懂得这也是一种历炼，这何尝不是一份快乐。在阴暗的角落里，总是独自在哭，带着伤愁，看不到一点希望。</p><p>我们不能堕落于此，而要像海燕那般，在苍茫的大海上，高傲地飞翔，任何事物都无法阻挡，任何事都是幸福快乐的。</p>');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `orderid` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单编号',
  `tablename` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'tejiashangpin' COMMENT '商品表名',
  `userid` bigint(20) NOT NULL COMMENT '用户id',
  `goodid` bigint(20) NOT NULL COMMENT '商品id',
  `goodname` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `picture` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '商品图片',
  `buynumber` int(11) NOT NULL COMMENT '购买数量',
  `price` float NOT NULL DEFAULT 0 COMMENT '价格',
  `discountprice` float NULL DEFAULT 0 COMMENT '折扣价格',
  `total` float NOT NULL DEFAULT 0 COMMENT '总价格',
  `discounttotal` float NULL DEFAULT 0 COMMENT '折扣总价格',
  `type` int(11) NULL DEFAULT 1 COMMENT '支付类型',
  `status` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
  `address` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址',
  `tel` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电话',
  `consignee` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收货人',
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `logistics` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '物流',
  `goodtype` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品类型',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `orderid`(`orderid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1680527152289 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1680272996891, '2023-03-31 22:29:56', '202333122295630', 'tejiashangpin', 11, 41, '物价名称1', 'upload/tejiashangpin_fengmian1.jpg', 1, 99.9, 99.9, 99.9, 99.9, 1, '已发货', '33', '13689659652', '用户账号1', '', NULL, NULL);
INSERT INTO `orders` VALUES (1680527138356, '2023-04-03 21:05:37', '20234321537712', 'tejiashangpin', 1680527067622, 41, '物价名称1', 'upload/tejiashangpin_fengmian1.jpg', 1, 99.9, 99.9, 99.9, 99.9, 1, '已支付', '333', '13696869654', '111', '', NULL, NULL);
INSERT INTO `orders` VALUES (1680527152288, '2023-04-03 21:05:52', '202343215522', 'tejiashangpin', 1680527067622, 41, '物价名称1', 'upload/tejiashangpin_fengmian1.jpg', 1, 99.9, 99.9, 99.9, 99.9, 1, '已发货', '333', '13696869654', '111', '', NULL, NULL);

-- ----------------------------
-- Table structure for rukuxinxi
-- ----------------------------
DROP TABLE IF EXISTS `rukuxinxi`;
CREATE TABLE `rukuxinxi`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dinghuoid` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订货id',
  `fuzhuangbianhao` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服装编号',
  `fuzhuangmingcheng` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服装名称',
  `shangpinfenlei` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品分类',
  `gongyingshangmingcheng` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '供应商名称',
  `gongyingshangid` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '供应商id',
  `lianxidianhua` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `fuzhuangtupian` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '服装图片',
  `yanse` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '颜色',
  `chicun` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '尺寸',
  `fuzhuangkucun` int(11) NULL DEFAULT NULL COMMENT '入库数量',
  `zhanghao` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '验收账号',
  `xingming` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '验收人',
  `rukushijian` date NULL DEFAULT NULL COMMENT '入库时间',
  `yanshoushuoming` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '验收说明',
  `payment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'UNPAID' COMMENT '付款状态：PAID-已付款, UNPAID-未付款',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '付款时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '入库信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rukuxinxi
-- ----------------------------
INSERT INTO `rukuxinxi` VALUES (1, '2025-01-02 14:58:49', '订货id1', '服装编号1', '服装名称1', '商品分类1', '供应商名称1', '供应商id1', '联系电话1', 'upload/rukuxinxi_fuzhuangtupian1.jpg,upload/rukuxinxi_fuzhuangtupian2.jpg,upload/rukuxinxi_fuzhuangtupian3.jpg', '颜色1', '尺寸1', 1, '验收账号1', '验收人1', '2025-01-02', '验收说明1', 'UNPAID', NULL);
INSERT INTO `rukuxinxi` VALUES (2, '2025-01-02 14:58:49', '订货id2', '服装编号2', '服装名称2', '商品分类2', '供应商名称2', '供应商id2', '联系电话2', 'upload/rukuxinxi_fuzhuangtupian2.jpg,upload/rukuxinxi_fuzhuangtupian3.jpg,upload/rukuxinxi_fuzhuangtupian4.jpg', '颜色2', '尺寸2', 2, '验收账号2', '验收人2', '2025-01-02', '验收说明2', 'UNPAID', NULL);
INSERT INTO `rukuxinxi` VALUES (3, '2025-01-02 14:58:49', '订货id3', '服装编号3', '服装名称3', '商品分类3', '供应商名称3', '供应商id3', '联系电话3', 'upload/rukuxinxi_fuzhuangtupian3.jpg,upload/rukuxinxi_fuzhuangtupian4.jpg,upload/rukuxinxi_fuzhuangtupian5.jpg', '颜色3', '尺寸3', 3, '验收账号3', '验收人3', '2025-01-02', '验收说明3', 'UNPAID', NULL);
INSERT INTO `rukuxinxi` VALUES (4, '2025-01-02 14:58:49', '订货id4', '服装编号4', '服装名称4', '商品分类4', '供应商名称4', '供应商id4', '联系电话4', 'upload/rukuxinxi_fuzhuangtupian4.jpg,upload/rukuxinxi_fuzhuangtupian5.jpg,upload/rukuxinxi_fuzhuangtupian6.jpg', '颜色4', '尺寸4', 4, '验收账号4', '验收人4', '2025-01-02', '验收说明4', 'UNPAID', NULL);
INSERT INTO `rukuxinxi` VALUES (5, '2025-01-02 14:58:49', '订货id5', '服装编号5', '服装名称5', '商品分类5', '供应商名称5', '供应商id5', '联系电话5', 'upload/rukuxinxi_fuzhuangtupian5.jpg,upload/rukuxinxi_fuzhuangtupian6.jpg,upload/rukuxinxi_fuzhuangtupian7.jpg', '颜色5', '尺寸5', 5, '验收账号5', '验收人5', '2025-01-02', '验收说明5', 'UNPAID', NULL);
INSERT INTO `rukuxinxi` VALUES (6, '2025-01-02 14:58:49', '订货id6', '服装编号6', '服装名称6', '商品分类6', '供应商名称6', '供应商id6', '联系电话6', 'upload/rukuxinxi_fuzhuangtupian6.jpg,upload/rukuxinxi_fuzhuangtupian7.jpg,upload/rukuxinxi_fuzhuangtupian8.jpg', '颜色6', '尺寸6', 6, '验收账号6', '验收人6', '2025-01-02', '验收说明6', 'UNPAID', NULL);
INSERT INTO `rukuxinxi` VALUES (7, '2025-01-02 14:58:49', '订货id7', '服装编号7', '服装名称7', '商品分类7', '供应商名称7', '供应商id7', '联系电话7', 'upload/rukuxinxi_fuzhuangtupian7.jpg,upload/rukuxinxi_fuzhuangtupian8.jpg,upload/rukuxinxi_fuzhuangtupian1.jpg', '颜色7', '尺寸7', 7, '验收账号7', '验收人7', '2025-01-02', '验收说明7', 'UNPAID', NULL);
INSERT INTO `rukuxinxi` VALUES (8, '2025-01-02 14:58:49', '订货id8', '服装编号8', '服装名称8', '商品分类8', '供应商名称8', '供应商id8', '联系电话8', 'upload/rukuxinxi_fuzhuangtupian8.jpg,upload/rukuxinxi_fuzhuangtupian1.jpg,upload/rukuxinxi_fuzhuangtupian2.jpg', '颜色8', '尺寸8', 8, '验收账号8', '验收人8', '2025-01-02', '验收说明8', 'UNPAID', NULL);
INSERT INTO `rukuxinxi` VALUES (9, '2025-01-02 15:03:59', '121', '2365', '改良汉服', '汉服', '万达', '123', '13613945847', 'upload/1735801402261.jpg', '蓝色', '均码', 101, '111', '李月', '2025-01-02', '1111', 'UNPAID', NULL);

-- ----------------------------
-- Table structure for shangpinfenlei
-- ----------------------------
DROP TABLE IF EXISTS `shangpinfenlei`;
CREATE TABLE `shangpinfenlei`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `shangpinfenlei` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品分类',
  `tianjiashijian` date NULL DEFAULT NULL COMMENT '添加时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shangpinfenlei
-- ----------------------------
INSERT INTO `shangpinfenlei` VALUES (1, '2025-01-02 14:58:49', '商品分类1', '2025-01-02');
INSERT INTO `shangpinfenlei` VALUES (2, '2025-01-02 14:58:49', '商品分类2', '2025-01-02');
INSERT INTO `shangpinfenlei` VALUES (3, '2025-01-02 14:58:49', '商品分类3', '2025-01-02');
INSERT INTO `shangpinfenlei` VALUES (4, '2025-01-02 14:58:49', '商品分类4', '2025-01-02');
INSERT INTO `shangpinfenlei` VALUES (5, '2025-01-02 14:58:49', '商品分类5', '2025-01-02');
INSERT INTO `shangpinfenlei` VALUES (6, '2025-01-02 14:58:49', '商品分类6', '2025-01-02');
INSERT INTO `shangpinfenlei` VALUES (7, '2025-01-02 14:58:49', '商品分类7', '2025-01-02');
INSERT INTO `shangpinfenlei` VALUES (8, '2025-01-02 14:58:49', '商品分类8', '2025-01-02');
INSERT INTO `shangpinfenlei` VALUES (9, '2025-01-02 15:02:49', '汉服', '2025-01-02');

-- ----------------------------
-- Table structure for shangpinxinxi
-- ----------------------------
DROP TABLE IF EXISTS `shangpinxinxi`;
CREATE TABLE `shangpinxinxi`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `fuzhuangbianhao` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服装编号',
  `fuzhuangmingcheng` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服装名称',
  `shangpinfenlei` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品分类',
  `gongyingshangid` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '供应商id',
  `gongyingshangbianhao` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '供应商编号',
  `gongyingshangmingcheng` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '供应商名称',
  `lianxidianhua` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `fuzhuangtupian` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '服装图片',
  `yanse` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '颜色',
  `chicun` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '尺寸',
  `fuzhuangkucun` int(11) NULL DEFAULT NULL COMMENT '服装库存',
  `kucunyuzhi` int(11) NULL DEFAULT 10 COMMENT '库存阈值',
  `fuzhuangxiangqing` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '服装详情',
  `tianjiashijian` date NULL DEFAULT NULL COMMENT '添加时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shangpinxinxi
-- ----------------------------
INSERT INTO `shangpinxinxi` VALUES (1, '2025-01-02 14:58:49', '服装编号1', '服装名称1', '商品分类1', '供应商id1', '供应商编号1', '供应商名称1', '联系电话1', 'upload/shangpinxinxi_fuzhuangtupian1.jpg,upload/shangpinxinxi_fuzhuangtupian2.jpg,upload/shangpinxinxi_fuzhuangtupian3.jpg', '颜色1', '尺寸1', 1, 10, '服装详情1', '2025-01-02');
INSERT INTO `shangpinxinxi` VALUES (2, '2025-01-02 14:58:49', '服装编号2', '服装名称2', '商品分类2', '供应商id2', '供应商编号2', '供应商名称2', '联系电话2', 'upload/shangpinxinxi_fuzhuangtupian2.jpg,upload/shangpinxinxi_fuzhuangtupian3.jpg,upload/shangpinxinxi_fuzhuangtupian4.jpg', '颜色2', '尺寸2', 2, 10, '服装详情2', '2025-01-02');
INSERT INTO `shangpinxinxi` VALUES (3, '2025-01-02 14:58:49', '服装编号3', '服装名称3', '商品分类3', '供应商id3', '供应商编号3', '供应商名称3', '联系电话3', 'upload/shangpinxinxi_fuzhuangtupian3.jpg,upload/shangpinxinxi_fuzhuangtupian4.jpg,upload/shangpinxinxi_fuzhuangtupian5.jpg', '颜色3', '尺寸3', 3, 10, '服装详情3', '2025-01-02');
INSERT INTO `shangpinxinxi` VALUES (4, '2025-01-02 14:58:49', '服装编号4', '服装名称4', '商品分类4', '供应商id4', '供应商编号4', '供应商名称4', '联系电话4', 'upload/shangpinxinxi_fuzhuangtupian4.jpg,upload/shangpinxinxi_fuzhuangtupian5.jpg,upload/shangpinxinxi_fuzhuangtupian6.jpg', '颜色4', '尺寸4', 4, 10, '服装详情4', '2025-01-02');
INSERT INTO `shangpinxinxi` VALUES (5, '2025-01-02 14:58:49', '服装编号5', '服装名称5', '商品分类5', '供应商id5', '供应商编号5', '供应商名称5', '联系电话5', 'upload/shangpinxinxi_fuzhuangtupian5.jpg,upload/shangpinxinxi_fuzhuangtupian6.jpg,upload/shangpinxinxi_fuzhuangtupian7.jpg', '颜色5', '尺寸5', 5, 10, '服装详情5', '2025-01-02');
INSERT INTO `shangpinxinxi` VALUES (6, '2025-01-02 14:58:49', '服装编号6', '服装名称6', '商品分类6', '供应商id6', '供应商编号6', '供应商名称6', '联系电话6', 'upload/shangpinxinxi_fuzhuangtupian6.jpg,upload/shangpinxinxi_fuzhuangtupian7.jpg,upload/shangpinxinxi_fuzhuangtupian8.jpg', '颜色6', '尺寸6', 6, 10, '服装详情6', '2025-01-02');
INSERT INTO `shangpinxinxi` VALUES (7, '2025-01-02 14:58:49', '服装编号7', '服装名称7', '商品分类7', '供应商id7', '供应商编号7', '供应商名称7', '联系电话7', 'upload/shangpinxinxi_fuzhuangtupian7.jpg,upload/shangpinxinxi_fuzhuangtupian8.jpg,upload/shangpinxinxi_fuzhuangtupian1.jpg', '颜色7', '尺寸7', 7, 10, '服装详情7', '2025-01-02');
INSERT INTO `shangpinxinxi` VALUES (8, '2025-01-02 14:58:49', '服装编号8', '服装名称8', '商品分类8', '供应商id8', '供应商编号8', '供应商名称8', '联系电话8', 'upload/shangpinxinxi_fuzhuangtupian8.jpg,upload/shangpinxinxi_fuzhuangtupian1.jpg,upload/shangpinxinxi_fuzhuangtupian2.jpg', '颜色8', '尺寸8', 8, 10, '服装详情8', '2025-01-02');
INSERT INTO `shangpinxinxi` VALUES (9, '2025-01-02 15:03:31', '2365', '改良汉服', '汉服', '123', '123456', '万达', '13613945847', 'upload/1735801402261.jpg', '蓝色', '均码', 126, 10, '<p><strong style=\"background-color: rgb(225, 225, 225); color: rgb(19, 39, 73);\">基于SpringBoot的仓库管理系统设计与实现</strong></p>', '2025-01-02');

-- ----------------------------
-- Table structure for storeup
-- ----------------------------
DROP TABLE IF EXISTS `storeup`;
CREATE TABLE `storeup`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `userid` bigint(20) NOT NULL COMMENT '用户id',
  `refid` bigint(20) NULL DEFAULT NULL COMMENT '商品id',
  `tablename` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '表名',
  `name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
  `picture` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '图片',
  `type` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '1' COMMENT '类型(1:收藏,21:赞,22:踩,31:竞拍参与,41:关注)',
  `inteltype` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '推荐类型',
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1680527098506 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '收藏表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of storeup
-- ----------------------------
INSERT INTO `storeup` VALUES (1680185293770, '2023-03-30 22:08:13', 11, 46, 'tejiashangpin', '物价名称6', 'upload/tejiashangpin_fengmian6.jpg', '1', NULL, NULL);
INSERT INTO `storeup` VALUES (1680272986704, '2023-03-31 22:29:46', 11, 41, 'tejiashangpin', '物价名称1', 'upload/tejiashangpin_fengmian1.jpg', '21', NULL, NULL);
INSERT INTO `storeup` VALUES (1680272990514, '2023-03-31 22:29:49', 11, 41, 'tejiashangpin', '物价名称1', 'upload/tejiashangpin_fengmian1.jpg', '1', NULL, NULL);
INSERT INTO `storeup` VALUES (1680527096125, '2023-04-03 21:04:55', 1680527067622, 41, 'tejiashangpin', '物价名称1', 'upload/tejiashangpin_fengmian1.jpg', '21', NULL, NULL);
INSERT INTO `storeup` VALUES (1680527098505, '2023-04-03 21:04:57', 1680527067622, 41, 'tejiashangpin', '物价名称1', 'upload/tejiashangpin_fengmian1.jpg', '1', NULL, NULL);

-- ----------------------------
-- Table structure for tejiashangpin
-- ----------------------------
DROP TABLE IF EXISTS `tejiashangpin`;
CREATE TABLE `tejiashangpin`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `chanpinbianhao` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产品编号',
  `wujiamingcheng` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物价名称',
  `fengmian` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '封面',
  `chanpinfenlei` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产品分类',
  `pinpai` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '品牌',
  `chanpincanshu` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产品参数',
  `shangpinyuanjia` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品原价',
  `jiangjiayuanyin` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '降价原因',
  `chanpinxiangqing` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '产品详情',
  `onelimittimes` int(11) NULL DEFAULT NULL COMMENT '单限',
  `alllimittimes` int(11) NULL DEFAULT NULL COMMENT '库存',
  `thumbsupnum` int(11) NULL DEFAULT 0 COMMENT '赞',
  `crazilynum` int(11) NULL DEFAULT 0 COMMENT '踩',
  `price` float NOT NULL COMMENT '价格',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `chanpinbianhao`(`chanpinbianhao`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '特价商品' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tejiashangpin
-- ----------------------------
INSERT INTO `tejiashangpin` VALUES (41, '2023-02-28 18:57:46', '1111111111', '物价名称1', 'upload/tejiashangpin_fengmian1.jpg,upload/tejiashangpin_fengmian2.jpg,upload/tejiashangpin_fengmian3.jpg', '产品分类1', '品牌1', '产品参数1', '商品原价1', '降价原因1', '产品详情1', 1, 96, 3, 1, 99.9);
INSERT INTO `tejiashangpin` VALUES (42, '2023-02-28 18:57:46', '2222222222', '物价名称2', 'upload/tejiashangpin_fengmian2.jpg,upload/tejiashangpin_fengmian3.jpg,upload/tejiashangpin_fengmian4.jpg', '产品分类2', '品牌2', '产品参数2', '商品原价2', '降价原因2', '产品详情2', 2, 99, 2, 2, 99.9);
INSERT INTO `tejiashangpin` VALUES (43, '2023-02-28 18:57:46', '3333333333', '物价名称3', 'upload/tejiashangpin_fengmian3.jpg,upload/tejiashangpin_fengmian4.jpg,upload/tejiashangpin_fengmian5.jpg', '产品分类3', '品牌3', '产品参数3', '商品原价3', '降价原因3', '产品详情3', 3, 99, 3, 3, 99.9);
INSERT INTO `tejiashangpin` VALUES (44, '2023-02-28 18:57:46', '4444444444', '物价名称4', 'upload/tejiashangpin_fengmian4.jpg,upload/tejiashangpin_fengmian5.jpg,upload/tejiashangpin_fengmian6.jpg', '产品分类4', '品牌4', '产品参数4', '商品原价4', '降价原因4', '产品详情4', 4, 99, 4, 4, 99.9);
INSERT INTO `tejiashangpin` VALUES (45, '2023-02-28 18:57:46', '5555555555', '物价名称5', 'upload/tejiashangpin_fengmian5.jpg,upload/tejiashangpin_fengmian6.jpg,upload/tejiashangpin_fengmian7.jpg', '产品分类5', '品牌5', '产品参数5', '商品原价5', '降价原因5', '产品详情5', 5, 99, 5, 5, 99.9);
INSERT INTO `tejiashangpin` VALUES (46, '2023-02-28 18:57:46', '6666666666', '物价名称6', 'upload/tejiashangpin_fengmian6.jpg,upload/tejiashangpin_fengmian7.jpg,upload/tejiashangpin_fengmian8.jpg', '产品分类6', '品牌6', '产品参数6', '商品原价6', '降价原因6', '产品详情6', 6, 99, 6, 6, 99.9);
INSERT INTO `tejiashangpin` VALUES (47, '2023-02-28 18:57:46', '7777777777', '物价名称7', 'upload/tejiashangpin_fengmian7.jpg,upload/tejiashangpin_fengmian8.jpg,upload/tejiashangpin_fengmian9.jpg', '产品分类7', '品牌7', '产品参数7', '商品原价7', '降价原因7', '产品详情7', 7, 99, 7, 7, 99.9);
INSERT INTO `tejiashangpin` VALUES (48, '2023-02-28 18:57:46', '8888888888', '物价名称8', 'upload/tejiashangpin_fengmian8.jpg,upload/tejiashangpin_fengmian9.jpg,upload/tejiashangpin_fengmian10.jpg', '产品分类8', '品牌8', '产品参数8', '商品原价8', '降价原因8', '产品详情8', 8, 99, 8, 8, 99.9);

-- ----------------------------
-- Table structure for token
-- ----------------------------
DROP TABLE IF EXISTS `token`;
CREATE TABLE `token`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `userid` bigint(20) NOT NULL COMMENT '用户id',
  `username` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `tablename` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '表名',
  `role` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色',
  `token` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `expiratedtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '过期时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'token表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of token
-- ----------------------------
INSERT INTO `token` VALUES (1, 11, '用户账号1', 'yonghu', '用户', '2vhsannnqp8i296oupdtaqtiwaz74k50', '2023-03-30 22:07:33', '2023-04-03 21:54:12');
INSERT INTO `token` VALUES (2, 1, 'admin', 'users', '管理员', 'bgj3e92rhx1t38nqdz43nn3p92a12nje', '2023-03-30 22:08:36', '2025-12-12 10:59:11');
INSERT INTO `token` VALUES (3, 1680527067622, '111', 'yonghu', '用户', 'a49iwl1qoy0v31qrmd35jknio3c3sik3', '2023-04-03 21:04:31', '2023-04-03 22:04:32');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `role` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '管理员' COMMENT '角色',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', 'admin', '管理员', '2023-02-28 18:57:46');

-- ----------------------------
-- Table structure for yonghu
-- ----------------------------
DROP TABLE IF EXISTS `yonghu`;
CREATE TABLE `yonghu`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `yonghuzhanghao` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户账号',
  `mima` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `yonghuxingming` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户姓名',
  `touxiang` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '头像',
  `xingbie` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `lianxifangshi` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系方式',
  `money` float NULL DEFAULT 0 COMMENT '余额',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `yonghuzhanghao`(`yonghuzhanghao`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1680527067623 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of yonghu
-- ----------------------------
INSERT INTO `yonghu` VALUES (11, '2023-02-28 18:57:46', '用户账号1', '123456', '用户姓名1', 'upload/yonghu_touxiang1.jpg', '男', '13823888881', 100.1);
INSERT INTO `yonghu` VALUES (12, '2023-02-28 18:57:46', '用户账号2', '123456', '用户姓名2', 'upload/yonghu_touxiang2.jpg', '男', '13823888882', 200);
INSERT INTO `yonghu` VALUES (13, '2023-02-28 18:57:46', '用户账号3', '123456', '用户姓名3', 'upload/yonghu_touxiang3.jpg', '男', '13823888883', 200);
INSERT INTO `yonghu` VALUES (14, '2023-02-28 18:57:46', '用户账号4', '123456', '用户姓名4', 'upload/yonghu_touxiang4.jpg', '男', '13823888884', 200);
INSERT INTO `yonghu` VALUES (15, '2023-02-28 18:57:46', '用户账号5', '123456', '用户姓名5', 'upload/yonghu_touxiang5.jpg', '男', '13823888885', 200);
INSERT INTO `yonghu` VALUES (16, '2023-02-28 18:57:46', '用户账号6', '123456', '用户姓名6', 'upload/yonghu_touxiang6.jpg', '男', '13823888886', 200);
INSERT INTO `yonghu` VALUES (17, '2023-02-28 18:57:46', '用户账号7', '123456', '用户姓名7', 'upload/yonghu_touxiang7.jpg', '男', '13823888887', 200);
INSERT INTO `yonghu` VALUES (18, '2023-02-28 18:57:46', '用户账号8', '123456', '用户姓名8', 'upload/yonghu_touxiang8.jpg', '男', '13823888888', 200);
INSERT INTO `yonghu` VALUES (1680527067622, '2023-04-03 21:04:27', '111', '111', '111', NULL, '男', '13696869546', 5355.2);

-- ----------------------------
-- Table structure for zuixinshangshi
-- ----------------------------
DROP TABLE IF EXISTS `zuixinshangshi`;
CREATE TABLE `zuixinshangshi`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `chanpinbianhao` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产品编号',
  `xinpinmingcheng` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '新品名称',
  `fengmian` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '封面',
  `chanpinfenlei` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产品分类',
  `pinpai` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '品牌',
  `chanpincanshu` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产品参数',
  `chanpinxiangqing` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '产品详情',
  `onelimittimes` int(11) NULL DEFAULT NULL COMMENT '单限',
  `alllimittimes` int(11) NULL DEFAULT NULL COMMENT '库存',
  `thumbsupnum` int(11) NULL DEFAULT 0 COMMENT '赞',
  `crazilynum` int(11) NULL DEFAULT 0 COMMENT '踩',
  `clicktime` datetime NULL DEFAULT NULL COMMENT '最近点击时间',
  `clicknum` int(11) NULL DEFAULT 0 COMMENT '点击次数',
  `price` float NOT NULL COMMENT '价格',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `chanpinbianhao`(`chanpinbianhao`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '最新上市' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of zuixinshangshi
-- ----------------------------
INSERT INTO `zuixinshangshi` VALUES (31, '2023-02-28 18:57:46', '1111111111', '新品名称1', 'upload/zuixinshangshi_fengmian1.jpg,upload/zuixinshangshi_fengmian2.jpg,upload/zuixinshangshi_fengmian3.jpg', '产品分类1', '品牌1', '产品参数1', '产品详情1', 1, 99, 1, 1, '2023-02-28 18:57:46', 1, 99.9);
INSERT INTO `zuixinshangshi` VALUES (32, '2023-02-28 18:57:46', '2222222222', '新品名称2', 'upload/zuixinshangshi_fengmian2.jpg,upload/zuixinshangshi_fengmian3.jpg,upload/zuixinshangshi_fengmian4.jpg', '产品分类2', '品牌2', '产品参数2', '产品详情2', 2, 99, 2, 2, '2023-02-28 18:57:46', 2, 99.9);
INSERT INTO `zuixinshangshi` VALUES (33, '2023-02-28 18:57:46', '3333333333', '新品名称3', 'upload/zuixinshangshi_fengmian3.jpg,upload/zuixinshangshi_fengmian4.jpg,upload/zuixinshangshi_fengmian5.jpg', '产品分类3', '品牌3', '产品参数3', '产品详情3', 3, 99, 3, 3, '2023-02-28 18:57:46', 3, 99.9);
INSERT INTO `zuixinshangshi` VALUES (34, '2023-02-28 18:57:46', '4444444444', '新品名称4', 'upload/zuixinshangshi_fengmian4.jpg,upload/zuixinshangshi_fengmian5.jpg,upload/zuixinshangshi_fengmian6.jpg', '产品分类4', '品牌4', '产品参数4', '产品详情4', 4, 99, 4, 4, '2023-02-28 18:57:46', 4, 99.9);
INSERT INTO `zuixinshangshi` VALUES (35, '2023-02-28 18:57:46', '5555555555', '新品名称5', 'upload/zuixinshangshi_fengmian5.jpg,upload/zuixinshangshi_fengmian6.jpg,upload/zuixinshangshi_fengmian7.jpg', '产品分类5', '品牌5', '产品参数5', '产品详情5', 5, 99, 5, 5, '2023-02-28 18:57:46', 5, 99.9);
INSERT INTO `zuixinshangshi` VALUES (36, '2023-02-28 18:57:46', '6666666666', '新品名称6', 'upload/zuixinshangshi_fengmian6.jpg,upload/zuixinshangshi_fengmian7.jpg,upload/zuixinshangshi_fengmian8.jpg', '产品分类6', '品牌6', '产品参数6', '产品详情6', 6, 99, 6, 6, '2023-02-28 18:57:46', 6, 99.9);
INSERT INTO `zuixinshangshi` VALUES (37, '2023-02-28 18:57:46', '7777777777', '新品名称7', 'upload/zuixinshangshi_fengmian7.jpg,upload/zuixinshangshi_fengmian8.jpg,upload/zuixinshangshi_fengmian9.jpg', '产品分类7', '品牌7', '产品参数7', '产品详情7', 7, 99, 7, 7, '2023-02-28 18:57:46', 7, 99.9);
INSERT INTO `zuixinshangshi` VALUES (38, '2023-02-28 18:57:46', '8888888888', '新品名称8', 'upload/zuixinshangshi_fengmian8.jpg,upload/zuixinshangshi_fengmian9.jpg,upload/zuixinshangshi_fengmian10.jpg', '产品分类8', '品牌8', '产品参数8', '产品详情8', 8, 99, 8, 8, '2023-02-28 18:57:46', 8, 99.9);

SET FOREIGN_KEY_CHECKS = 1;
