package com.example.agentscope;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.model.DashScopeChatModel;
import io.agentscope.core.tool.Toolkit;

public final class AgentScopeDemoApplication {

    private static final String DEFAULT_PROMPT = "请告诉我 Asia/Shanghai 时区现在几点。";

    private AgentScopeDemoApplication() {
    }

    public static void main(String[] args) {
        try {
            DemoSettings settings = DemoSettings.fromEnvironment(System.getenv());
            ReActAgent agent = createAgent(settings);
            Msg response = agent.call(createMessage(args)).block();

            if (response == null) {
                throw new IllegalStateException("AgentScope 未返回响应");
            }
            System.out.println(response.getTextContent());
        } catch (IllegalArgumentException exception) {
            System.err.println("配置错误：" + exception.getMessage());
            System.err.println("请先设置环境变量 DASHSCOPE_API_KEY。示例：");
            System.err.println("$env:DASHSCOPE_API_KEY = \"你的 API Key\"");
            System.exit(1);
        }
    }

    private static ReActAgent createAgent(DemoSettings settings) {
        Toolkit toolkit = new Toolkit();
        toolkit.registerTool(new TimeTools());

        DashScopeChatModel model = DashScopeChatModel.builder()
                .apiKey(settings.apiKey())
                .modelName(settings.modelName())
                .build();

        return ReActAgent.builder()
                .name("DemoAssistant")
                .sysPrompt("你是一个简洁、可靠的中文助手。需要查询当前时间时，请调用工具。")
                .model(model)
                .toolkit(toolkit)
                .maxIters(5)
                .build();
    }

    private static Msg createMessage(String[] args) {
        String prompt = args.length == 0 ? DEFAULT_PROMPT : String.join(" ", args);
        return Msg.builder().textContent(prompt).build();
    }
}
