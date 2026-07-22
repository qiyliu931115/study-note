package com.example.agentscope;

import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class TimeTools {

    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss VV");

    @Tool(name = "get_current_time", description = "获取指定 IANA 时区的当前日期和时间")
    public String getCurrentTime(
            @ToolParam(name = "zoneId", description = "IANA 时区，例如 Asia/Shanghai") String zoneId) {
        if (zoneId == null || zoneId.isBlank()) {
            return "时区不能为空，请使用 Asia/Shanghai 这样的 IANA 时区名称";
        }

        try {
            return ZonedDateTime.now(java.time.ZoneId.of(zoneId.trim())).format(OUTPUT_FORMATTER);
        } catch (DateTimeException exception) {
            return "无法识别时区 '%s'，请使用 Asia/Shanghai 这样的 IANA 时区名称"
                    .formatted(zoneId);
        }
    }
}
