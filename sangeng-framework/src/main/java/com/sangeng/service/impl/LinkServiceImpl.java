package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Comment;
import com.sangeng.domain.vo.CommentVo;
import com.sangeng.domain.vo.LinkVo;
import com.sangeng.mapper.LinkMapper;
import com.sangeng.service.LinkService;
import com.sangeng.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;
import com.sangeng.domain.entity.Link;

import java.util.List;

/**
 * 友链(Link)表服务实现类
 *
 * @author makejava
 * @since 2023-02-27 13:30:03
 */
@Service("linkService")
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {


    /**
     * 查询所有审核通过的link，并封装成VO
     * @return
     */
    @Override
    public ResponseResult getAllLink() {
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        //审核状态
        queryWrapper.eq(Link::getStatus, SystemConstants.LINK_STATUS_NORMAL);
        List<Link> links = list(queryWrapper);
        //封装成VO
        List<LinkVo> linkVos = BeanCopyUtils.copyBeanList(links, LinkVo.class);
        //封装返回
        return ResponseResult.okResult(linkVos);
    }

}


