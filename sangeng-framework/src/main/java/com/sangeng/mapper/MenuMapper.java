package com.sangeng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangeng.domain.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 菜单权限表(Menu)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-02 15:33:31
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    //mapper的实现类是XML文件
    List<String> selectPermsByUserId(Long id);

    List<Menu> selectAllRouterMenu();

    List<Menu> selectRouterMenuTreeByUserId(Long userId);
}
