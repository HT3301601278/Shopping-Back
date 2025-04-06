/*
 Navicat Premium Dump SQL

 Source Server         : MySQL5.7
 Source Server Type    : MySQL
 Source Server Version : 50744 (5.7.44)
 Source Host           : localhost:3305
 Source Schema         : shopping

 Target Server Type    : MySQL
 Target Server Version : 50744 (5.7.44)
 File Encoding         : 65001

 Date: 06/04/2025 19:33:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for addresses
-- ----------------------------
DROP TABLE IF EXISTS `addresses`;
CREATE TABLE `addresses`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '地址ID，唯一标识',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID，关联users表',
  `receiver_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '收件人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '收件人联系电话',
  `province` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '省份名称',
  `city` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '城市名称',
  `district` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '区/县名称',
  `detail_address` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '详细地址，如街道、门牌号等',
  `is_default` bit(1) NOT NULL COMMENT '是否为默认地址：0-否，1-是',
  `create_time` datetime NULL DEFAULT NULL COMMENT '地址创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '地址更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '收货地址表，存储用户的收货地址信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for announcements
-- ----------------------------
DROP TABLE IF EXISTS `announcements`;
CREATE TABLE `announcements`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '公告ID，唯一标识',
  `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '公告内容，支持富文本',
  `publisher_id` bigint(20) NOT NULL COMMENT '发布者ID，关联users表',
  `status` int(11) NOT NULL COMMENT '公告状态：0-隐藏，1-显示',
  `read_users` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '已读用户ID列表，JSON格式存储',
  `create_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '公告表，存储系统公告信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for carts
-- ----------------------------
DROP TABLE IF EXISTS `carts`;
CREATE TABLE `carts`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '购物车项ID，唯一标识',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID，关联users表',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID，关联products表',
  `quantity` int(5) NOT NULL COMMENT '选购数量',
  `selected` bit(1) NOT NULL COMMENT '是否勾选：0-未勾选，1-已勾选',
  `spec_info` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '选择的商品规格信息，JSON格式存储',
  `create_time` datetime NULL DEFAULT NULL COMMENT '加入购物车时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '购物车表，存储用户的购物车信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID，唯一标识',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint(20) NULL DEFAULT NULL COMMENT '父分类ID，顶级分类为0或null',
  `level` int(11) NOT NULL COMMENT '分类层级：1-一级分类，2-二级分类，3-三级分类',
  `status` int(11) NOT NULL COMMENT '分类状态：0-禁用，1-启用',
  `sort_order` int(11) NULL DEFAULT NULL COMMENT '同级分类的排序值，值越小越靠前',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品分类表，存储商品的分类体系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for customer_service_messages
-- ----------------------------
DROP TABLE IF EXISTS `customer_service_messages`;
CREATE TABLE `customer_service_messages`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息ID，唯一标识',
  `session_id` bigint(20) NOT NULL COMMENT '所属会话ID，关联customer_service_sessions表',
  `user_id` bigint(20) NOT NULL COMMENT '相关用户ID，关联users表',
  `store_id` bigint(20) NOT NULL COMMENT '相关店铺ID，关联stores表',
  `from_type` int(11) NOT NULL COMMENT '发送方类型：0-用户，1-商家/客服',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息内容',
  `content_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '内容类型：text-文本，image-图片，file-文件',
  `read_status` bit(1) NOT NULL COMMENT '读取状态：0-未读，1-已读',
  `create_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '客服消息表，记录用户与客服的聊天记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for customer_service_sessions
-- ----------------------------
DROP TABLE IF EXISTS `customer_service_sessions`;
CREATE TABLE `customer_service_sessions`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '会话ID，唯一标识',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID，关联users表',
  `store_id` bigint(20) NOT NULL COMMENT '店铺ID，关联stores表',
  `status` int(11) NOT NULL COMMENT '会话状态：0-进行中，1-已结束',
  `start_time` datetime(6) NOT NULL COMMENT '会话开始时间',
  `end_time` datetime(6) NULL DEFAULT NULL COMMENT '会话结束时间',
  `evaluation` int(11) NULL DEFAULT NULL COMMENT '用户服务评价：1-5星',
  `complaint_status` int(11) NULL DEFAULT NULL COMMENT '投诉状态：0-待处理，1-已处理，2-已驳回',
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户评价备注',
  `complaint_result` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '投诉处理结果说明',
  `is_penalty` bit(1) NULL DEFAULT NULL COMMENT '是否处罚商家：0-否，1-是',
  `penalty_content` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '处罚内容详情',
  `create_time` datetime NULL DEFAULT NULL COMMENT '记录创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '记录更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '客服会话表，记录用户与客服的会话信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for favorites
-- ----------------------------
DROP TABLE IF EXISTS `favorites`;
CREATE TABLE `favorites`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID，唯一标识',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID，关联users表',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID，关联products表',
  `create_time` datetime NULL DEFAULT NULL COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '收藏表，存储用户收藏的商品' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID，唯一标识',
  `order_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单编号，系统生成，全局唯一',
  `user_id` bigint(20) NOT NULL COMMENT '下单用户ID，关联users表',
  `store_id` bigint(20) NOT NULL COMMENT '店铺ID，关联stores表',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额，单位元',
  `status` int(11) NOT NULL COMMENT '订单状态：0-待付款，1-待发货，2-待收货，3-已完成，4-已取消',
  `address_info` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '收货地址信息，JSON格式存储',
  `items` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单商品明细，JSON格式存储',
  `payment_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付方式：支付宝、微信支付等',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `shipping_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
  `refund_status` int(11) NULL DEFAULT NULL COMMENT '退款状态：0-无退款，1-申请中，2-已退款，3-已拒绝',
  `refund_reason` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '退款原因说明',
  `create_time` datetime NULL DEFAULT NULL COMMENT '订单创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '订单更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_order_no`(`order_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单表，存储用户订单信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID，唯一标识',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '商品名称',
  `store_id` bigint(20) NOT NULL COMMENT '所属店铺ID，关联stores表',
  `category_id` bigint(20) NOT NULL COMMENT '所属分类ID，关联categories表',
  `price` decimal(10, 2) NOT NULL COMMENT '商品售价，单位元',
  `status` int(11) NOT NULL COMMENT '商品状态：0-下架，1-上架',
  `stock` int(8) NOT NULL COMMENT '库存数量',
  `description` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品简短描述',
  `detail` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '商品详细介绍，支持富文本',
  `images` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '商品图片URL列表，JSON格式存储',
  `specifications` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '商品规格信息，JSON格式存储',
  `sales` int(8) NOT NULL COMMENT '商品销量',
  `rating` double NULL DEFAULT NULL COMMENT '商品评分，1-5星',
  `create_time` datetime NULL DEFAULT NULL COMMENT '商品创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '商品信息更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品表，存储商品的基本信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for reviews
-- ----------------------------
DROP TABLE IF EXISTS `reviews`;
CREATE TABLE `reviews`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评价ID，唯一标识',
  `user_id` bigint(20) NOT NULL COMMENT '评价用户ID，关联users表',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID，关联products表',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID，关联orders表',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '评价文字内容',
  `rating` int(11) NOT NULL COMMENT '评分等级：1-5星',
  `images` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '评价图片URL列表，JSON格式存储',
  `status` int(11) NOT NULL COMMENT '评价状态：0-审核中，1-显示，2-隐藏',
  `reply` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商家回复内容',
  `is_top` bit(1) NOT NULL COMMENT '是否置顶：0-否，1-是',
  `create_time` datetime NULL DEFAULT NULL COMMENT '评价创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '评价更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品评价表，存储用户对商品的评价信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for search_histories
-- ----------------------------
DROP TABLE IF EXISTS `search_histories`;
CREATE TABLE `search_histories`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '搜索历史ID，唯一标识',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID，关联users表',
  `keyword` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '搜索关键词',
  `result_count` int(8) NULL DEFAULT NULL COMMENT '搜索结果数量',
  `create_time` datetime NULL DEFAULT NULL COMMENT '搜索时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '搜索历史表，记录用户的搜索记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for stores
-- ----------------------------
DROP TABLE IF EXISTS `stores`;
CREATE TABLE `stores`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '店铺ID，唯一标识',
  `user_id` bigint(20) NOT NULL COMMENT '店主用户ID，关联users表',
  `status` int(11) NOT NULL COMMENT '店铺状态：0-审核中，1-正常，2-关闭',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '店铺名称',
  `logo` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺LOGO图片URL',
  `description` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺简介描述',
  `contact_info` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '联系方式，JSON格式存储（电话、邮箱、微信等）',
  `license` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '营业执照图片URL',
  `create_time` datetime NULL DEFAULT NULL COMMENT '店铺创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '店铺信息更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '店铺表，存储商家店铺的基本信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID，系统自动生成的唯一标识',
  `status` int(11) NOT NULL COMMENT '账号状态：0-禁用，1-启用',
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名，用于登录和显示，唯一',
  `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '手机号码，用于账号验证和找回密码，唯一',
  `role` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户角色：ROLE_ADMIN-管理员，ROLE_MERCHANT-商家，ROLE_USER-普通用户',
  `avatar` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户头像URL地址',
  `addresses` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '用户常用地址列表，以JSON格式存储',
  `create_time` datetime NULL DEFAULT NULL COMMENT '账号创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '账号最后更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_username`(`username`) USING BTREE,
  UNIQUE INDEX `UK_phone`(`phone`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户信息表，存储系统用户的基本信息' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
