package com.leyou.goods.service;

import java.util.Map;

public interface GoodsService {

    /**
     * 根据spuId查询商品信息的方法
     * @param spuId
     * @return
     */
    Map<String,Object> loadDate(Long spuId);
}
