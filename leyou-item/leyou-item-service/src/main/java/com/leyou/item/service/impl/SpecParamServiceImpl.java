package com.leyou.item.service.impl;

import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecParamServiceImpl implements SpecParamService {

    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 根据id删除参数的方法
     * @param id 要删除的参数id
     */
    @Override
    public void deleteParamById(Long id) {
        this.specParamMapper.deleteByPrimaryKey(id);

    }

    /**
     * 修改参数的方法
     * @param specParam 修改的参数对象
     */
    @Override
    public void updateParamById(SpecParam specParam) {
        this.specParamMapper.updateByPrimaryKey(specParam);
    }

    /**
     * 新增参数的方法
     * @param specParam 新增的参数对象
     */
    @Override
    public void insertParam(SpecParam specParam) {
        this.specParamMapper.insertSelective(specParam);
    }

    /**
     * 根据条件查询参数组的方法
     * @param gid 规格组id
     * @param cid 商品分类id
     * @param generic 是否搜索
     * @param searching 是否为通用属性
     * @return List<com.leyou.item.pojo.SpecParam>
     */
    @Override
    public List<SpecParam> queryParams(Long gid, Long cid, Boolean generic, Boolean searching) {
        SpecParam param = new SpecParam();
        param.setGroupId(gid);
        param.setCid(cid);
        param.setGeneric(generic);
        param.setSearching(searching);
        return this.specParamMapper.select(param);
    }
}
