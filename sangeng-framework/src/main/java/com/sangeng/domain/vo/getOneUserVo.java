package com.sangeng.domain.vo;

import com.sangeng.domain.entity.Role;
import com.sangeng.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class getOneUserVo {
    private List<Long> roleIds;
    private List<Role> roles;
    private User user;
}
