# AgentScope Java Demo

一个基于 AgentScope Java v1 的最小 Maven 示例。项目创建了一个 `ReActAgent`，默认使用 DashScope 的 `qwen3-max` 模型，并注册了一个查询指定时区当前时间的本地工具。

## 环境要求

- JDK 17 或更高版本
- Maven 3.8 或更高版本
- DashScope API Key

## 配置

PowerShell：

```powershell
$env:DASHSCOPE_API_KEY = "你的 API Key"
$env:DASHSCOPE_MODEL = "qwen3-max" # 可选
```

`DASHSCOPE_MODEL` 未设置时默认使用 `qwen3-max`。API Key 只从环境变量读取，不要提交到代码仓库。

## 运行

```powershell
mvn clean test
mvn exec:java -Dexec.args="请告诉我 Asia/Shanghai 时区现在几点"
```

如果不传参数，程序会使用内置的示例问题。

## 目录结构

```text
src/main/java/com/example/agentscope/
├── AgentScopeDemoApplication.java  # 程序入口和 Agent 创建
├── DemoSettings.java               # 环境变量配置
└── TimeTools.java                  # Agent 可调用的本地工具
```

## 参考文档

- [AgentScope Java v1 安装](https://java.agentscope.io/v1/zh/docs/quickstart/installation.html)
- [创建 ReAct 智能体](https://java.agentscope.io/v1/zh/docs/quickstart/agent.html)
- [模型集成](https://java.agentscope.io/v1/zh/docs/task/model.html)
