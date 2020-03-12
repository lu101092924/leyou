package com.leyou.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private GoodsRepository goodsRepository;

    private static final ObjectMapper MAPPER=new ObjectMapper();


    public Goods buildGoods(Spu spu) throws IOException {
        Goods goods = new Goods();

        //根据分类的id查询分类名称
        List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //根据品牌id查询品牌名称
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

        //根据spuid查询所有的sku
        List<Sku> skuList = this.goodsClient.querySkusBySpuId(spu.getId());
       // System.out.println(spu.getId());
        //获取所有sku价格
        List<Long> prices=new ArrayList<>();

        //收集sku的必要字段信息
        List<Map<String,Object>> skuMapList=new ArrayList<>();

        skuList.forEach(sku -> {
            //添加查询到的价格到集合中
            prices.add(sku.getPrice());

            Map<String,Object> map=new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            //获取sku中的图片，数据库的的图片可能时多张，多张是以逗号（,）分隔，我们有多张图片时取第一张
            map.put("image",StringUtils.isBlank(sku.getImages()) ? "":StringUtils.split(sku.getImages(),",")[0]);

            skuMapList.add(map);

        });


        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        //拼接all字段，需要分类名称及品牌名称
        goods.setAll(spu.getTitle()+" "+ StringUtils.join(names," ")+" "+brand.getName());
        //获取spu下面的所有sku的价格
        goods.setPrice(prices);
        //获取spu下的所有sku,并转化成json字符串
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));

        //获取所有查询的规格参数
        //根据cid查询所有的搜索规格参数
        List<SpecParam> specParams = this.specificationClient.queryParams(null, spu.getCid3(), null, true);
        //根据spuId查询spudetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        //把通用的规格参数值进行反序列化
        Map<String, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {});
        //把特殊的规格参数值进行反序列化
        Map<String, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<Object>>>() {});
        // 定义map接收{规格参数名，规格参数值}
        Map<String,Object> specsMap=new HashMap<>();

        specParams.forEach(param->{
            // 判断是否通用规格参数
            if(param.getGeneric()){
                // 获取通用规格参数值
                String value=genericSpecMap.get(param.getId().toString()).toString();
                // 判断是否是数值类型
                if (param.getNumeric()){
                    // 如果是数值的话，判断该数值落在那个区间
                    value = chooseSegment(value, param);
                }
                specsMap.put(param.getName(),value);

            }else{
                // 获取特殊规格参数值
                List<Object>  value= specialSpecMap.get(param.getId().toString());
                specsMap.put(param.getName(),value);
            }
        });

        goods.setSpecs(specsMap);

        return goods;
    }

    /**
     *
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * 根据条件查询商品信息
     * @param searchRequest 搜索条件对象
     * @return SearchResult
     */
    public SearchResult search(SearchRequest searchRequest) {
        //// 判断是否有搜索条件，如果没有，直接返回null。不允许搜索全部商品
        if(StringUtils.isBlank(searchRequest.getKey())){
            return null;
        }
        // 构建自定义查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //添加查询条件
       // QueryBuilder basicQuery= QueryBuilders.matchQuery("all", searchRequest.getKey()).operator(Operator.AND);
        BoolQueryBuilder basicQuery=buildBooleanQueryBuilder(searchRequest);
        //// 对key进行全文检索查询
        queryBuilder.withQuery(basicQuery);

        //添加分页,页码从0开始
        queryBuilder.withPageable(PageRequest.of(searchRequest.getPage()-1,searchRequest.getSize()));

        // 通过sourceFilter设置返回的结果字段,我们只需要id、skus、subTitle
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));

        //添加分类和品牌的聚合
        String categoryAggName="categories";
        String brandAggName="brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        //执行查询，获取结果
        AggregatedPage<Goods> search = (AggregatedPage<Goods>)this.goodsRepository.search(queryBuilder.build());

        //获取聚合结果集并解析
        List<Map<String,Object>> categories=getCategoryAggResult(search.getAggregation(categoryAggName));

        List<Brand> brands=getBrandAggResult(search.getAggregation(brandAggName));

        //判断分类聚合的结果大小，等于1则聚合
        List<Map<String,Object>> specs=null;
        if (!CollectionUtils.isEmpty(categories) && categories.size()==1){
            specs=getParamAggResult((Long)categories.get(0).get("id"),basicQuery);
        }

        //封装结果并返回
        SearchResult searchResult = new SearchResult(search.getTotalElements(),search.getTotalPages(),search.getContent(),categories,brands,specs);
        return searchResult;
    }

    /**
     * 构建布尔查询的方法
     * @param searchRequest
     * @return
     */
    private BoolQueryBuilder buildBooleanQueryBuilder(SearchRequest searchRequest) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //给布尔查询添加基本查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all",searchRequest.getKey()).operator(Operator.AND));

        //添加过滤条件
        if(CollectionUtils.isEmpty(searchRequest.getFilter()) || searchRequest.getFilter().size()==0){
            return boolQueryBuilder;
        }

        for (Map.Entry<String,Object> entry : searchRequest.getFilter().entrySet()){
            String key = entry.getKey();

            // 如果过滤条件是“品牌”, 过滤的字段名：brandId
            if (StringUtils.equals("品牌", key)) {
                key = "brandId";
            } else if (StringUtils.equals("分类", key)) {
                // 如果是“分类”，过滤字段名：cid3
                key = "cid3";
            } else {
                // 如果是规格参数名，过滤字段名：specs.key.keyword
                key = "specs." + key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key,entry.getValue()));
        }

        return boolQueryBuilder;

    }


    /**
     * 根据查询条件聚合规格参数的方法
     * @param cid 分类id
     * @param basicQuery 查询构建器
     * @return
     */
    private List<Map<String,Object>> getParamAggResult(Long cid, QueryBuilder basicQuery) {
        //自定义查询对象构建
        NativeSearchQueryBuilder queryBuilder=new NativeSearchQueryBuilder();
        //添加基本查询条件
        queryBuilder.withQuery(basicQuery);
        //查询要聚合的规格参数
        List<SpecParam> specParams = specificationClient.queryParams(null, cid, null, true);
        //添加规格参数的聚合
        specParams.forEach(specParam -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs."+specParam.getName()+".keyword"));
        });
        //添加结果集过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));
        //执行聚合查询
        AggregatedPage<Goods> aggregatedPage = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

        List<Map<String,Object>> specs=new ArrayList<>();
        //解析聚合结果集, key:聚合名称(规格参数名)  value=集合对象
        Map<String, Aggregation> aggregationMap = aggregatedPage.getAggregations().asMap();

        for (Map.Entry<String,Aggregation> entry : aggregationMap.entrySet()){
            //初始化一个map {k规格参数名: options:聚合的规格参数值}
            Map<String,Object> map=new HashMap<>();
            map.put("k",entry.getKey());
            //初始化一个options集合，收集桶中的key
            List<String> options=new ArrayList<>();
            //获取聚合
            StringTerms terms=(StringTerms)entry.getValue();
            //获取桶集合
            terms.getBuckets().forEach(bucket -> {
                options.add(bucket.getKeyAsString());
            });
            map.put("options",options);

            specs.add(map);
        }

        return  specs;

    }

    /**
     * 解析品牌聚合结果集
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        LongTerms terms =(LongTerms)aggregation;


        //获取聚合中的桶，并添加到集合中,简写方式

        return terms.getBuckets().stream().map(bucket -> {
            //获取桶中的品牌id查询品牌信息
            return this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue());
        }).collect(Collectors.toList());

//        List<Brand> brands=new ArrayList<>();
//        terms.getBuckets().forEach(bucket -> {
//            //获取桶中的品牌id查询品牌信息
//            Brand brand = this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue());
//            brands.add(brand);
//        });
//        return  brands;

    }

    /**
     * 解析分类聚合结果集
     * @param aggregation
     * @return
     */
    private List<Map<String,Object>> getCategoryAggResult(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;

        return terms.getBuckets().stream().map(bucket -> {
            Map<String,Object> map=new HashMap<>();
            //获取桶中的分类id查询分类名称
            Long cId=bucket.getKeyAsNumber().longValue();
            List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(cId));
            //向map集合中添加分类id跟分类名称的键值对
            map.put("id",cId);
            map.put("name",names.get(0));

            return map;
        }).collect(Collectors.toList());

    }


    /**
     * 根据spuId保存信息跟更新信息的方法
     * @param id spuId
     */
    public void save(Long id) throws IOException {

        Spu spu = this.goodsClient.querySpuById(id);
        Goods goods = this.buildGoods(spu);
        this.goodsRepository.save(goods);
    }
}
