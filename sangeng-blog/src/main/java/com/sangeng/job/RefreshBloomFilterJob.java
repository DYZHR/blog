package com.sangeng.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.sangeng.domain.entity.Article;
import com.sangeng.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RefreshBloomFilterJob {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ArticleService articleService;

    //每天清理一次布隆过滤器
    @Scheduled(cron = "0 0/10 * * * ? ")
    public void getBloomFilter() {
        //获取上下文
        DefaultListableBeanFactory defaultListableBeanFactory =
                (DefaultListableBeanFactory)applicationContext.getAutowireCapableBeanFactory();
        //销毁原有BloomFilter
//        BloomFilter<Long> oldBloomFilter = (BloomFilter<Long>)applicationContext.getBean("bloomFilter");
//        if (defaultListableBeanFactory.containsBeanDefinition("bloomFilter")) {
            defaultListableBeanFactory.destroySingleton("bloomFilter");
            defaultListableBeanFactory.removeBeanDefinition("bloomFilter");
//        }

        //重新获取BloomFilter
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
        //重新注册bloomFilter到IoC容器
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(BloomFilter.class);
        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        defaultListableBeanFactory.registerBeanDefinition("bloomFilter", beanDefinition);
        defaultListableBeanFactory.registerSingleton("bloomFilter", bloomFilter);
    }

}
