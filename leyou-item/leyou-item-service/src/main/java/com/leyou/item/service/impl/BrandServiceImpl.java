package com.leyou.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 根据id查询品牌信息的方法
     * @param id 品牌id
     * @return Brand对象
     */
    @Override
    public Brand queryBrandById(Long id) {
        return this.brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据分类id查询品牌信息的方法
     * @param cid 分类id
     * @return List<Brand>
     */
    @Override
    public List<Brand> queryBrandsByCid(Long cid) {
        return this.brandMapper.selectBrandsByCid(cid);
    }

    /**
     * 保存品牌信息的方法
     * @param brand
     * @param cids
     */
    @Transactional
    @Override
    public void saveBrand(Brand brand, List<Long> cids) {
        //先新增Brand
        this.brandMapper.insertSelective(brand);
        //新增tb_category_brand中间表

        cids.forEach(cid -> {
            this.brandMapper.insertCategoryAndBrand(cid,brand.getId());
        });

    }

    /**
     *根据查询条件分页并排序查询品牌信息的方法
     * @param key 条件
     * @param page 当前页码
     * @param rows 每页记录数数
     * @param sortBy 根据什么排序（排序字段名）
     * @param desc 是否降序排列
     * @return 查询的分页结果对象
     */
    @Override
    public PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        //初始化example对象
        Example example=new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();

        //根据name模糊查询，或者根据首字母查询
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("name","%"+key+"%").orEqualTo("letter",key);
        }

        //添加分页条件
        PageHelper.startPage(page,rows);
        //添加排序条件
        if(StringUtils.isNotBlank(sortBy)){
            example.setOrderByClause(sortBy+" "+(desc?"desc":"asc"));
        }

        List<Brand> brands = brandMapper.selectByExample(example);
        //包装成PageInfo
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        //包装成分页结果集返回
        return new PageResult<>(pageInfo.getTotal(),pageInfo.getList());
    }
}
