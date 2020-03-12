package com.leyou.sms.listener;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utils.SmsUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SMSListener {

    @Autowired
    private SmsProperties smsProperties;

    @Autowired
    private SmsUtils smsUtils;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.SMS.QUEUE",durable = "true"),
            exchange = @Exchange(value = "LEYOU.SMS.EXCHANGE",ignoreDeclarationExceptions = "true"),
            key = {"verifycode_sms"}
    ))
    public void listenerSendSMS(Map<String,String> msg) throws ClientException {

        if(msg == null || msg.size() <=0 ){
            //参数中没有值则不处理
            return;
        }

        String phone = msg.get("phone");
        String code = msg.get("code");
        //判断获取集合中参数是否为空
        if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)){
            return;
        }

        //发送消息
        SendSmsResponse sendSmsResponse = this.smsUtils.sendSms(phone, code, smsProperties.getSignName(), smsProperties.getVerifyCodeTemplate());

    }

}
