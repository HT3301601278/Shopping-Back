package example.shopping.service.impl;

import example.shopping.entity.Product;
import example.shopping.entity.SearchHistory;
import example.shopping.mapper.ProductMapper;
import example.shopping.mapper.SearchHistoryMapper;
import example.shopping.service.SearchHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 搜索历史服务实现类
 */
@Service
public class SearchHistoryServiceImpl implements SearchHistoryService {

    @Autowired
    private SearchHistoryMapper searchHistoryMapper;
    
    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional
    public SearchHistory add(Long userId, String keyword, Integer resultCount) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setUserId(userId);
        searchHistory.setKeyword(keyword.trim());
        searchHistory.setResultCount(resultCount);
        searchHistory.setCreateTime(new Date());
        
        searchHistoryMapper.insert(searchHistory);
        
        return searchHistory;
    }

    @Override
    public List<SearchHistory> findByUserId(Long userId) {
        return searchHistoryMapper.findByUserId(userId);
    }

    @Override
    public Map<String, Object> findByUserIdWithPage(Long userId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<SearchHistory> histories = searchHistoryMapper.findByUserIdWithPage(userId, offset, pageSize);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", histories);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        // TODO: 添加总数统计
        
        return result;
    }

    @Override
    public List<String> findKeywordsByUserId(Long userId) {
        return searchHistoryMapper.findKeywordsByUserId(userId);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        return searchHistoryMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean clear(Long userId) {
        return searchHistoryMapper.deleteByUserId(userId) > 0;
    }

    @Override
    public List<Map<String, Object>> findHotKeywords(int limit) {
        if (limit <= 0) {
            limit = 10; // 默认返回10个
        }
        
        return searchHistoryMapper.findHotKeywords(limit);
    }

    @Override
    public List<Long> recommendProductsBySearchHistory(Long userId, int limit) {
        if (limit <= 0) {
            limit = 10; // 默认返回10个
        }
        
        // 获取用户的搜索关键词
        List<String> keywords = findKeywordsByUserId(userId);
        if (keywords == null || keywords.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 为了简单起见，这里只取最近10个关键词
        if (keywords.size() > 10) {
            keywords = keywords.subList(0, 10);
        }
        
        // 根据关键词搜索商品
        Set<Long> productIds = new HashSet<>();
        for (String keyword : keywords) {
            productMapper.search(keyword).stream()
                    .map(Product::getId)
                    .forEach(productIds::add);
            
            if (productIds.size() >= limit) {
                break;
            }
        }
        
        // 如果找到的商品不够，则补充热门商品
        if (productIds.size() < limit) {
            productMapper.findHotProducts(limit - productIds.size()).stream()
                    .map(Product::getId)
                    .forEach(productIds::add);
        }
        
        // 转换为列表并限制数量
        return new ArrayList<>(productIds).subList(0, Math.min(productIds.size(), limit));
    }
} 