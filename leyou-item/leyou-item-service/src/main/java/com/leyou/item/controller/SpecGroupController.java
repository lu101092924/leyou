package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.service.SpecGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/spec")
public class SpecGroupController {

    @Autowired
    private SpecGroupService specGroupService;

    ///item/spec/groups/4

    /**
     * 根据分类id查询参数组
     * @param cid 分类id
     * @return  ResponseEntity<List<com.leyou.item.pojo.SpecGroup>></>
     */
    @GetMapping("/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsById(@PathVariable("cid")Long cid){
        List<SpecGroup> groups=this.specGroupService.queryGroupById(cid);

        if(CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(groups);
    }

    /**
     * 新增分组方法
     * @param specGroup 新增的分组对象
     * @return ResponseEntity<Void> 成功201，错误500
     */
    @PostMapping("/group")
    public ResponseEntity<Void> saveGroup(@RequestBody SpecGroup specGroup){
        System.out.println(specGroup);
        this.specGroupService.saveGroup(specGroup);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改分组名字的方法
     * @param specGroup 修改分组对象
     * @return ResponseEntity<Void>成功201，错误500
     */
    @PutMapping("/group")
    public ResponseEntity<Void> updateGroupById(@RequestBody SpecGroup specGroup){
        this.specGroupService.updateGroupById(specGroup);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据分组id删除分组信息的方法
     * @param gid 分组id
     * @return ResponseEntity<Void> 删除成功204，失败500
     */
    @DeleteMapping("/group/{gid}")
    public ResponseEntity<Void> deleteGroupById(@PathVariable("gid")Long gid){
        this.specGroupService.deleteGroupById(gid);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据分类id查询规格参数组和组下参数的方法
     * @param cid
     * @return
     */
    @GetMapping("/group/param/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupWithParam(@PathVariable("cid") Long cid){
        List<SpecGroup> groups=this.specGroupService.queryGroupWithParam(cid);
        if(CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(groups);
    }

}
