package com.sangeng.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sangeng.annotion.SystemLog;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Article;
import com.sangeng.domain.vo.HotArticleVo;
import com.sangeng.service.ArticleService;
import com.sangeng.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @SystemLog(businessName = "更新用户信息")
    @GetMapping("/hotArticleList")
    public ResponseResult hotArticleList(){
        ResponseResult result =  articleService.hotArticleList();
        return result;
    }

    @SystemLog(businessName = "文章列表")
    @GetMapping("/articleList")
    public ResponseResult articleList(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam Long categoryId) {
        return articleService.articleList(pageNum, pageSize, categoryId);
    }

    @SystemLog(businessName = "查询文章详情")
    @GetMapping("/{id}")
    public ResponseResult getArticleDetail(@PathVariable("id") Long id) {
        return articleService.getArticleDetail(id);
    }

    @SystemLog(businessName = "更新用户信息")
    @PutMapping("/updateViewCount/{id}")
    public ResponseResult updateViewCount(@PathVariable("id") Long id) {
        return articleService.updateViewCount(id);
    }
}
