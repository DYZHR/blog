package com.sangeng.job;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sangeng.domain.entity.Article;
import com.sangeng.service.ArticleService;
import com.sangeng.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UpdateViewCountJob {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private ArticleService articleService;
    @Scheduled(cron = "0 0/10 * * * ? ")
//    @Scheduled(cron = "0/10 * * * * ? ")
    //一次更新多个viewCount，所以加个注解
    @Transactional
    public void updateViewCountJob(){
        System.out.println("定时任务");
        //获取redis中浏览量
        Map<String, Integer> viewCountMap = redisCache.getCacheMap("article:viewCount");
//        List<Article> articles = viewCountMap.entrySet()
//                .stream()
//                .map(entry -> new Article(Long.valueOf(entry.getKey()), entry.getValue().longValue()))
//                .collect(Collectors.toList());
//
//        //更新到数据库
//        articleService.updateBatchById(articles);
        for (String id : viewCountMap.keySet()) {
            LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Article::getId, Long.valueOf(id));
            updateWrapper.set(Article::getViewCount, viewCountMap.get(id).longValue());
            articleService.update(updateWrapper);
        }
    }
}
