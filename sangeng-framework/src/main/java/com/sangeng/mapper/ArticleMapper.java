package com.sangeng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangeng.domain.entity.Article;
import com.sangeng.domain.entity.Article;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章表(Article)表数据库访问层
 *
 * @author makejava
 * @since 2023-02-26 11:45:19
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

}

