package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddArticleDto;
import com.sangeng.domain.entity.Menu;
import com.sangeng.domain.vo.ArticleWithTagVo;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.service.ArticleService;
import com.sangeng.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private MenuService menuService;

    @PostMapping
    public ResponseResult add(@RequestBody AddArticleDto article){

        return articleService.add(article);
    }
    @GetMapping("/list")
    public ResponseResult listArticle(
            @RequestParam int pageNum,
            @RequestParam int pageSize,
            @RequestParam(required = false, value = "title") String title,
            @RequestParam(required = false, value = "summary") String summary) {
        return articleService.listArticle(pageNum, pageSize, title, summary);
    }

    @GetMapping("/{id}")
    public ResponseResult selectDetail(@PathVariable("id") Long id) {
        return articleService.selectDetail(id);
    }
    @PutMapping
    public ResponseResult updateArticleWithTags(@RequestBody ArticleWithTagVo articleWithTagVo){
        return articleService.updateArticleWithTags(articleWithTagVo);
    }
    @DeleteMapping("/{id}")
    public ResponseResult deleteArticle(@PathVariable("id") Long id) {
        articleService.removeById(id);
        return ResponseResult.okResult();
    }

}