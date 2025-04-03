# 在线购物系统

本项目是一个功能完善的在线购物系统，采用Spring Boot框架开发。

## 已实现功能

### 用户模块
- 用户注册、登录和身份验证（基于JWT）
- 个人信息管理
- 收货地址管理

### 商品模块
- 商品分类管理
- 商品信息管理（增删改查）
- 商品搜索
- 热门商品和新品推荐

### 购物车模块
- 购物车管理（添加、移除、更新商品）
- 购物车商品选择和批量操作

### 订单模块
- 订单创建和管理
- 订单状态跟踪（待付款、待发货、待收货、已完成等）
- 订单支付处理
- 退款申请和处理

### 评论系统
- 商品评价（文字、评分、图片）
- 商家回复
- 管理员审核和管理评论
- 商品评分计算

### 收藏功能
- 商品收藏和取消收藏
- 收藏列表管理

### 搜索历史功能
- 记录用户搜索历史
- 基于搜索历史的商品推荐
- 热门搜索词统计

### 公告功能
- 系统公告管理
- 用户阅读状态追踪
- 未读公告提醒

### 店铺管理
- 店铺信息管理
- 商家入驻申请和审核
- 店铺状态控制

### 客服服务功能
- 在线客服会话
- 消息收发管理
- 会话评价系统
- 客服满意度统计

## 技术栈
- 后端：Spring Boot, Spring Security, MyBatis
- 数据库：MySQL
- 认证：JWT (JSON Web Token)
- 工具：Fastjson, Lombok

## 数据库设计
系统包含以下主要数据表：
- users: 用户信息
- stores: 店铺信息
- categories: 商品分类
- products: 商品信息
- carts: 购物车
- orders: 订单信息
- reviews: 商品评论
- favorites: 收藏记录
- search_history: 搜索历史
- announcements: 系统公告
- customer_service_sessions: 客服会话
- customer_service_messages: 客服消息

## 项目结构
- entity: 实体类
- mapper: MyBatis Mapper接口
- service: 服务接口和实现
- dto: 数据传输对象
- controller: API控制器
- exception: 自定义异常
- utils: 工具类
- config: 配置类 