package com.sangeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.mapper.RoleMenuMapper;
import com.sangeng.service.RoleMenuService;
import org.springframework.stereotype.Service;
import com.sangeng.domain.entity.RoleMenu;

/**
 * 角色和菜单关联表(RoleMenu)表服务实现类
 *
 * @author makejava
 * @since 2023-03-05 16:38:31
 */
@Service("roleMenuService")
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

}


