package com.tineco.market.client.constants;

public enum ActivityType {

    NORMAL(0,"不参加活动"),
    DISCOUNT(1,"满折活动"),

    ;
    private final Integer code;
    private final String message;

    ActivityType(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static String getMessageByCode(Integer code) {
        if (null == code) {
            return null;
        }
        for (ActivityType value : values()) {
            if (value.getCode().equals(code)) {
                return value.message;
            }
        }
        return null;
    }

    public static ActivityType getByCode(Integer code) {
        if (null == code) {
            return null;
        }
        for (ActivityType value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
