package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddRoleDto;
import com.sangeng.domain.dto.RoleDto;
import com.sangeng.domain.entity.Menu;
import com.sangeng.domain.entity.RoleMenu;
import com.sangeng.domain.vo.MenuTreeVo;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.mapper.RoleMapper;
import com.sangeng.service.MenuService;
import com.sangeng.service.RoleMenuService;
import com.sangeng.service.RoleService;
import com.sangeng.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sangeng.domain.entity.Role;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author makejava
 * @since 2023-03-02 15:37:03
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private RoleMenuService roleMenuService;
    @Autowired
    private MenuServiceImpl menuService;

    @Override
    public List<String> selectRoleKeyByUserId(Long id) {
        //判断是否是管理员，如果是，则返回集合中只需有admin
        if (id == 1L) {
            List<String> roleKeys = new ArrayList<>();
            roleKeys.add("admin");
            return roleKeys;
        }
        //否则查询用户所具有的角色信息
        return getBaseMapper().selectRoleKeyByUserId(id);
    }

    @Override
    public ResponseResult listRole(int pageNum, int pageSize, String roleName, String status) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        //角色名称
        queryWrapper.like(roleName != null, Role::getRoleName, roleName);
        //状态
        queryWrapper.eq(status != null, Role::getStatus, status);
        //按照role_sort进行升序排列
        queryWrapper.orderByAsc(Role::getRoleSort);
        //分页
        Page<Role> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        //封装数据返回
        PageVo pageVo = new PageVo(page.getRecords(), page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult changeStatus(RoleDto roleDto) {

        Role role = getById(roleDto.getRoleId());
        if (role.getStatus().equals("0")) {
            role.setStatus("1");
        }
        role.setStatus("0");
        updateById(role);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult add(AddRoleDto addRoleDto) {
        //新增角色
        Role role = BeanCopyUtils.copyBean(addRoleDto, Role.class);
        save(role);
        //新增角色对应的菜单
        List<RoleMenu> roleMenus = new ArrayList<>();
        Long roleId = role.getId();
        for (Long menuId : addRoleDto.getMenuIds()) {
            roleMenus.add(new RoleMenu(roleId, menuId));
        }
        roleMenuService.saveBatch(roleMenus);
        //封装数据返回
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult roleMenuTreeselect(Long id) {
        LambdaQueryWrapper<RoleMenu> roleMenuQueryWrapper = new LambdaQueryWrapper<>();
        roleMenuQueryWrapper.eq(RoleMenu::getRoleId, id);
        List<Long> menuIds = roleMenuService.list(roleMenuQueryWrapper).stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toList());
        List<MenuTreeVo> menuTreeVos = new ArrayList<>();
        for (Long menuId : menuIds) {
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Menu::getId, menuId);
            List<Menu> menus = menuService.list(wrapper);
            List<MenuTreeVo> treeVos = BeanCopyUtils.copyBeanList(menus, MenuTreeVo.class);
            menuTreeVos.addAll(treeVos);
        }


        return ResponseResult.okResult(menuTreeVos);
    }

}


