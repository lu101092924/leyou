package com.leyou.goods.service.impl;

import com.leyou.goods.service.GoodsHtmlService;
import com.leyou.goods.service.GoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

@Service
public class GoodsHtmlServiceImpl implements GoodsHtmlService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private GoodsService goodsService;

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsHtmlService.class);

    @Override
    public void createHtml(Long spuId) {

        PrintWriter writer =null;
        try {
            //获取页面数据
            Map<String, Object> map = this.goodsService.loadDate(spuId);

            //创建thymeleaf上下文对象
            Context context=new Context();
            //把数据放入上下文对象
            context.setVariables(map);

            //创建文件流
            File file = new File("D:\\utils\\nginx\\nginx-1.14.0\\html\\item\\"+spuId+".html");
            writer = new PrintWriter(file);
            //执行页面静态方法
            templateEngine.process("item",context,writer);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("页面静态化出错：{}，"+ e, spuId);
        } finally {
            if (writer!=null){
                writer.close();
            }
        }
    }
}
