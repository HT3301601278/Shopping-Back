package example.shopping.service.impl;

import example.shopping.entity.Store;
import example.shopping.entity.User;
import example.shopping.exception.BusinessException;
import example.shopping.mapper.CustomerServiceSessionMapper;
import example.shopping.mapper.StoreMapper;
import example.shopping.mapper.UserMapper;
import example.shopping.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 店铺服务实现类
 */
@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreMapper storeMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CustomerServiceSessionMapper sessionMapper;

    @Override
    @Transactional
    public Store create(Long userId, Store store) {
        // 检查用户是否存在
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查用户是否已有店铺
        Store existingStore = storeMapper.findByUserId(userId);
        if (existingStore != null) {
            throw new BusinessException("用户已有店铺");
        }
        
        // 补充店铺信息
        store.setUserId(userId);
        if (store.getStatus() == null) {
            store.setStatus(0); // 0-审核中
        }
        
        Date now = new Date();
        store.setCreateTime(now);
        store.setUpdateTime(now);
        
        storeMapper.insert(store);
        
        return store;
    }

    @Override
    @Transactional
    public Store update(Long id, Store store) {
        Store existingStore = storeMapper.findById(id);
        if (existingStore == null) {
            throw new BusinessException("店铺不存在");
        }
        
        // 保留不可修改的字段
        store.setId(id);
        store.setUserId(existingStore.getUserId());
        store.setCreateTime(existingStore.getCreateTime());
        store.setUpdateTime(new Date());
        
        storeMapper.update(store);
        
        return storeMapper.findById(id);
    }

    @Override
    public Store findById(Long id) {
        return storeMapper.findById(id);
    }

    @Override
    public Store findByUserId(Long userId) {
        return storeMapper.findByUserId(userId);
    }

    @Override
    public List<Store> findAll() {
        return storeMapper.findAll();
    }

    @Override
    public Map<String, Object> findByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Store> stores = storeMapper.findByPage(offset, pageSize);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", stores);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        // TODO: 添加总数统计
        
        return result;
    }

    @Override
    public List<Store> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BusinessException("搜索关键字不能为空");
        }
        
        return storeMapper.search(keyword.trim());
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        Store store = storeMapper.findById(id);
        if (store == null) {
            throw new BusinessException("店铺不存在");
        }
        
        if (status < 0 || status > 2) {
            throw new BusinessException("状态值无效");
        }
        
        // 当店铺审核通过时，将用户角色更新为商家
        if (status == 1 && store.getStatus() != 1) {
            // 获取店铺所有者
            User storeOwner = userMapper.findById(store.getUserId());
            if (storeOwner != null && "ROLE_USER".equals(storeOwner.getRole())) {
                // 更新用户角色为商家
                User updateUser = new User();
                updateUser.setId(storeOwner.getId());
                updateUser.setRole("ROLE_MERCHANT");
                updateUser.setUpdateTime(new Date());
                userMapper.update(updateUser);
            }
        }
        
        return storeMapper.updateStatus(id, status) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        Store store = storeMapper.findById(id);
        if (store == null) {
            throw new BusinessException("店铺不存在");
        }
        
        return storeMapper.deleteById(id) > 0;
    }

    @Override
    public Double getCustomerServiceRating(Long id) {
        Store store = storeMapper.findById(id);
        if (store == null) {
            throw new BusinessException("店铺不存在");
        }
        
        // 获取客服会话评价的平均分
        Double avgRating = sessionMapper.calculateAverageEvaluation(id);
        return avgRating != null ? avgRating : 5.0; // 默认5分
    }

    @Override
    public List<Store> findByStatus(Integer status) {
        if (status < 0 || status > 2) {
            throw new BusinessException("状态值无效");
        }
        
        return storeMapper.findByStatus(status);
    }
} 