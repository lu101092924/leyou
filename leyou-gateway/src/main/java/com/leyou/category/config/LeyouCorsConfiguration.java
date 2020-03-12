package com.leyou.category.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class LeyouCorsConfiguration {

    @Bean
    public CorsFilter corsFilter(){
        //初始化cors配置对象
        CorsConfiguration configuration=new CorsConfiguration();
        //允许跨域的域名，如果要携带cookie，不能写*，*代表所有域名都能跨域访问
        configuration.addAllowedOrigin("http://manage.leyou.com");
        configuration.addAllowedOrigin("http://www.leyou.com"); //添加www.leyou.com允许跨域
        configuration.setAllowCredentials(true); //允许携带cookie
        configuration.addAllowedMethod("*"); //允许的请求方式,*代表所有的请求方式，GET、POST、PUT....
        configuration.addAllowedHeader("*"); //允许携带任何头信息
        //添加映射路径，我们拦截一切请求
        //初始化cors配置源对象
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource=new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",configuration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
