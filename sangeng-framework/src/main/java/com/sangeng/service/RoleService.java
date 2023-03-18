package com.sangeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddRoleDto;
import com.sangeng.domain.dto.RoleDto;
import com.sangeng.domain.entity.Role;

import java.util.List;

/**
 * 角色信息表(Role)表服务接口
 *
 * @author makejava
 * @since 2023-03-02 15:37:03
 */
public interface RoleService extends IService<Role> {

    List<String> selectRoleKeyByUserId(Long id);

    ResponseResult listRole(int pageNum, int pageSize, String roleName, String status);

    ResponseResult changeStatus(RoleDto roleDto);

    ResponseResult add(AddRoleDto addRoleDto);

    ResponseResult roleMenuTreeselect(Long id);
}

