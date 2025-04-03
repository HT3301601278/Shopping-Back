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
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setPhone("13800000000");
            admin.setRole("ROLE_ADMIN");
            admin.setStatus(1);

            entityManager.persist(admin);
            System.out.println("管理员账户已创建，用户名：admin，密码：admin123");
        }
    }
}
