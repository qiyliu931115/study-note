package com.example.springdemo.headfirst.prototype;

public class SmsTemplate {

    /**
     * 短信模版id
     */
    private String templateId;

    /**
     * 短信模版内容
     */
    private String templateContent;

    public SmsTemplate(String templateId, String templateContent) {
        this.templateId = templateId;
        this.templateContent = templateContent;
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
}
