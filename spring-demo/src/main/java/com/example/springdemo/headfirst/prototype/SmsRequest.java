package com.example.springdemo.headfirst.prototype;

import java.util.Map;

/**
 * 请求第三方平台对象 注意 必须 实现Cloneable 重写clone方法
 */
public class SmsRequest implements Cloneable{

    /**
     * 用户手机
     */
    private Long userPhone;

    /**
     * 短信模版
     */
    private String templateContent;

    /**
     * 短信类型 1-营销短信 2-通知短信
     */
    private Integer type;


    public Long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(Long userPhone) {
        this.userPhone = userPhone;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public SmsRequest(Long userPhone, Integer type,  String templateContent) {
        this.userPhone = userPhone;
        this.type = type;
        this.templateContent = templateContent;
    }

    public void replaceVariables (Map<String, String> variables) {
        String content = this.templateContent;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String variablesName = entry.getKey();
            String variablesValue = entry.getValue();
            content = content.replace("{" + variablesName + "}", variablesValue);
        }
        this.templateContent =  content;
    }

    @Override
    protected SmsRequest clone() throws CloneNotSupportedException {
        SmsRequest clone = null;
        try {
            //使用该方法克隆原型对象
            clone = (SmsRequest) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }

    @Override
    public String toString() {
        return "SmsRequest{" +
                "userPhone=" + userPhone +
                ", templateContent='" + templateContent + '\'' +
                ", type=" + type +
                '}';
    }
}
