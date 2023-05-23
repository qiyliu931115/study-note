package com.example.springdemo.headfirst.prototype;

import java.util.Map;

/**
 * 请求对象 注意 必须 实现Cloneable 重写clone方法
 */
public class SmsRequest implements Cloneable{

    /**
     * 用户手机
     */
    private Long userPhone;

    /**
     * 短信内容
     */
    private String content;

    /**
     * 短信模版
     */
    private String templateContent;


    public Long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(Long userPhone) {
        this.userPhone = userPhone;
    }

    public String getContent() {
        return content;
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public SmsRequest(Long userPhone, String content) {
        this.userPhone = userPhone;
        this.content = content;
    }

    public SmsRequest(Long userPhone, String content, String templateContent) {
        this.userPhone = userPhone;
        this.content = content;
        this.templateContent = templateContent;
    }

    /**
     * 替换短信模版到占位符
     * @param variables
     */
    public void replaceVariables (Map<String, String> variables) {
        String content = this.templateContent;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String variablesName = entry.getKey();
            String variablesValue = entry.getValue();
            content = content.replace("{" + variablesName + "}", variablesValue);
        }
        this.content =  content;
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
                ", content='" + content + '\'' +
                '}';
    }
}
