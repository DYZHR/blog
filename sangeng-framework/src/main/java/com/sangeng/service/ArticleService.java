package com.sangeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddArticleDto;
import com.sangeng.domain.entity.Article;
import com.sangeng.domain.vo.ArticleWithTagVo;
import com.sangeng.domain.vo.PageVo;

/**
 * 文章表(Article)表服务接口
 *
 * @author makejava
 * @since 2023-02-26 11:45:21
 */
public interface ArticleService extends IService<Article> {

    ResponseResult hotArticleList();

    ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId);

    ResponseResult getArticleDetail(Long id);

    ResponseResult updateViewCount(Long id);

    ResponseResult add(AddArticleDto article);

    ResponseResult listArticle(int pageNum, int pageSize, String title, String summary);

    ResponseResult selectDetail(Long id);

    ResponseResult updateArticleWithTags(ArticleWithTagVo articleWithTagVo);

}

