package com.example.agentscope;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DemoSettingsTest {

    @Test
    void usesDefaultModelWhenModelVariableIsMissing() {
        DemoSettings settings = DemoSettings.fromEnvironment(Map.of("DASHSCOPE_API_KEY", "test-key"));

        assertEquals("test-key", settings.apiKey());
        assertEquals("qwen3-max", settings.modelName());
    }

    @Test
    void usesConfiguredModel() {
        DemoSettings settings = DemoSettings.fromEnvironment(Map.of(
                "DASHSCOPE_API_KEY", "test-key",
                "DASHSCOPE_MODEL", "qwen-plus"));

        assertEquals("qwen-plus", settings.modelName());
    }

    @Test
    void rejectsMissingApiKey() {
        assertThrows(IllegalArgumentException.class, () -> DemoSettings.fromEnvironment(Map.of()));
    }
}
