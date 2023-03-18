package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.vo.MenuTreeVo;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.mapper.MenuMapper;
import com.sangeng.service.MenuService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import com.sangeng.domain.entity.Menu;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author makejava
 * @since 2023-03-02 15:33:36
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<String> selectPermsByUserId(Long id) {
       //如果是超级管理员，返回所有权限
        if (SecurityUtils.isAdmin()) {
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Menu::getMenuType, SystemConstants.MENU, SystemConstants.BUTTON);
            wrapper.eq(Menu::getStatus, SystemConstants.STATUS_NORMAL);
            List<Menu> menus = list(wrapper);
            List<String> perms = menus.stream()
                    .map(Menu::getPerms)
                    .collect(Collectors.toList());
            return perms;
        }
        //否则返回所具有的权限
        //user_id =(user_role表)=> role_id
        //role_id =(role_menu表)=> menu_id
        //menu_id =(menu表)=> perms
        return getBaseMapper().selectPermsByUserId(id);
    }

    @Override
    public List<Menu> selectRouterMenuTreeByUserId(Long userId) {
        MenuMapper menuMapper = getBaseMapper();
        List<Menu> menus = null;
        //判断是否是管理员，如果是，返回所有符合要求的menu
        if (SecurityUtils.isAdmin()) {
            menus = menuMapper.selectAllRouterMenu();
        } else {
            //否则，返回当前用户具有的menu
            menus = menuMapper.selectRouterMenuTreeByUserId(userId);
        }
        //构建tree
        //先找出第一层的菜单  然后去找他们的子菜单设置到children属性中
        List<Menu> menuTree = builderMenuTree(menus, 0L);
        return menuTree;
    }

    private List<Menu> builderMenuTree(List<Menu> menus, Long parentId) {
        List<Menu> menuTree = menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> menu.setChildren(getChildren(menu, menus)))
                .collect(Collectors.toList());
        return menuTree;
    }
    /**
     * 获取存入参数的 子Menu集合
     * @param menu
     * @param menus
     * @return
     */
    private List<Menu> getChildren(Menu menu, List<Menu> menus) {
        List<Menu> childrenList = menus.stream()
                .filter(m -> m.getParentId().equals(menu.getId()))
                .map(m->m.setChildren(getChildren(m,menus)))
                .collect(Collectors.toList());
        return childrenList;
    }

    @Override
    public ResponseResult listAllMenu(String status, String menuName) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        //按状态查询
        queryWrapper.like(status != null, Menu::getStatus, status);
        //按菜单名查询
        queryWrapper.like(menuName != null, Menu::getMenuName, menuName);
        //按父菜单排序
        queryWrapper.orderByAsc(Menu::getParentId);
        //按orderNum排序
        queryWrapper.orderByAsc(Menu::getOrderNum);
        //查询
        List<Menu> menus = list(queryWrapper);
        //封装数据返回
        return ResponseResult.okResult(menus);
    }

    @Override
    public ResponseResult updateMenu(Menu menu) {
        //父菜单不能为当前菜单
        if (menu.getId().equals(menu.getParentId())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "修改菜单'写博文'失败，上级菜单不能选择自己");
        }
        updateById(menu);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteMenu(Long menuId) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        //如果要删除的菜单有子菜单则不能删除
        queryWrapper.eq(Menu::getParentId, menuId);
        if (count(queryWrapper) > 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "存在子菜单不允许删除");
        }
        //顺利删除
        removeById(menuId);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult treeselect() {
        List<MenuTreeVo> menuTreeVos = getChildren(0L);
        return ResponseResult.okResult(menuTreeVos);
    }
    public List<MenuTreeVo> getChildren(Long parentId) {
        //查出parentId的孩子
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getParentId, parentId);
        queryWrapper.orderByAsc(Menu::getOrderNum);
        List<Menu> menus = list(queryWrapper);
        List<MenuTreeVo> menuTreeVos = BeanCopyUtils.copyBeanList(menus, MenuTreeVo.class);

        //设置标签，并查询parentId的孩子的孩子
        for (MenuTreeVo menuTreeVo : menuTreeVos) {
            //设置标签
            menuTreeVo.setLabel(menuTreeVo.getMenuName());
            //查parentId的孩子的孩子
            LambdaQueryWrapper<Menu> childrenQueryWrapper = new LambdaQueryWrapper<>();
            childrenQueryWrapper.eq(Menu::getParentId, menuTreeVo.getId());
            //如果parentId的孩子有孩子，就递归查
            if (count(childrenQueryWrapper) > 0) {
                menuTreeVo.setChildren(getChildren(menuTreeVo.getId()));
            }
        }

        return menuTreeVos;
    }

}


