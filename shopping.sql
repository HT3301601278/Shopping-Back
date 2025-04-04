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

 Date: 04/04/2025 13:03:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键',
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '手机号码',
  `role` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户角色',
  `status` int(11) NOT NULL COMMENT '用户状态：0-禁用，1-正常',
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户头像URL',
  `addresses` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '用户地址JSON',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_du5v5sr43g5bfnji4vb8hg5s3`(`phone`) USING BTREE,
  UNIQUE INDEX `UK_r43af9ap4edm43mmtq01oddj6`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for addresses
-- ----------------------------
DROP TABLE IF EXISTS `addresses`;
CREATE TABLE `addresses`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '地址ID，主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID，外键',
  `receiver_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '收件人姓名',
  `receiver_phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '收件人电话',
  `province` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '省份',
  `city` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '城市',
  `district` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '区/县',
  `detail_address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '详细地址',
  `is_default` bit(1) NOT NULL COMMENT '是否默认地址：0-否，1-是',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '收货地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for stores
-- ----------------------------
DROP TABLE IF EXISTS `stores`;
CREATE TABLE `stores`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '店铺ID，主键',
  `user_id` bigint(20) NOT NULL COMMENT '店主用户ID，外键',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '店铺名称',
  `logo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺LOGO图片URL',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺描述',
  `contact_info` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '联系信息JSON',
  `license` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '营业执照URL',
  `status` int(11) NOT NULL COMMENT '店铺状态：0-关闭，1-营业',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '店铺表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID，主键',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint(20) NULL DEFAULT NULL COMMENT '父分类ID',
  `level` int(11) NOT NULL COMMENT '分类层级',
  `sort_order` int(11) NULL DEFAULT NULL COMMENT '排序值',
  `status` int(11) NOT NULL COMMENT '状态：0-禁用，1-启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID，主键',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '商品名称',
  `store_id` bigint(20) NOT NULL COMMENT '店铺ID，外键',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID，外键',
  `price` decimal(10, 2) NOT NULL COMMENT '商品价格',
  `stock` int(11) NOT NULL COMMENT '库存数量',
  `sales` int(11) NOT NULL COMMENT '销量',
  `status` int(11) NOT NULL COMMENT '状态：0-下架，1-上架',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品简述',
  `detail` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '商品详情',
  `specifications` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '规格信息JSON',
  `images` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '商品图片URL列表JSON',
  `rating` double NULL DEFAULT NULL COMMENT '商品评分',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for carts
-- ----------------------------
DROP TABLE IF EXISTS `carts`;
CREATE TABLE `carts`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '购物车ID，主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID，外键',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID，外键',
  `quantity` int(11) NOT NULL COMMENT '商品数量',
  `selected` bit(1) NOT NULL COMMENT '是否选中：0-未选中，1-已选中',
  `spec_info` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '商品规格信息JSON',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '购物车表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID，主键',
  `order_no` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID，外键',
  `store_id` bigint(20) NOT NULL COMMENT '店铺ID，外键',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `status` int(11) NOT NULL COMMENT '订单状态：0-待付款，1-待发货，2-待收货，3-已完成，4-已取消',
  `address_info` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '收货地址信息JSON',
  `items` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单商品明细JSON',
  `payment_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付方式',
  `payment_time` datetime(6) NULL DEFAULT NULL COMMENT '支付时间',
  `shipping_time` datetime(6) NULL DEFAULT NULL COMMENT '发货时间',
  `refund_status` int(11) NULL DEFAULT NULL COMMENT '退款状态：0-无退款，1-申请中，2-已退款，3-已拒绝',
  `refund_reason` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '退款原因',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_g8pohnngqi5x1nask7nff2u7w`(`order_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for reviews
-- ----------------------------
DROP TABLE IF EXISTS `reviews`;
CREATE TABLE `reviews`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评价ID，主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID，外键',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID，外键',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID，外键',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '评价内容',
  `rating` int(11) NOT NULL COMMENT '评分：1-5星',
  `images` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '评价图片URL列表JSON',
  `reply` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商家回复',
  `is_top` bit(1) NOT NULL COMMENT '是否置顶：0-否，1-是',
  `status` int(11) NOT NULL COMMENT '状态：0-隐藏，1-显示',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for customer_service_sessions
-- ----------------------------
DROP TABLE IF EXISTS `customer_service_sessions`;
CREATE TABLE `customer_service_sessions`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '会话ID，主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID，外键',
  `store_id` bigint(20) NOT NULL COMMENT '店铺ID，外键',
  `status` int(11) NOT NULL COMMENT '会话状态：0-关闭，1-进行中',
  `start_time` datetime(6) NOT NULL COMMENT '开始时间',
  `end_time` datetime(6) NULL DEFAULT NULL COMMENT '结束时间',
  `evaluation` int(11) NULL DEFAULT NULL COMMENT '用户评价：1-5星',
  `complaint_status` int(11) NULL DEFAULT NULL COMMENT '投诉状态：0-无投诉，1-已投诉，2-已处理',
  `complaint_result` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '投诉处理结果',
  `is_penalty` bit(1) NULL DEFAULT NULL COMMENT '是否处罚：0-否，1-是',
  `penalty_content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '处罚内容',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '客服会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for customer_service_messages
-- ----------------------------
DROP TABLE IF EXISTS `customer_service_messages`;
CREATE TABLE `customer_service_messages`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息ID，主键',
  `session_id` bigint(20) NOT NULL COMMENT '会话ID，外键',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID，外键',
  `store_id` bigint(20) NOT NULL COMMENT '店铺ID，外键',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息内容',
  `content_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '内容类型：text-文本，image-图片，video-视频',
  `from_type` int(11) NOT NULL COMMENT '发送方类型：0-用户，1-客服',
  `read_status` bit(1) NOT NULL COMMENT '阅读状态：0-未读，1-已读',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '客服消息表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
