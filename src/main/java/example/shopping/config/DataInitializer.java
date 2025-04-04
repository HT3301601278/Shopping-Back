package example.shopping.config;

import example.shopping.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
     * 应用启动时自动创建管理员账户
     */
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.execute(status -> {
                createAdminIfNotExists();
                createMerchantIfNotExists();
                createUserIfNotExists();
                return null;
            });
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
            admin.setUsername("111");
            admin.setPassword("111");
            admin.setPhone("13500000000");
            admin.setRole("ROLE_ADMIN");
            admin.setStatus(1);

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
            User merchant = new User();
            merchant.setUsername("222");
            merchant.setPassword("222");
            merchant.setPhone("13600000000");
            merchant.setRole("ROLE_MERCHANT");
            merchant.setStatus(1);

            entityManager.persist(merchant);
            System.out.println("商家账户已创建，用户名：merchant，密码：merchant123");
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
            User user = new User();
            user.setUsername("333");
            user.setPassword("333");
            user.setPhone("13700000000");
            user.setRole("ROLE_USER");
            user.setStatus(1);

            entityManager.persist(user);
            System.out.println("普通用户账户已创建，用户名：user，密码：user123");
        }
    }
}
