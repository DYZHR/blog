package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/system/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public ResponseResult listAll(
            @RequestParam(required = false, value = "pageNum") int pageNum,
            @RequestParam(required = false, value = "pageSize") int pageSize,
            @RequestParam(required = false, value = "userName") String userName,
            @RequestParam(required = false, value = "phonenumber") String phonenumber,
            @RequestParam(required = false, value = "status") String status
    ) {
        return userService.listAll(pageNum, pageSize, userName, phonenumber, status);
    }

    @GetMapping("/{id}")
    public ResponseResult getOneUser(@PathVariable("id") Long id){
        return userService.getOneUser(id);
    }
}
