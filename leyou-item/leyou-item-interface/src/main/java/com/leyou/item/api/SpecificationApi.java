package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("spec")
public interface SpecificationApi {

    /**
     * 根据条件查询参数组的方法
     * @param gid 规格组id
     * @param cid 商品分类id
     * @param generic 是否搜索
     * @param searching 是否为通用属性
     * @return ResponseEntity<List<com.leyou.item.pojo.SpecParam>>
     */
    @GetMapping("/params")
    public List<SpecParam> queryParams(
            @RequestParam(value = "gid", required = false)Long gid,
            @RequestParam(value = "cid", required = false)Long cid,
            @RequestParam(value = "generic", required = false)Boolean generic,
            @RequestParam(value = "searching", required = false)Boolean searching
    );


    /**
     * 根据分类id查询规格参数组和组下参数的方法
     * @param cid 分类id
     * @return List<SpecGroup>
     */
    @GetMapping("/group/param/{cid}")
    public List<SpecGroup> queryGroupWithParam(@PathVariable("cid") Long cid);

}