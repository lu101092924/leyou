package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/brand")
public interface BrandApi {
    /**
     * 根据id查询品牌信息的方法
     * @param id 品牌id
     * @return Brand对象
     */
    @GetMapping("{id}")
    public Brand queryBrandById(@PathVariable("id") Long id);
}
