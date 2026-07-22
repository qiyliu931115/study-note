package com.example.agentscope;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeToolsTest {

    private final TimeTools timeTools = new TimeTools();

    @Test
    void returnsTimeForValidZone() {
        String result = timeTools.getCurrentTime("Asia/Shanghai");

        assertTrue(result.endsWith("Asia/Shanghai"));
    }

    @Test
    void returnsHelpfulMessageForInvalidZone() {
        String result = timeTools.getCurrentTime("invalid-zone");

        assertTrue(result.contains("无法识别时区"));
    }
}
