package com.leyou.item.controller;

import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/spec")
public class SpecParamController {

    @Autowired
    private SpecParamService specParamService;

    /**
     * 根据条件查询参数组的方法
     * @param gid 规格组id
     * @param cid 商品分类id
     * @param generic 是否搜索
     * @param searching 是否为通用属性
     * @return ResponseEntity<List<com.leyou.item.pojo.SpecParam>>
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> queryParams(
            @RequestParam(value = "gid", required = false)Long gid,
            @RequestParam(value = "cid", required = false)Long cid,
            @RequestParam(value = "generic", required = false)Boolean generic,
            @RequestParam(value = "searching", required = false)Boolean searching
    ){
        List<SpecParam> specParams=this.specParamService.queryParams(gid,cid,generic,searching);

        if (CollectionUtils.isEmpty(specParams)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specParams);

    }

    /**
     * 新增参数的方法
     * @param specParam 新增的参数对象
     * @return ResponseEntity<Void> 成功201，失败500
     */
    @PostMapping("/param")
    public ResponseEntity<Void> insertParam(@RequestBody SpecParam specParam){
        this.specParamService.insertParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改参数的方法
     * @param specParam 修改的参数对象
     * @return ResponseEntity<Void> 成功201，失败500
     */
    @PutMapping("/param")
    public ResponseEntity<Void> updateParamById(@RequestBody SpecParam specParam){
        this.specParamService.updateParamById(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据id删除参数的方法
     * @param id 要删除的参数id
     * @return ResponseEntity<Void>删除成功204，失败500
     */
    @DeleteMapping("/param/{id}")
    public ResponseEntity<Void> deleteParamById(@PathVariable("id") Long id){
        this.specParamService.deleteParamById(id);
        return ResponseEntity.noContent().build();
    }
}
