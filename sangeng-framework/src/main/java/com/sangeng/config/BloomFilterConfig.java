package com.sangeng.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.sangeng.domain.entity.Article;
import com.sangeng.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class BloomFilterConfig {
    @Autowired
    private ArticleService articleService;
    @Bean("bloomFilter")
    @ConditionalOnMissingBean
    public BloomFilter<Long> initBloomFilter() {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Article::getId);
        List<Long> articleIds = articleService.list(queryWrapper)
                .stream()
                .map(Article::getId)
                .collect(Collectors.toList());
        BloomFilter<Long> bloomFilter = BloomFilter.create(
                Funnels.longFunnel(),
                articleIds.size() * 2,
                0.01
        );
        for (Long articleId : articleIds) {
            bloomFilter.put(articleId);
        }
        return bloomFilter;
    }
}
