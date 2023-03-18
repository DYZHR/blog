package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddRoleDto;
import com.sangeng.domain.dto.RoleDto;
import com.sangeng.domain.entity.Role;
import com.sangeng.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public ResponseResult listRole(
            @RequestParam(value = "pageNum") int pageNum,
            @RequestParam(value = "pageSize") int pageSize,
            @RequestParam(required = false, value = "roleName") String roleName,
            @RequestParam(required = false, value = "status") String status
    ){
        return roleService.listRole(pageNum, pageSize, roleName, status);
    }

    @PutMapping("/changeStatus")
    public ResponseResult changeStatus(@RequestBody RoleDto roleDto) {
        return roleService.changeStatus(roleDto);
    }

    @PostMapping
    public ResponseResult add(@RequestBody AddRoleDto addRoleDto) {
        return roleService.add(addRoleDto);
    }

    @GetMapping("/{id}")
    public ResponseResult selectOne(@PathVariable("id") Long id) {
        Role role = roleService.getById(id);
        return ResponseResult.okResult(role);
    }

    @GetMapping("/roleMenuTreeselect/{id}")
    public ResponseResult roleMenuTreeselect(@PathVariable("id") Long id) {
        return roleService.roleMenuTreeselect(id);
    }

    @GetMapping("/listAllRole")
    public ResponseResult listAllRole() {
        List<Role> roles = roleService.list();
        return ResponseResult.okResult(roles);
    }
}
