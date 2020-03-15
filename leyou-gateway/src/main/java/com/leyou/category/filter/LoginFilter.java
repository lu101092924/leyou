package com.leyou.category.filter;

import com.leyou.auth.utils.JwtUtils;
import com.leyou.category.config.FilterProperties;
import com.leyou.category.config.JwtProperties;
import com.leyou.utils.CookieUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        //初始化上下文对象
        RequestContext context = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = context.getRequest();
        //获取当前的请求路径
        String url = request.getRequestURL().toString();
        // 判断白名单
        // 遍历允许访问的路径
        for (String path: this.filterProperties.getAllowPaths()) {
            // 然后判断是否是符合
            if (StringUtils.contains(url,path)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        //初始化上下文对象
        RequestContext context = RequestContext.getCurrentContext();

        //获取token
        String token = CookieUtils.getCookieValue(context.getRequest(), this.jwtProperties.getCookieName());

        if(StringUtils.isBlank(token)){
            //不转发请求
            context.setSendZuulResponse(false);
            //设置响应状态码
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());

        }

        //校验token
        try {
            // 校验通过什么都不做，即放行
            JwtUtils.getInfoFromToken(token,jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            //不转发请求
            context.setSendZuulResponse(false);
            //设置响应状态码
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }

        return null;
    }
}
