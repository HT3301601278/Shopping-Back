package example.shopping.service.impl;

import example.shopping.dto.SearchHistoryDTO;
import example.shopping.entity.SearchHistory;
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

    @Override
    @Transactional
    public SearchHistory add(Long userId, SearchHistoryDTO searchHistoryDTO) {
        if (searchHistoryDTO.getKeyword() == null || searchHistoryDTO.getKeyword().trim().isEmpty()) {
            return null;
        }
        
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setUserId(userId);
        searchHistory.setKeyword(searchHistoryDTO.getKeyword().trim());
        searchHistory.setResultCount(searchHistoryDTO.getResultCount());
        searchHistory.setCreateTime(new Date());
        
        try {
            int result = searchHistoryMapper.insert(searchHistory);
            return result > 0 ? searchHistory : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<SearchHistory> findByUserId(Long userId) {
        try {
            return searchHistoryMapper.findByUserId(userId);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<SearchHistory> findLatestByUserId(Long userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // 默认返回10个
        }
        
        try {
            // 获取所有搜索历史并按时间排序
            List<SearchHistory> allHistories = searchHistoryMapper.findByUserId(userId);
            
            // 按创建时间倒序排序
            allHistories.sort(Comparator.comparing(SearchHistory::getCreateTime).reversed());
            
            // 返回前limit条记录
            if (allHistories.size() <= limit) {
                return allHistories;
            } else {
                return allHistories.subList(0, limit);
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public boolean delete(Long userId, Long id) {
        // 确保只能删除自己的搜索历史
        SearchHistory history = null;
        
        // 查找指定ID的历史记录
        for (SearchHistory sh : searchHistoryMapper.findByUserId(userId)) {
            if (sh.getId().equals(id)) {
                history = sh;
                break;
            }
        }
        
        if (history == null || !history.getUserId().equals(userId)) {
            return false;
        }
        
        return searchHistoryMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean clear(Long userId) {
        return searchHistoryMapper.deleteByUserId(userId) > 0;
    }

    @Override
    public List<Map<String, Object>> getHotKeywords(Long userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // 默认返回10个
        }
        
        // 获取用户的所有搜索历史
        List<SearchHistory> histories = searchHistoryMapper.findByUserId(userId);
        
        // 统计关键词频率
        Map<String, Long> keywordCount = histories.stream()
                .collect(Collectors.groupingBy(SearchHistory::getKeyword, Collectors.counting()));
        
        // 转换为列表并按频率排序
        List<Map<String, Object>> result = new ArrayList<>();
        keywordCount.forEach((keyword, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("keyword", keyword);
            item.put("count", count);
            result.add(item);
        });
        
        // 按搜索次数降序排序
        result.sort((a, b) -> Long.compare((Long)b.get("count"), (Long)a.get("count")));
        
        // 返回前limit条
        if (result.size() <= limit) {
            return result;
        } else {
            return result.subList(0, limit);
        }
    }

    @Override
    public List<Map<String, Object>> getGlobalHotKeywords(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // 默认返回10个
        }
        
        // 为简化实现，我们只返回一个示例数据
        // 在实际实现中，应该从数据库中统计全局热门关键词
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 创建一些示例数据
        String[] sampleKeywords = {"手机", "电脑", "鞋子", "衣服", "零食", "家具", "书籍", "玩具", "化妆品", "首饰"};
        Random random = new Random();
        
        for (int i = 0; i < Math.min(limit, sampleKeywords.length); i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("keyword", sampleKeywords[i]);
            item.put("count", random.nextInt(1000) + 1); // 随机生成1-1000的计数
            result.add(item);
        }
        
        // 按搜索次数降序排序
        result.sort((a, b) -> Integer.compare((Integer)b.get("count"), (Integer)a.get("count")));
        
        return result;
    }
} 