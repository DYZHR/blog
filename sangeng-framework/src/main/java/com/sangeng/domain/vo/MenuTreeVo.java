package com.sangeng.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuTreeVo {
    private Long id;
    //对应实体类菜单名称
    private String label;
    private String menuName;
    //父菜单ID
    private Long parentId;
    private List<MenuTreeVo> children;
}
