package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.hash.BloomFilter;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddArticleDto;
import com.sangeng.domain.entity.ArticleTag;
import com.sangeng.domain.entity.Category;
import com.sangeng.domain.entity.Tag;
import com.sangeng.domain.vo.*;
import com.sangeng.mapper.ArticleMapper;
import com.sangeng.domain.entity.Article;
import com.sangeng.service.ArticleService;
import com.sangeng.service.ArticleTagService;
import com.sangeng.service.CategoryService;
import com.sangeng.service.TagService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.RedisCache;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文章表(Article)表服务实现类
 *
 * @author makejava
 * @since 2023-02-26 11:45:23
 */
@Service("articleService")
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private ArticleTagService articleTagService;
    @Autowired
    private TagService tagService;
    @Autowired
    private BloomFilter<Long> bloomFilter;
    @Override
    public ResponseResult hotArticleList() {
        //查询热门文章 封装成ResponseResult返回
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //必须是正式文章
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        //按照浏览量进行排序
        queryWrapper.orderByDesc(Article::getViewCount);
        //最多只查询10条
        Page<Article> page = new Page(1,10);
        page(page,queryWrapper);

        List<Article> articles = page.getRecords();
        //从redis查浏览量
        for (Article article : articles) {
            Integer viewCount = redisCache.getCacheMapValue("article:viewCount", article.getId().toString());
            article.setViewCount(Long.valueOf(viewCount));
        }

        //bean拷贝
        return ResponseResult.okResult(
                BeanCopyUtils.copyBeanList(articles, HotArticleVo.class));
    }

    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        //查询条件
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //如果有传categoryId，查询就要和传入相同
        queryWrapper.eq(categoryId != null && categoryId > 0, Article::getCategoryId, categoryId);
        //文章状态要是正式发布的
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        //对置顶状态降序排序
        queryWrapper.orderByDesc(Article::getIsTop);
        //分页查询
        Page<Article> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        List<Article> articles = page.getRecords();
        //从redis查浏览量
        for (Article article : articles) {
            Integer viewCount = redisCache.getCacheMapValue("article:viewCount", article.getId().toString());
            article.setViewCount(Long.valueOf(viewCount));
        }
        //查询categoryName
        articles.stream()
                .map(new Function<Article, Article>() {
                    @Override
                    public Article apply(Article article) {
                        //获取分类id，查询分类名称
                        Category category = categoryService.getById(article.getCategoryId());
                        String categoryName = category.getName();
                        //把分类名称赋给article
                        article.setCategoryName(categoryName);
                        return article;
                    }
                }).collect(Collectors.toList());
        //封装查询结果
        List<ArticleListVo> articleListVos = BeanCopyUtils.copyBeanList(articles, ArticleListVo.class);
        PageVo pageVo = new PageVo(articleListVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getArticleDetail(Long id) {
        //1.先看布隆过滤器有没有
        if (!bloomFilter.mightContain(id)) {
            return ResponseResult.errorResult(404, "查询出错");
        }
        //2.再看redis
        ArticleDetailVo articleDetailVo = null;
        articleDetailVo = redisCache.getCacheMapValue("articleDetailVo", id.toString());
        if (articleDetailVo != null) {
            //设置浏览量
            Integer viewCount = redisCache.getCacheMapValue("article:viewCount", id.toString());
            articleDetailVo.setViewCount(Long.valueOf(viewCount));
            //封装响应返回
            return ResponseResult.okResult(articleDetailVo);
        }
        //3.查MySQL
        //根据id查询文章
        Article article = getById(id);
        //从redis查浏览量
        Integer viewCount = redisCache.getCacheMapValue("article:viewCount", id.toString());
        article.setViewCount(Long.valueOf(viewCount));
        //转换成VO
        ArticleDetailVo articleDetailVo1 = BeanCopyUtils.copyBean(article, ArticleDetailVo.class);
        //根据分类id查询分类名
        String categoryId = articleDetailVo1.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if (category != null)
            articleDetailVo1.setCategoryName(category.getName());
        redisCache.setCacheMapValue("articleDetailVo", id.toString(), articleDetailVo1);
        //封装响应返回
        return ResponseResult.okResult(articleDetailVo1);

//        //根据id查询文章
//        Article article = getById(id);
//        //从redis查浏览量
//        Integer viewCount = redisCache.getCacheMapValue("article:viewCount", id.toString());
//        article.setViewCount(Long.valueOf(viewCount));
//        //转换成VO
//        ArticleDetailVo articleDetailVo = BeanCopyUtils.copyBean(article, ArticleDetailVo.class);
//        //根据分类id查询分类名
//        String categoryId = articleDetailVo.getCategoryId();
//        Category category = categoryService.getById(categoryId);
//        if (category != null)
//            articleDetailVo.setCategoryName(category.getName());
//        //封装响应返回
//        return ResponseResult.okResult(articleDetailVo);
    }

    @Override
    public ResponseResult updateViewCount(Long id) {
        redisCache.incrementCacheMapValue("article:viewCount", id.toString(), 1);
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult add(AddArticleDto articleDto) {
        //添加 博客
        Article article = BeanCopyUtils.copyBean(articleDto, Article.class);
        save(article);

        //此时article.getId()就可以返回刚插入的article_id了
        List<ArticleTag> articleTags = articleDto.getTags().stream()
                .map(tagId -> new ArticleTag(article.getId(), tagId))
                .collect(Collectors.toList());

        //添加 博客和标签的关联
        articleTagService.saveBatch(articleTags);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult listArticle(int pageNum, int pageSize, String title, String summary) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(title != null, Article::getTitle, title);
        queryWrapper.like(summary != null, Article::getSummary, summary);
        Page<Article> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        PageVo pageVo = new PageVo(page.getRecords(), page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult selectDetail(Long id) {
        //查询文章
        Article article = getById(id);
        ArticleWithTagVo articleWithTagVo = BeanCopyUtils.copyBean(article, ArticleWithTagVo.class);
        //查询相关标签
        LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId, id);
        List<ArticleTag> articleTags = articleTagService.list(queryWrapper);
        //过滤出tag_id
        List<Long> tags = articleTags.stream()
                .map(ArticleTag::getTagId)
                .collect(Collectors.toList());
        //加入articleWithTagVo
        articleWithTagVo.setTags(tags);
        //封装数据返回
        return ResponseResult.okResult(articleWithTagVo);
    }

    @Override
    public ResponseResult updateArticleWithTags(ArticleWithTagVo articleWithTagVo) {
        //更新文章表
        Article article = BeanCopyUtils.copyBean(articleWithTagVo, Article.class);
        updateById(article);
        //更新文章标签表
        //先删除再插入
        Long articleId = article.getId();
        LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId, articleId);
        articleTagService.remove(queryWrapper);
        List<ArticleTag> articleTags = articleWithTagVo.getTags().stream()
                .map(aLong -> new ArticleTag(articleId, aLong))
                .collect(Collectors.toList());
        articleTagService.saveBatch(articleTags);
        //封装数据返回
        return ResponseResult.okResult();
    }


}

