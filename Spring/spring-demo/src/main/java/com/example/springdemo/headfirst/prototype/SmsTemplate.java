package com.example.springdemo.headfirst.prototype;

/**
 * 短信模版 包含了模版id 和模版内容
 */
public class SmsTemplate {

    /**
     * 短信模版id
     */
    private String templateId;

    /**
     * 短信模版内容
     */
    private String templateContent;

    /**
     * 短信类型 1-营销短信 2-通知短信
     */
    private Integer type;


    public SmsTemplate(String templateId, Integer type,  String templateContent) {
        this.templateId = templateId;
        this.templateContent = templateContent;
        this.type = type;
    }

    public SmsTemplate() {
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
