package com.leyou.sms;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utils.SmsUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SmsTest {
    @Autowired
    private SmsProperties smsProperties;

    @Autowired
    private SmsUtils smsUtils;

    @Test
    public void test(){
        //发送消息
        try {
            SendSmsResponse sendSmsResponse = this.smsUtils.sendSms("13215113539", "123456", smsProperties.getSignName(), smsProperties.getVerifyCodeTemplate());
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
