package example.shopping.service.impl;

import example.shopping.entity.Favorite;
import example.shopping.entity.Product;
import example.shopping.exception.BusinessException;
import example.shopping.mapper.FavoriteMapper;
import example.shopping.mapper.ProductMapper;
import example.shopping.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 收藏服务实现类
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional
    public Favorite add(Long userId, Long productId) {
        // 检查商品是否存在
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 检查是否已经收藏
        Favorite existingFavorite = favoriteMapper.findByUserIdAndProductId(userId, productId);
        if (existingFavorite != null) {
            throw new BusinessException("已经收藏过该商品");
        }

        // 创建收藏
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favorite.setCreateTime(new Date());

        favoriteMapper.insert(favorite);

        return favorite;
    }

    @Override
    @Transactional
    public boolean cancel(Long userId, Long productId) {
        // 检查是否已经收藏
        Favorite existingFavorite = favoriteMapper.findByUserIdAndProductId(userId, productId);
        if (existingFavorite == null) {
            throw new BusinessException("尚未收藏该商品");
        }

        return favoriteMapper.deleteByUserIdAndProductId(userId, productId) > 0;
    }

    @Override
    public List<Map<String, Object>> findByUserId(Long userId) {
        List<Favorite> favorites = favoriteMapper.findByUserId(userId);
        return favorites.stream().map(this::convertFavoriteToMap).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> findByUserIdWithPage(Long userId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Favorite> favorites = favoriteMapper.findByUserIdWithPage(userId, offset, pageSize);

        List<Map<String, Object>> list = favorites.stream()
                .map(this::convertFavoriteToMap)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        // TODO: 添加总数统计

        return result;
    }

    @Override
    public boolean isFavorite(Long userId, Long productId) {
        return favoriteMapper.findByUserIdAndProductId(userId, productId) != null;
    }

    @Override
    public int countByProductId(Long productId) {
        return favoriteMapper.countByProductId(productId);
    }

    /**
     * 转换收藏对象为Map
     *
     * @param favorite 收藏对象
     * @return Map
     */
    private Map<String, Object> convertFavoriteToMap(Favorite favorite) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", favorite.getId());
        map.put("userId", favorite.getUserId());
        map.put("productId", favorite.getProductId());
        map.put("createTime", favorite.getCreateTime());

        // 获取商品信息
        Product product = productMapper.findById(favorite.getProductId());
        if (product != null) {
            map.put("product", product);
        }

        return map;
    }
}
