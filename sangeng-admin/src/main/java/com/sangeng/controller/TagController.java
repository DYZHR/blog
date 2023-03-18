package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.TagListDto;
import com.sangeng.domain.entity.Tag;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.domain.vo.TagVo;
import com.sangeng.service.TagService;
import com.sangeng.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content/tag")
public class TagController {
    @Autowired
    private TagService tagService;

    @GetMapping("/list")
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, TagListDto tagListDto){
        return tagService.pageTagList(pageNum,pageSize,tagListDto);
    }
    @PostMapping
    public ResponseResult insert(@RequestBody Tag tag) {
        tagService.save(tag);
        return ResponseResult.okResult();
    }
    @DeleteMapping("/{id}")
    public ResponseResult delete(@PathVariable("id") Long id) {
        tagService.removeById(id);
        return ResponseResult.okResult();
    }
    @GetMapping("/{id}")
    public ResponseResult<TagVo> getTag(@PathVariable("id") Long id) {
        Tag tag = tagService.getById(id);
        TagVo tagVo = BeanCopyUtils.copyBean(tag, TagVo.class);
        return ResponseResult.okResult(tagVo);
    }
    @PutMapping
    public ResponseResult updateTag(@RequestBody Tag tag) {
        tagService.updateById(tag);
        return ResponseResult.okResult();
    }
    @GetMapping("/listAllTag")
    public ResponseResult listAllTag() {
        List<TagVo> tagVos = tagService.listAllTag();
        return ResponseResult.okResult(tagVos);
    }
}