package com.example.springdemo.headfirst.prototype;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SmsService {

    private final static String NAME = "name";

    private final static String COUPON = "coupon";

    public static void main(String[] args) throws CloneNotSupportedException {
        SmsTemplate smsTemplate = new SmsTemplate("1",
                "尊敬到{name}先生/女士，您的{coupon}元优惠券已发放到您到券包中，请登录添可小程序查看。");

        Map<String, Map<String, String>> variablesMap  = new HashMap<>();
        Map<String, String> valueMap = new HashMap<>();
        valueMap.put(NAME, "爱因斯坦");
        valueMap.put(COUPON, "8");
        variablesMap.put("18866665555", valueMap);


        Map<String, String> valueMap2 = new HashMap<>();
        valueMap2.put(NAME, "牛顿");
        valueMap2.put(COUPON, "10");
        variablesMap.put("18866664444", valueMap2);

        sendSms(variablesMap, smsTemplate);
    }

    /**
     *
     * @param variablesMap key 手机号 value 占位符和替换值到键值对
     * @param template 短信模版
     * @throws CloneNotSupportedException
     */
    public static void sendSms (Map<String, Map<String, String>> variablesMap, SmsTemplate template) throws CloneNotSupportedException {
        String templateContent = template.getTemplateContent();
        SmsRequest prototype = new SmsRequest(null, templateContent);//创建原型对象

        for (Map.Entry<String, Map<String, String >> entry : variablesMap.entrySet()) {
            String phoneNumber = entry.getKey();
            Map<String, String> variables = entry.getValue();
            SmsRequest request = prototype.clone();//克隆原型对象
            request.replaceVariables(variables);
            request.setUserPhone(Long.valueOf(phoneNumber));
            sendSms(request);
        }

    }

    private static void sendSms (SmsRequest smsRequest) {
        System.out.println("sendSms" + smsRequest.toString());
    }


}
