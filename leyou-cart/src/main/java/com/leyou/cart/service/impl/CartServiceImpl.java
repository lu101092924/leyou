package com.leyou.cart.service.impl;

import com.leyou.auth.entity.UserInfo;
import com.leyou.cart.client.GoodsClient;
import com.leyou.cart.interceptor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import com.leyou.item.pojo.Sku;
import com.leyou.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GoodsClient goodsClient;

    private static final String KEY_PREFIX="leyou:cart:";

    @Override
    public void addCart(Cart cart) {
        // 获取登录用户
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        // Redis的key
        String key = KEY_PREFIX + userInfo.getId();
        // 获取hash操作对象
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);
        // 查询是否存在
        Long skuId = cart.getSkuId();
        Integer num = cart.getNum();

        if (hashOps.hasKey(skuId.toString())){
            // 存在，获取购物车数据,修改数量
            String cartJson = hashOps.get(skuId.toString()).toString();
            cart = JsonUtils.parse(cartJson, Cart.class);
            cart.setNum(cart.getNum()+num);
        }else{
            // 不存在，新增购物车数据
            cart.setUserId(userInfo.getId());

            //其他数据要去查询获取
            Sku sku = this.goodsClient.querySkuById(skuId);

            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setPrice(sku.getPrice());
            cart.setTitle(sku.getTitle());
        }
        // 将购物车数据写入redis
        hashOps.put(cart.getSkuId().toString(), JsonUtils.serialize(cart));
    }

    @Override
    public List<Cart> queryCarts() {
        //获取登录用户信息
        UserInfo user = LoginInterceptor.getUserInfo();
        
        //判断用户是否有购物车信息，没有之间返回null
        if(!this.redisTemplate.hasKey(KEY_PREFIX + user.getId())){
            return null;
        }

        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(KEY_PREFIX + user.getId());
        //获取购物车Map中所有的Cart记录
        List<Object> carts = hashOps.values();

        //如果购物车集合为空，返回null
        if(CollectionUtils.isEmpty(carts)){
            return null;
        }

        //把List<Object>集合转化为List<Cart>集合
       return carts.stream().map(cart -> JsonUtils.parse(cart.toString(),Cart.class)).collect(Collectors.toList());
    }

    @Override
    public void updateCarts(Cart cart) {
        UserInfo user = LoginInterceptor.getUserInfo();
        //判断用户是否有购物车信息，没有之间返回null
        if(!this.redisTemplate.hasKey(KEY_PREFIX + user.getId())){
            return ;
        }
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(KEY_PREFIX + user.getId());
        // 获取购物车信息
        String cartJson = hashOps.get(cart.getSkuId().toString()).toString();

        Integer num=cart.getNum();

        cart = JsonUtils.parse(cartJson, Cart.class);
        // 更新数量
        cart.setNum(num);
        // 写入购物车
        hashOps.put(cart.getSkuId().toString().toString(),JsonUtils.serialize(cart));
    }

    @Override
    public void deleteCart(String skuId) {
        // 获取登录用户
        UserInfo user = LoginInterceptor.getUserInfo();
        String key = KEY_PREFIX + user.getId();
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        hashOps.delete(skuId);
    }
}
