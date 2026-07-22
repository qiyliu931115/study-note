package com.example.agentscope;

import java.util.Map;
import java.util.Objects;

record DemoSettings(String apiKey, String modelName) {

    private static final String API_KEY_VARIABLE = "DASHSCOPE_API_KEY";
    private static final String MODEL_VARIABLE = "DASHSCOPE_MODEL";
    private static final String DEFAULT_MODEL = "qwen3-max";

    DemoSettings {
        apiKey = requireText(apiKey, API_KEY_VARIABLE + " 不能为空");
        modelName = requireText(modelName, MODEL_VARIABLE + " 不能为空");
    }

    static DemoSettings fromEnvironment(Map<String, String> environment) {
        Objects.requireNonNull(environment, "environment 不能为空");

        String modelName = environment.getOrDefault(MODEL_VARIABLE, DEFAULT_MODEL);
        return new DemoSettings(environment.get(API_KEY_VARIABLE), modelName);
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
