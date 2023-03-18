package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.vo.CommentVo;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.exception.SystemException;
import com.sangeng.mapper.CommentMapper;
import com.sangeng.service.CommentService;
import com.sangeng.service.UserService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.enums.AppHttpCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sangeng.domain.entity.Comment;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author makejava
 * @since 2023-02-27 21:15:30
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserService userService;

    @Override
    public ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize) {
        //查询对应文章的根评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        //对articleId进行判断
        queryWrapper.eq(SystemConstants.ARTICLE_COMMENT.equals(commentType), Comment::getArticleId,articleId);
        //根评论 rootId为-1
        queryWrapper.eq(Comment::getRootId,-1);
        //判断评论类型
        queryWrapper.eq(Comment::getType, commentType);

        //分页查询
        Page<Comment> page = new Page(pageNum,pageSize);
        page(page,queryWrapper);
        List<CommentVo> commentVoList = toCommentVoList(page.getRecords());

        //根据id查询子评论
        for (CommentVo commentVo : commentVoList) {
            List<CommentVo> children = getChildren(commentVo.getId());
            commentVo.setChildren(children);
        }


        return ResponseResult.okResult(new PageVo(commentVoList,page.getTotal()));
    }

    @Override
    public ResponseResult addComment(Comment comment) {
        //评论内容不能为空
        if(!StringUtils.hasText(comment.getContent())){
            throw new SystemException(AppHttpCodeEnum.CONTENT_NOT_NULL);
        }
        //设置了自动填充公共字段，这里直接插入即可
        save(comment);
        return ResponseResult.okResult();
    }

    /**
     * 根据id查所有子评论
     * @param id
     * @return List<CommentVo>
     */
    private List<CommentVo> getChildren(Long id) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootId, id);
        queryWrapper.orderByAsc(Comment::getCreateTime);
        List<Comment> comments = list(queryWrapper);
        return toCommentVoList(comments);
    }

    private List<CommentVo> toCommentVoList(List<Comment> list){
        List<CommentVo> commentVos = BeanCopyUtils.copyBeanList(list, CommentVo.class);
        //遍历vo集合
        for (CommentVo commentVo : commentVos) {
            //通过creatyBy查询用户的昵称并赋值
            String nickName = userService.getById(commentVo.getCreateBy()).getNickName();
            commentVo.setUsername(nickName);
            //通过toCommentUserId查询用户的昵称并赋值
            //如果toCommentUserId不为-1才进行查询
            if(commentVo.getToCommentUserId()!=-1){
                String toCommentUserName = userService.getById(commentVo.getToCommentUserId()).getNickName();
                commentVo.setToCommentUserName(toCommentUserName);
            }
        }
        return commentVos;
    }
}


