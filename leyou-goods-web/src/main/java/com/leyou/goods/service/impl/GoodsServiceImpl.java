package com.leyou.goods.service.impl;

import com.leyou.goods.client.BrandClient;
import com.leyou.goods.client.CategoryClient;
import com.leyou.goods.client.GoodsClient;
import com.leyou.goods.client.SpecificationClient;
import com.leyou.goods.service.GoodsService;
import com.leyou.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Override
    public Map<String, Object> loadDate(Long spuId) {
        Map<String, Object> model=new HashMap<>();

        //查询spu
        Spu spu = this.goodsClient.querySpuById(spuId);

        //查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spuId);

        //查询分类信息
        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = this.categoryClient.queryNamesByIds(cids);
        List<Map<String, Object>> categories = new ArrayList<>();

        for (int i = 0; i < cids.size(); i++) {
            Map<String, Object> categoryMap = new HashMap<>();
            categoryMap.put("id",cids.get(i));
            categoryMap.put("name", names.get(i));

            categories.add(categoryMap);
        }

        //查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

        //查询skus
        List<Sku> skuList = this.goodsClient.querySkusBySpuId(spuId);

        //查询规格参数组
        List<SpecGroup> groups = this.specificationClient.queryGroupWithParam(spu.getCid3());

        //查询特殊的规格参数
        List<SpecParam> specParams = this.specificationClient.queryParams(null, spu.getCid3(), false, null);
        Map<Long, String> paramMap = new HashMap<>();
        specParams.forEach(param -> {
            paramMap.put(param.getId(), param.getName());
        });

        model.put("spu",spu);
        model.put("spuDetail",spuDetail);
        model.put("categories",categories);
        model.put("brand",brand);
        model.put("skus",skuList);
        model.put("groups",groups);
        model.put("paramMap",paramMap);

        return model;
    }
}
