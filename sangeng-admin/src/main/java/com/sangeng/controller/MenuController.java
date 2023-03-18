package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Menu;
import com.sangeng.domain.vo.MenuVo;
import com.sangeng.service.MenuService;
import com.sangeng.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

    @GetMapping("/list")
    public ResponseResult listAllMenu(
             @RequestParam(required = false, value = "status") String status
            ,@RequestParam(required = false, value = "menuName") String menuName
    ) {
        return menuService.listAllMenu(status, menuName);
    }

    @PostMapping
    public ResponseResult insert(@RequestBody Menu menu) {
        menuService.save(menu);
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult select(@PathVariable("id") Long id) {
        Menu menu = menuService.getById(id);
        MenuVo menuVo = BeanCopyUtils.copyBean(menu, MenuVo.class);
        return ResponseResult.okResult(menuVo);
    }

    @PutMapping
    public ResponseResult updateMenu(@RequestBody Menu menu) {
        return menuService.updateMenu(menu);
    }

    @DeleteMapping("/{menuId}")
    public ResponseResult deleteMenu(@PathVariable("menuId") Long menuId) {
        return menuService.deleteMenu(menuId);
    }

    @GetMapping("/treeselect")
    public ResponseResult treeselect() {
        return menuService.treeselect();
    }
}
