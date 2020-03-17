package com.leyou.item.api;

import com.leyou.common.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {

    /**
     * 根据条件分页查询Spud的方法
     * @param key 搜索条件
     * @param saleable 是否上架，查询全部为null
     * @param page 当前页码
     * @param rows 每页记录数
     * @return ResponseEntity<PageResult<SpuBo>>
     */
    @GetMapping("/spu/page")
    public PageResult<SpuBo> querySpuByPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows
    );

    /**
     * 根据spuId查询SpuDetail信息的方法
     * @param spuId
     * @return SResponseEntity<SpuDetail>
     */
    @GetMapping("/spu/detail/{spuId}")
    public SpuDetail querySpuDetailBySpuId(@PathVariable("spuId")Long spuId);

    /**
     * 根据spuId查询Sku信息的方法
     * @param spuId
     * @return ResponseEntity<List<Sku>>
     */
    @GetMapping("sku/list")
    public List<Sku> querySkusBySpuId(@RequestParam("id") Long spuId);

    /**
     * 根据spuid查询spu的方法
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public Spu querySpuById(@PathVariable("id")Long id);

    /**
     * 根据skuId查询sku的方法
     * @param id skuId
     * @return
     */
    @GetMapping("sku/{id}")
    public Sku querySkuById(@PathVariable("id")Long id);
}
