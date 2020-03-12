package com.leyou.item.service.impl;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecGroupService;
import com.leyou.item.service.SpecParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecGroupServiceImpl implements SpecGroupService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamService specParamService;
    /**
     * 根据分类id查询参数组
     * @param cid 分类id
     * @return List<com.leyou.item.pojo.SpecGroup></>
     */
    @Override
    public List<SpecGroup> queryGroupById(Long cid) {
        SpecGroup specGroup=new SpecGroup();
        specGroup.setCid(cid);

        List<SpecGroup> specGroups = specGroupMapper.select(specGroup);

        return specGroups;
    }

    /**
     * 新增分组方法
     * @param specGroup 新增的分组对象
     */
    @Override
    public void saveGroup(SpecGroup specGroup) {

        this.specGroupMapper.insertSelective(specGroup);
    }


    /**
     * 根据分组id删除分组信息的方法
     * @param gid 分组id
     */
    @Override
    public void deleteGroupById(Long gid) {
        this.specGroupMapper.deleteByPrimaryKey(gid);
    }

    /**
     * 修改分组名字的方法
     * @param specGroup 修改分组对象
     */
    @Override
    public void updateGroupById(SpecGroup specGroup) {
        this.specGroupMapper.updateByPrimaryKey(specGroup);
    }

    /**
     * 根据分类id查询规格参数组和组下参数的方法
     * @param cid
     * @return
     */
    @Override
    public List<SpecGroup> queryGroupWithParam(Long cid) {
        List<SpecGroup> groups = this.queryGroupById(cid);
        groups.forEach(group -> {
            List<SpecParam> specParams = this.specParamService.queryParams(group.getId(), null, null, null);

            group.setParams(specParams);
        });

        return groups;
    }
}
