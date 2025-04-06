package example.shopping.config;

import example.shopping.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 数据初始化类
 */
@Configuration
public class DataInitializer {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    /**
     * 应用启动时自动创建测试数据
     */
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            // transactionTemplate.execute(status -> {
            //     try {
            //         // 初始化用户（需要最先创建）
            //         createAdminIfNotExists();
            //         createMerchantIfNotExists();
            //         createUserIfNotExists();

            //         // 在用户创建完成后，创建相关表
            //         createCategoriesIfNotExists();
            //         createStoresIfNotExists();
            //         createProductsIfNotExists();
            //         createOrdersIfNotExists();
            //         createReviewsIfNotExists();

            //         try {
            //             // 单独处理公告和地址，避免因某个实体未映射导致整个事务回滚
            //             createAnnouncementsIfNotExists();
            //         } catch (Exception e) {
            //             System.out.println("创建公告时发生错误，可能是实体未映射: " + e.getMessage());
            //         }

            //         try {
            //             createAddressesIfNotExists();
            //         } catch (Exception e) {
            //             System.out.println("创建地址时发生错误: " + e.getMessage());
            //         }

            //         // 创建购物车、收藏、搜索历史、客服会话和客服消息数据
            //         try {
            //             createCartsIfNotExists();
            //         } catch (Exception e) {
            //             System.out.println("创建购物车数据时发生错误: " + e.getMessage());
            //         }

            //         try {
            //             createFavoritesIfNotExists();
            //         } catch (Exception e) {
            //             System.out.println("创建收藏数据时发生错误: " + e.getMessage());
            //         }

            //         try {
            //             createSearchHistoriesIfNotExists();
            //         } catch (Exception e) {
            //             System.out.println("创建搜索历史数据时发生错误: " + e.getMessage());
            //         }

            //         try {
            //             createCustomerServiceSessionsIfNotExists();
            //         } catch (Exception e) {
            //             System.out.println("创建客服会话数据时发生错误: " + e.getMessage());
            //         }

            //     } catch (Exception e) {
            //         System.out.println("初始化数据时发生错误: " + e.getMessage());
            //         e.printStackTrace();
            //     }
            //     return null;
            // });
            // 暂时取消数据初始化
            System.out.println("数据初始化已被禁用");
        };
    }

    /**
     * 创建管理员账户（如果不存在）
     */
    public void createAdminIfNotExists() {
        // 查询是否已存在管理员账户
        Long count = (Long) entityManager
                .createQuery("SELECT COUNT(u) FROM User u WHERE u.role = 'ROLE_ADMIN'")
                .getSingleResult();

        // 如果不存在，则创建
        if (count == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setPhone("13500000000");
            admin.setRole("ROLE_ADMIN");
            admin.setStatus(1);
            admin.setAvatar("https://example.com/avatars/admin.jpg");

            entityManager.persist(admin);
            System.out.println("管理员账户已创建，用户名：admin，密码：admin123");
        }
    }

    /**
     * 创建商家账户（如果不存在）
     */
    public void createMerchantIfNotExists() {
        // 查询是否已存在商家账户
        Long count = (Long) entityManager
                .createQuery("SELECT COUNT(u) FROM User u WHERE u.role = 'ROLE_MERCHANT'")
                .getSingleResult();

        // 如果不存在，则创建
        if (count == 0) {
            // 创建3个商家账户
            for (int i = 1; i <= 3; i++) {
                User merchant = new User();
                merchant.setUsername("merchant" + i);
                merchant.setPassword("merchant123");
                merchant.setPhone("1360000000" + i);
                merchant.setRole("ROLE_MERCHANT");
                merchant.setStatus(1);
                merchant.setAvatar("https://example.com/avatars/merchant" + i + ".jpg");

                entityManager.persist(merchant);
                entityManager.flush(); // 确保用户被立即持久化到数据库
                System.out.println("商家账户已创建，用户名：merchant" + i + "，密码：merchant123");
            }
        }
    }

    /**
     * 创建普通用户账户（如果不存在）
     */
    public void createUserIfNotExists() {
        // 查询是否已存在普通用户账户
        Long count = (Long) entityManager
                .createQuery("SELECT COUNT(u) FROM User u WHERE u.role = 'ROLE_USER'")
                .getSingleResult();

        // 如果不存在，则创建
        if (count == 0) {
            // 创建5个普通用户
            for (int i = 1; i <= 5; i++) {
                User user = new User();
                user.setUsername("user" + i);
                user.setPassword("user123");
                user.setPhone("1370000000" + i);
                user.setRole("ROLE_USER");
                user.setStatus(1);
                user.setAvatar("https://example.com/avatars/user" + i + ".jpg");

                entityManager.persist(user);
                System.out.println("普通用户账户已创建，用户名：user" + i + "，密码：user123");
            }
            entityManager.flush(); // 确保用户被立即持久化到数据库
        }
    }

    /**
     * 创建商品分类（如果不存在）
     */
    public void createCategoriesIfNotExists() {
        // 查询是否已存在分类
        Long count = (Long) entityManager
                .createQuery("SELECT COUNT(c) FROM Category c")
                .getSingleResult();

        // 如果不存在，则创建
        if (count == 0) {
            // 创建一级分类
            String[] mainCategories = {"电子产品", "服装", "食品", "家居", "图书"};
            for (int i = 0; i < mainCategories.length; i++) {
                Category category = new Category();
                category.setName(mainCategories[i]);
                category.setParentId(0L);
                category.setLevel(1);
                category.setStatus(1);
                category.setSortOrder(i);

                entityManager.persist(category);

                // 为每个一级分类创建子分类
                if (i == 0) { // 电子产品子分类
                    createSubCategory(category.getId(), new String[]{"手机", "电脑", "相机", "耳机"});
                } else if (i == 1) { // 服装子分类
                    createSubCategory(category.getId(), new String[]{"男装", "女装", "童装", "鞋子"});
                } else if (i == 2) { // 食品子分类
                    createSubCategory(category.getId(), new String[]{"零食", "饮料", "生鲜", "干货"});
                } else if (i == 3) { // 家居子分类
                    createSubCategory(category.getId(), new String[]{"家具", "厨具", "灯具", "床上用品"});
                } else if (i == 4) { // 图书子分类
                    createSubCategory(category.getId(), new String[]{"小说", "教材", "漫画", "杂志"});
                }
            }

            System.out.println("商品分类已创建");
        }
    }

    /**
     * 创建子分类
     */
    private void createSubCategory(Long parentId, String[] subCategoryNames) {
        for (int i = 0; i < subCategoryNames.length; i++) {
            Category subCategory = new Category();
            subCategory.setName(subCategoryNames[i]);
            subCategory.setParentId(parentId);
            subCategory.setLevel(2);
            subCategory.setStatus(1);
            subCategory.setSortOrder(i);

            entityManager.persist(subCategory);
        }
    }

    /**
     * 创建店铺（如果不存在）
     */
    public void createStoresIfNotExists() {
        // 查询是否已存在店铺
        Long count = (Long) entityManager
                .createQuery("SELECT COUNT(s) FROM Store s")
                .getSingleResult();

        // 如果不存在，则创建
        if (count == 0) {
            // 获取所有商家用户
            @SuppressWarnings("unchecked")
            List<User> merchants = entityManager
                    .createQuery("SELECT u FROM User u WHERE u.role = 'ROLE_MERCHANT'")
                    .getResultList();

            if (merchants.isEmpty()) {
                System.out.println("没有找到商家用户，无法创建店铺");
                return;
            }

            // 为每个商家创建店铺
            for (int i = 0; i < merchants.size(); i++) {
                User merchant = merchants.get(i);

                Store store = new Store();
                store.setUserId(merchant.getId());
                store.setName("优质商城" + (i+1) + "号店");
                store.setLogo("https://example.com/store_logos/logo" + (i+1) + ".jpg");
                store.setDescription("这是一家专注于提供优质商品的商店，已经经营多年，深受顾客好评。");
                store.setContactInfo("{\"phone\":\"" + merchant.getPhone() + "\",\"email\":\"store" + (i+1) + "@example.com\"}");
                store.setLicense("https://example.com/licenses/license" + (i+1) + ".jpg");
                store.setStatus(1); // 正常营业

                entityManager.persist(store);
                System.out.println("店铺已创建：" + store.getName());
            }
            entityManager.flush(); // 确保店铺被立即持久化到数据库
        }
    }

    /**
     * 创建商品（如果不存在）
     */
    public void createProductsIfNotExists() {
        // 查询是否已存在商品
        Long count = (Long) entityManager
                .createQuery("SELECT COUNT(p) FROM Product p")
                .getSingleResult();

        // 如果不存在，则创建
        if (count == 0) {
            // 获取所有店铺
            @SuppressWarnings("unchecked")
            java.util.List<Store> stores = entityManager
                    .createQuery("SELECT s FROM Store s")
                    .getResultList();

            if (stores.isEmpty()) {
                System.out.println("没有找到店铺，无法创建商品");
                return;
            }

            // 获取所有二级分类
            @SuppressWarnings("unchecked")
            java.util.List<Category> categories = entityManager
                    .createQuery("SELECT c FROM Category c WHERE c.level = 2")
                    .getResultList();

            if (categories.isEmpty()) {
                System.out.println("没有找到分类，无法创建商品");
                return;
            }

            // 为每个店铺创建商品
            for (Store store : stores) {
                for (int i = 1; i <= 5; i++) {
                    // 随机选择一个分类
                    Category category = categories.get((int) (Math.random() * categories.size()));

                    Product product = new Product();
                    product.setName("优质商品" + store.getId() + "-" + i);
                    product.setStoreId(store.getId());
                    product.setCategoryId(category.getId());
                    product.setPrice(new BigDecimal(String.format("%.2f", 50 + Math.random() * 1000)));
                    product.setStock(100 + (int)(Math.random() * 900));
                    product.setDescription(category.getName() + "类别下的优质商品，性价比高，质量好。");

                    // 设置商品图片（多张）
                    product.setImages("[\"https://example.com/products/img1.jpg\",\"https://example.com/products/img2.jpg\"]");

                    // 设置商品详情
                    product.setDetail("<h2>商品详情</h2><p>这是商品的详细描述，包含商品的各种信息。</p>");

                    // 设置商品规格
                    product.setSpecifications("{\"颜色\":[\"红色\",\"蓝色\",\"黑色\"],\"尺寸\":[\"S\",\"M\",\"L\",\"XL\"]}");

                    product.setStatus(1);  // 上架状态
                    product.setSales((int)(Math.random() * 100));
                    product.setRating(4.0 + Math.random());  // 4-5之间的评分

                    entityManager.persist(product);
                }
            }
            entityManager.flush(); // 确保商品被立即持久化到数据库
            System.out.println("商品数据已创建");
        }
    }

    /**
     * 创建订单（如果不存在）
     */
    public void createOrdersIfNotExists() {
        // 查询是否已存在订单
        Long count = (Long) entityManager
                .createQuery("SELECT COUNT(o) FROM Order o")
                .getSingleResult();

        // 如果不存在，则创建
        if (count == 0) {
            // 获取所有普通用户
            @SuppressWarnings("unchecked")
            java.util.List<User> users = entityManager
                    .createQuery("SELECT u FROM User u WHERE u.role = 'ROLE_USER'")
                    .getResultList();

            if (users.isEmpty()) {
                System.out.println("没有找到普通用户，无法创建订单");
                return;
            }

            // 获取所有商品
            @SuppressWarnings("unchecked")
            java.util.List<Product> products = entityManager
                    .createQuery("SELECT p FROM Product p")
                    .getResultList();

            if (products.isEmpty()) {
                System.out.println("没有找到商品，无法创建订单");
                return;
            }

            // 为每个用户创建订单
            for (User user : users) {
                // 每个用户创建2个订单
                for (int i = 0; i < 2; i++) {
                    try {
                        // 随机选择一个商品
                        Product product = products.get((int) (Math.random() * products.size()));

                        // 获取商品所属的店铺
                        Store store = entityManager.find(Store.class, product.getStoreId());
                        if (store == null) {
                            System.out.println("找不到商品对应的店铺，跳过此订单创建");
                            continue;
                        }

                        Order order = new Order();
                        order.setOrderNo("ORD" + System.currentTimeMillis() + (int)(Math.random() * 1000));
                        order.setUserId(user.getId());
                        order.setStoreId(store.getId());

                        // 设置订单项（可能包含多个商品）
                        int quantity = 1 + (int)(Math.random() * 3);
                        BigDecimal itemAmount = product.getPrice().multiply(new BigDecimal(quantity));

                        order.setItems("[{\"productId\":" + product.getId() +
                                      ",\"productName\":\"" + product.getName() +
                                      "\",\"price\":" + product.getPrice() +
                                      ",\"quantity\":" + quantity +
                                      ",\"amount\":" + itemAmount + "}]");

                        order.setTotalAmount(itemAmount);

                        // 设置收货地址
                        order.setAddressInfo("{\"name\":\"收货人" + user.getId() +
                                            "\",\"phone\":\"" + user.getPhone() +
                                            "\",\"address\":\"XX省XX市XX区XX街道XX号\"}");

                        // 随机设置支付方式
                        String[] paymentTypes = {"支付宝", "微信", "银行卡"};
                        order.setPaymentType(paymentTypes[(int)(Math.random() * paymentTypes.length)]);

                        // 随机设置订单状态
                        int status = (int)(Math.random() * 5); // 0-未支付, 1-已支付, 2-已发货, 3-已完成, 4-已取消
                        order.setStatus(status);

                        // 根据状态设置时间
                        if (status >= 1) { // 已支付及以上状态
                            order.setPaymentTime(new Date(System.currentTimeMillis() - (long)(Math.random() * 86400000))); // 1天内的随机时间
                        }

                        if (status >= 2) { // 已发货及以上状态
                            order.setShippingTime(new Date(System.currentTimeMillis() - (long)(Math.random() * 43200000))); // 12小时内的随机时间
                        }

                        entityManager.persist(order);
                    } catch (Exception e) {
                        System.out.println("创建订单时发生错误: " + e.getMessage());
                    }
                }
            }
            entityManager.flush(); // 确保订单被立即持久化到数据库
            System.out.println("订单数据已创建");
        }
    }

    /**
     * 创建商品评价（如果不存在）
     */
    public void createReviewsIfNotExists() {
        // 查询是否已存在评价
        Long count = (Long) entityManager
                .createQuery("SELECT COUNT(r) FROM Review r")
                .getSingleResult();

        // 如果不存在，则创建
        if (count == 0) {
            // 获取所有已完成的订单
            @SuppressWarnings("unchecked")
            java.util.List<Order> orders = entityManager
                    .createQuery("SELECT o FROM Order o WHERE o.status = 3")
                    .getResultList();

            if (orders.isEmpty()) {
                System.out.println("没有找到已完成的订单，无法创建评价");
                return;
            }

            // 为每个已完成订单创建评价
            for (Order order : orders) {
                try {
                    // 解析订单项
                    String itemsStr = order.getItems();
                    // 简单解析，实际项目中可能需要更复杂的JSON解析
                    int startIdx = itemsStr.indexOf("\"productId\":") + 12;
                    int endIdx = itemsStr.indexOf(",", startIdx);
                    if (startIdx < 12 || endIdx < 0) {
                        System.out.println("订单项格式异常，跳过此评价创建");
                        continue;
                    }

                    Long productId = Long.parseLong(itemsStr.substring(startIdx, endIdx));
                    // 检查商品是否存在
                    Product product = entityManager.find(Product.class, productId);
                    if (product == null) {
                        System.out.println("找不到对应的商品，跳过此评价创建");
                        continue;
                    }

                    Review review = new Review();
                    review.setUserId(order.getUserId());
                    review.setProductId(productId);
                    review.setOrderId(order.getId());

                    // 随机设置评分(1-5)
                    int rating = 3 + (int)(Math.random() * 3); // 偏向好评
                    review.setRating(rating);

                    // 根据评分设置评价内容
                    String[] goodComments = {
                        "商品质量非常好，物流也很快，很满意的一次购物体验！",
                        "卖家服务态度好，商品和描述一致，会再次购买！",
                        "包装很精美，商品质量出乎意料的好，推荐购买！",
                        "性价比很高的商品，值得购买！"
                    };

                    String[] normalComments = {
                        "商品还可以，基本符合预期。",
                        "质量一般，但价格还算合理。",
                        "送货速度还行，商品质量中规中矩。"
                    };

                    if (rating >= 4) {
                        review.setContent(goodComments[(int)(Math.random() * goodComments.length)]);
                    } else {
                        review.setContent(normalComments[(int)(Math.random() * normalComments.length)]);
                    }

                    // 设置图片（可能有多张）
                    if (Math.random() > 0.5) { // 50%概率上传图片
                        review.setImages("[\"https://example.com/reviews/img1.jpg\",\"https://example.com/reviews/img2.jpg\"]");
                    }

                    review.setStatus(1); // 正常状态

                    entityManager.persist(review);

                    // 更新商品的评分
                    if (product != null) {
                        // 简单计算：原评分*0.8 + 新评分*0.2
                        double newRating = (product.getRating() * 0.8) + (rating * 0.2);
                        product.setRating(Math.round(newRating * 10) / 10.0); // 保留一位小数
                        entityManager.merge(product);
                    }
                } catch (Exception e) {
                    System.out.println("创建评价时发生错误: " + e.getMessage());
                }
            }

            System.out.println("商品评价数据已创建");
        }
    }

    /**
     * 创建公告（如果不存在）
     */
    public void createAnnouncementsIfNotExists() {
        // 查询是否已存在公告
        Long count = (Long) entityManager
                .createQuery("SELECT COUNT(a) FROM Announcement a")
                .getSingleResult();

        // 如果不存在，则创建
        if (count == 0) {
            String[] titles = {
                "系统维护通知",
                "新功能上线公告",
                "618购物节活动公告",
                "双11购物节即将到来",
                "关于提高商品质量的通知"
            };

            String[] contents = {
                "系统将于2023年6月15日凌晨2:00-4:00进行例行维护，届时系统将暂停使用，请广大用户提前做好准备。",
                "我们的APP新增了直播购物功能，现在您可以通过直播间实时了解商品信息，与卖家互动，享受更便捷的购物体验。",
                "618购物节期间，平台将推出多重优惠活动，包括满减、折扣、抢购等，敬请期待！",
                "双11购物节即将到来，今年我们准备了更多优惠和惊喜，敬请关注平台公告获取最新信息。",
                "为提高平台商品质量，我们将对所有商家的商品进行严格审核，确保用户能够购买到高质量的商品。"
            };

            try {
                // 获取管理员用户
                @SuppressWarnings("unchecked")
                List<User> admins = entityManager
                        .createQuery("SELECT u FROM User u WHERE u.role = 'ROLE_ADMIN'")
                        .getResultList();

                if (admins.isEmpty()) {
                    System.out.println("没有找到管理员用户，无法创建公告");
                    return;
                }

                User admin = admins.get(0);

                for (int i = 0; i < titles.length; i++) {
                    Announcement announcement = new Announcement();
                    announcement.setTitle(titles[i]);
                    announcement.setContent(contents[i]);
                    announcement.setPublisherId(admin.getId());

                    // 设置状态
                    announcement.setStatus(1);

                    // 设置创建时间（过去30天内的随机时间）
                    long now = System.currentTimeMillis();
                    long thirtyDaysAgo = now - 30L * 24 * 60 * 60 * 1000;
                    long randomTime = thirtyDaysAgo + (long)(Math.random() * (now - thirtyDaysAgo));

                    Date createTime = new Date(randomTime);
                    announcement.setCreateTime(createTime);
                    announcement.setUpdateTime(createTime);

                    entityManager.persist(announcement);
                }
            } catch (Exception e) {
                System.out.println("创建公告时发生错误: " + e.getMessage());
            }

            System.out.println("公告数据已创建");
        }
    }

    /**
     * 创建收货地址（如果不存在）
     */
    public void createAddressesIfNotExists() {
        // 获取所有普通用户
        @SuppressWarnings("unchecked")
        java.util.List<User> users = entityManager
                .createQuery("SELECT u FROM User u WHERE u.role = 'ROLE_USER'")
                .getResultList();
                
        if (users.isEmpty()) {
            System.out.println("没有找到普通用户，无法创建收货地址");
            return;
        }
        
        // 省份和城市
        String[][] locations = {
            {"北京市", "朝阳区"},
            {"上海市", "浦东新区"},
            {"广东省", "深圳市南山区"},
            {"浙江省", "杭州市西湖区"},
            {"四川省", "成都市武侯区"}
        };
        
        // 检查Address表中是否已有数据
        Long addressCount = (Long) entityManager
                .createQuery("SELECT COUNT(a) FROM Address a")
                .getSingleResult();
                
        if (addressCount == 0) {
            System.out.println("开始创建Address表数据...");
            
            Random random = new Random();
            
            for (User user : users) {
                try {
                    // 为每个用户创建1-3个收货地址
                    int userAddressCount = 1 + random.nextInt(2);
                    
                    for (int i = 0; i < userAddressCount; i++) {
                        String[] location = locations[random.nextInt(locations.length)];
                        String province = location[0];
                        String city = location[1];
                        
                        Address address = new Address();
                        address.setUserId(user.getId());
                        address.setReceiverName(user.getUsername());
                        address.setReceiverPhone(user.getPhone());
                        address.setProvince(province);
                        address.setCity(city);
                        address.setDistrict("某某小区");
                        
                        // 生成详细地址
                        String detailAddress = (random.nextInt(100) + 1) + "号楼" + 
                                             (random.nextInt(10) + 1) + "0" + 
                                             (random.nextInt(9) + 1) + "室";
                        address.setDetailAddress(detailAddress);
                        
                        // 第一个地址设为默认
                        address.setIsDefault(i == 0);
                        
                        entityManager.persist(address);
                    }
                } catch (Exception e) {
                    System.out.println("为用户" + user.getId() + "创建地址时发生错误: " + e.getMessage());
                }
            }
            
            System.out.println("Address表数据创建完成");
        }
        
        for (User user : users) {
            try {
                // 检查用户是否已有地址
                if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
                    StringBuilder addressJson = new StringBuilder("[");
                    
                    // 为每个用户创建1-3个收货地址
                    int userJsonAddressCount = 1 + (int)(Math.random() * 2);
                    
                    for (int i = 0; i < userJsonAddressCount; i++) {
                        String[] location = locations[(int)(Math.random() * locations.length)];
                        String province = location[0];
                        String city = location[1];
                        
                        if (i > 0) addressJson.append(",");
                        
                        addressJson.append("{")
                                  .append("\"name\":\"").append(user.getUsername()).append("\",")
                                  .append("\"phone\":\"").append(user.getPhone()).append("\",")
                                  .append("\"province\":\"").append(province).append("\",")
                                  .append("\"city\":\"").append(city).append("\",")
                                  .append("\"district\":\"某某小区\",")
                                  .append("\"detailAddress\":\"").append((int)(Math.random() * 100) + 1).append("号楼").append((int)(Math.random() * 10) + 1).append("0").append((int)(Math.random() * 9) + 1).append("室\",")
                                  .append("\"isDefault\":").append(i == 0 ? "true" : "false")
                                  .append("}");
                    }
                    
                    addressJson.append("]");
                    
                    user.setAddresses(addressJson.toString());
                    entityManager.merge(user);
                }
            } catch (Exception e) {
                System.out.println("创建用户地址时发生错误: " + e.getMessage());
            }
        }
        
        System.out.println("用户收货地址数据已创建");
    }

    /**
     * 创建购物车数据（如果不存在）
     */
    public void createCartsIfNotExists() {
        // 查询是否已存在购物车数据
        Long count = (Long) entityManager
                .createQuery("SELECT COUNT(c) FROM Cart c")
                .getSingleResult();

        // 如果不存在，则创建
        if (count == 0) {
            // 获取所有普通用户
            @SuppressWarnings("unchecked")
            List<User> users = entityManager
                    .createQuery("SELECT u FROM User u WHERE u.role = 'ROLE_USER'")
                    .getResultList();

            if (users.isEmpty()) {
                System.out.println("没有找到普通用户，无法创建购物车数据");
                return;
            }

            // 获取所有商品
            @SuppressWarnings("unchecked")
            List<Product> products = entityManager
                    .createQuery("SELECT p FROM Product p")
                    .getResultList();

            if (products.isEmpty()) {
                System.out.println("没有找到商品，无法创建购物车数据");
                return;
            }

            Random random = new Random();

            // 为每个用户创建购物车记录
            for (User user : users) {
                // 每个用户添加1-4个商品到购物车
                int productCount = 1 + random.nextInt(3);

                for (int i = 0; i < productCount; i++) {
                    try {
                        // 随机选择一个商品
                        Product product = products.get(random.nextInt(products.size()));

                        // 检查购物车中是否已有此商品
                        Long existCount = (Long) entityManager
                                .createQuery("SELECT COUNT(c) FROM Cart c WHERE c.userId = :userId AND c.productId = :productId")
                                .setParameter("userId", user.getId())
                                .setParameter("productId", product.getId())
                                .getSingleResult();

                        if (existCount > 0) {
                            continue;  // 跳过已存在的商品
                        }

                        Cart cart = new Cart();
                        cart.setUserId(user.getId());
                        cart.setProductId(product.getId());

                        // 随机设置数量
                        cart.setQuantity(1 + random.nextInt(5));

                        // 随机设置是否勾选
                        cart.setSelected(random.nextBoolean());

                        // 随机选择规格
                        String specifications = product.getSpecifications();
                        if (specifications != null && !specifications.isEmpty()) {
                            // 简化处理，实际应用可能需要更复杂的JSON解析
                            if (specifications.contains("颜色") && specifications.contains("尺寸")) {
                                String[] colors = {"红色", "蓝色", "黑色"};
                                String[] sizes = {"S", "M", "L", "XL"};

                                String color = colors[random.nextInt(colors.length)];
                                String size = sizes[random.nextInt(sizes.length)];

                                cart.setSpecInfo("{\"颜色\":\"" + color + "\",\"尺寸\":\"" + size + "\"}");
                            }
                        }

                        entityManager.persist(cart);
                    } catch (Exception e) {
                        System.out.println("为用户" + user.getId() + "创建购物车数据时发生错误: " + e.getMessage());
                    }
                }
            }

            System.out.println("购物车数据已创建");
        }
    }

    /**
     * 创建收藏数据（如果不存在）
     */
    public void createFavoritesIfNotExists() {
        // A查询是否已存在收藏数据
        Long count = (Long) entityManager
                .createQuery("SELECT COUNT(f) FROM Favorite f")
                .getSingleResult();

        // 如果不存在，则创建
        if (count == 0) {
            // 获取所有普通用户
            @SuppressWarnings("unchecked")
            List<User> users = entityManager
                    .createQuery("SELECT u FROM User u WHERE u.role = 'ROLE_USER'")
                    .getResultList();

            if (users.isEmpty()) {
                System.out.println("没有找到普通用户，无法创建收藏数据");
                return;
            }

            // 获取所有商品
            @SuppressWarnings("unchecked")
            List<Product> products = entityManager
                    .createQuery("SELECT p FROM Product p")
                    .getResultList();

            if (products.isEmpty()) {
                System.out.println("没有找到商品，无法创建收藏数据");
                return;
            }

            Random random = new Random();

            // 为每个用户创建收藏记录
            for (User user : users) {
                // 每个用户收藏1-5个商品
                int favoriteCount = 1 + random.nextInt(4);

                for (int i = 0; i < favoriteCount; i++) {
                    try {
                        // 随机选择一个商品
                        Product product = products.get(random.nextInt(products.size()));

                        // 检查是否已收藏此商品
                        Long existCount = (Long) entityManager
                                .createQuery("SELECT COUNT(f) FROM Favorite f WHERE f.userId = :userId AND f.productId = :productId")
                                .setParameter("userId", user.getId())
                                .setParameter("productId", product.getId())
                                .getSingleResult();

                        if (existCount > 0) {
                            continue;  // 跳过已收藏的商品
                        }

                        Favorite favorite = new Favorite();
                        favorite.setUserId(user.getId());
                        favorite.setProductId(product.getId());

                        entityManager.persist(favorite);
                    } catch (Exception e) {
                        System.out.println("为用户" + user.getId() + "创建收藏数据时发生错误: " + e.getMessage());
                    }
                }
            }

            System.out.println("收藏数据已创建");
        }
    }

    /**
     * 创建搜索历史数据（如果不存在）
     */
    public void createSearchHistoriesIfNotExists() {
        // 查询是否已存在搜索历史数据
        Long count = (Long) entityManager
                .createQuery("SELECT COUNT(s) FROM SearchHistory s")
                .getSingleResult();

        // 如果不存在，则创建
        if (count == 0) {
            // 获取所有普通用户
            @SuppressWarnings("unchecked")
            List<User> users = entityManager
                    .createQuery("SELECT u FROM User u WHERE u.role = 'ROLE_USER'")
                    .getResultList();

            if (users.isEmpty()) {
                System.out.println("没有找到普通用户，无法创建搜索历史数据");
                return;
            }

            // 搜索关键词列表
            String[] keywords = {"手机", "电脑", "耳机", "衣服", "零食", "书籍", "家具", "女装", "男装", "运动鞋"};

            Random random = new Random();

            // 为每个用户创建搜索历史
            for (User user : users) {
                // 每个用户创建3-8条搜索历史
                int historyCount = 3 + random.nextInt(5);

                for (int i = 0; i < historyCount; i++) {
                    try {
                        SearchHistory searchHistory = new SearchHistory();
                        searchHistory.setUserId(user.getId());

                        // 随机选择关键词
                        String keyword = keywords[random.nextInt(keywords.length)];
                        searchHistory.setKeyword(keyword);

                        // 随机设置搜索结果数量
                        searchHistory.setResultCount(random.nextInt(100));

                        // 设置搜索时间为过去7天内的随机时间
                        long now = System.currentTimeMillis();
                        long sevenDaysAgo = now - 7L * 24 * 60 * 60 * 1000;
                        long randomTime = sevenDaysAgo + (long)(random.nextDouble() * (now - sevenDaysAgo));
                        searchHistory.setCreateTime(new Date(randomTime));

                        entityManager.persist(searchHistory);
                    } catch (Exception e) {
                        System.out.println("为用户" + user.getId() + "创建搜索历史数据时发生错误: " + e.getMessage());
                    }
                }
            }

            System.out.println("搜索历史数据已创建");
        }
    }

    /**
     * 创建客服会话和消息数据（如果不存在）
     */
    public void createCustomerServiceSessionsIfNotExists() {
        // 查询是否已存在客服会话数据
        Long count = (Long) entityManager
                .createQuery("SELECT COUNT(c) FROM CustomerServiceSession c")
                .getSingleResult();

        // 如果不存在，则创建
        if (count == 0) {
            // 获取所有普通用户
            @SuppressWarnings("unchecked")
            List<User> users = entityManager
                    .createQuery("SELECT u FROM User u WHERE u.role = 'ROLE_USER'")
                    .getResultList();

            if (users.isEmpty()) {
                System.out.println("没有找到普通用户，无法创建客服会话数据");
                return;
            }

            // 获取所有店铺
            @SuppressWarnings("unchecked")
            List<Store> stores = entityManager
                    .createQuery("SELECT s FROM Store s")
                    .getResultList();

            if (stores.isEmpty()) {
                System.out.println("没有找到店铺，无法创建客服会话数据");
                return;
            }

            Random random = new Random();

            // 用户常见问题列表
            String[] userQuestions = {
                "您好，请问这个商品什么时候能到货？",
                "我想咨询一下这个商品的尺码问题，我身高170cm，体重60kg，应该选什么尺码？",
                "商品的质量怎么样？耐用吗？",
                "请问这个商品可以退换吗？",
                "这个商品的保修期是多久？",
                "请问有没有优惠活动？"
            };

            // 商家常见回复列表
            String[] merchantReplies = {
                "您好，我们的商品一般3-5天内到货，请耐心等待。",
                "您好，根据您提供的身高体重信息，建议您选择M码。",
                "您好，我们的商品采用优质材料制作，质量有保障，请您放心购买。",
                "您好，本店所有商品支持7天无理由退换，但需要保持商品的完好无损。",
                "您好，本商品提供一年的保修期，如有质量问题，可随时联系我们。",
                "您好，目前店铺正在进行满300减50的活动，欢迎您选购。"
            };

            // 为每个用户创建客服会话
            for (User user : users) {
                // 每个用户创建1-2个客服会话
                int sessionCount = 1 + random.nextInt(1);

                for (int i = 0; i < sessionCount; i++) {
                    try {
                        // 随机选择一个店铺
                        Store store = stores.get(random.nextInt(stores.size()));

                        // 创建会话
                        CustomerServiceSession session = new CustomerServiceSession();
                        session.setUserId(user.getId());
                        session.setStoreId(store.getId());

                        // 设置会话时间为过去30天内的随机时间
                        long now = System.currentTimeMillis();
                        long thirtyDaysAgo = now - 30L * 24 * 60 * 60 * 1000;
                        long startRandomTime = thirtyDaysAgo + (long)(random.nextDouble() * (now - thirtyDaysAgo));
                        Date startTime = new Date(startRandomTime);
                        session.setStartTime(startTime);

                        // 50%的会话设为已结束
                        boolean isEnded = random.nextBoolean();
                        session.setStatus(isEnded ? 1 : 0);

                        if (isEnded) {
                            // 设置结束时间为开始时间后的10分钟到1小时
                            long endRandomTime = startRandomTime + (10 * 60 * 1000) + (long)(random.nextDouble() * 50 * 60 * 1000);
                            session.setEndTime(new Date(endRandomTime));

                            // 设置评价
                            session.setEvaluation(3 + random.nextInt(3)); // 3-5星，偏向好评

                            // 可能有备注
                            if (random.nextBoolean()) {
                                session.setRemark("客服态度很好，问题解决得很及时。");
                            }
                        }

                        entityManager.persist(session);
                        entityManager.flush(); // 确保会话被立即持久化以获取ID

                        // 创建会话消息
                        createCustomerServiceMessages(session, userQuestions, merchantReplies);
                    } catch (Exception e) {
                        System.out.println("为用户" + user.getId() + "创建客服会话数据时发生错误: " + e.getMessage());
                    }
                }
            }

            System.out.println("客服会话和消息数据已创建");
        }
    }

    /**
     * 为客服会话创建消息记录
     */
    private void createCustomerServiceMessages(CustomerServiceSession session, String[] userQuestions, String[] merchantReplies) {
        Random random = new Random();

        // 生成3-6条消息记录
        int messageCount = 3 + random.nextInt(3);

        long baseTime = session.getStartTime().getTime();
        long interval = session.getStatus() == 1 && session.getEndTime() != null ?
                        (session.getEndTime().getTime() - baseTime) / (messageCount + 1) :
                        5 * 60 * 1000; // 默认5分钟间隔

        for (int i = 0; i < messageCount; i++) {
            try {
                CustomerServiceMessage message = new CustomerServiceMessage();
                message.setSessionId(session.getId());
                message.setUserId(session.getUserId());
                message.setStoreId(session.getStoreId());

                // 奇数消息是用户发送的，偶数消息是商家回复的
                boolean isUserMessage = (i % 2 == 0);
                message.setFromType(isUserMessage ? 0 : 1);

                // 设置消息内容
                if (isUserMessage) {
                    message.setContent(userQuestions[random.nextInt(userQuestions.length)]);
                } else {
                    message.setContent(merchantReplies[random.nextInt(merchantReplies.length)]);
                }

                // 设置内容类型，大部分是文本
                message.setContentType("text");
                if (!isUserMessage && random.nextDouble() < 0.2) {
                    // 20%概率商家发送图片
                    message.setContentType("image");
                    message.setContent("https://example.com/customer_service/img" + (random.nextInt(5) + 1) + ".jpg");
                }

                // 设置已读状态
                message.setReadStatus(true);

                // 设置消息时间
                long messageTime = baseTime + (i * interval);
                message.setCreateTime(new Date(messageTime));

                entityManager.persist(message);
            } catch (Exception e) {
                System.out.println("为会话" + session.getId() + "创建消息数据时发生错误: " + e.getMessage());
            }
        }
    }
}
