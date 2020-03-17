package com.leyou.cart.service;

import com.leyou.cart.pojo.Cart;

import java.util.List;

public interface CartService {

    /**
     * 添加购物车信息的方法
     * @param cart
     */
    void addCart(Cart cart);

    List<Cart> queryCarts();

    void updateCarts(Cart cart);

    void deleteCart(String skuId);
}
