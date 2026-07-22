# OpenCode 技术原理与源码解析

> 调研日期：2026-07-20  
> 调研对象：OpenCode（`anomalyco/opencode`，不是 OpenAI Codex）  
> 重点：Agent Harness、上下文构建、工具调用、权限、会话压缩与工程实现  
> 源码基线：调研日期当天的 GitHub `dev` 分支；项目迭代很快，具体实现可能继续变化

## 1. 一句话结论

OpenCode 本身不是大模型。它是一个运行在模型外部的 **AI Coding Agent Harness**：负责选择模型、拼装上下文、暴露工具、执行工具、记录状态、控制权限，并不断把执行结果反馈给模型，形成 Agent Loop。

```text
模型负责：理解、推理、决定下一步、生成工具参数

OpenCode 负责：
  上下文构建 + 模型适配 + 工具执行 + 权限审批
  + 会话状态 + 长上下文压缩 + 文件差异/回滚 + 客户端通信
```

它的核心不在“生成代码”这一个动作，而在于把不可靠、无状态的大模型调用，组织成一个能够观察环境、采取行动、得到反馈并继续迭代的软件执行系统。

## 2. 技术栈与代码结构

OpenCode 是一个以 TypeScript 为主的 monorepo，核心开发和构建使用 Bun。

主要技术组件：

| 层次 | 技术/实现 | 作用 |
| --- | --- | --- |
| 核心运行时 | TypeScript、Bun | CLI、Agent 和服务端核心 |
| 效应系统 | Effect | 依赖注入、异步流、资源生命周期、取消、重试和并发控制 |
| 模型统一层 | Vercel AI SDK；实验性原生 LLM Runtime | 统一不同模型服务商的流式输出和 Tool Calling |
| 模型目录 | Models.dev + OpenCode 配置 | 模型 ID、上下文窗口、服务商元数据和参数 |
| TUI | SolidJS + OpenTUI | 终端交互界面 |
| Web UI | SolidJS | 浏览器客户端 |
| Desktop | Tauri + Web UI | 桌面客户端 |
| HTTP 服务 | Effect HTTP + Node HTTP | OpenAPI、会话、工具和客户端通信 |
| 扩展协议 | MCP、LSP、插件、Skills | 外部工具、代码语义和自定义工作流 |

核心源码大致位于：

```text
packages/opencode/src/
├── session/
│   ├── prompt.ts          # 外层 Agent Loop、上下文组装入口
│   ├── processor.ts       # 消费 LLM 流事件、维护工具/文本状态
│   ├── llm.ts             # 模型调用和统一事件流
│   ├── instruction.ts     # AGENTS.md、CLAUDE.md、远程指令
│   ├── system.ts          # 模型模板、环境、Skills、MCP 指令
│   ├── compaction.ts      # 长会话裁剪与摘要压缩
│   └── tools.ts           # 把内置工具、插件和 MCP 装配给模型
├── tool/
│   ├── registry.ts        # 工具注册表
│   └── task.ts            # 子 Agent/子会话任务
├── permission/index.ts    # 权限规则与异步审批
├── agent/agent.ts         # build、plan、explore 等 Agent 定义
├── provider/              # 模型服务商与参数转换
├── mcp/                   # MCP 客户端和工具转换
├── lsp/                   # LSP 客户端
├── snapshot/              # 文件快照、diff 和回滚
└── server/                # HTTP/OpenAPI 服务端
```

## 3. 总体架构

```text
┌──────────────────────────────────────────────────────┐
│ TUI / Desktop / Web / IDE Extension / 自定义客户端 │
└──────────────────────────┬───────────────────────────┘
                           │ HTTP / OpenAPI / 事件通道
                           ▼
┌──────────────────────────────────────────────────────┐
│                OpenCode Server                       │
│ 会话 API、权限请求、状态事件、项目实例、客户端协调   │
└──────────────────────────┬───────────────────────────┘
                           ▼
┌──────────────────────────────────────────────────────┐
│                Session / Agent Runtime               │
│ Prompt Loop → Context Builder → LLM → Stream Processor│
└───────────────┬──────────────────────┬───────────────┘
                │                      │
                ▼                      ▼
┌──────────────────────────┐  ┌────────────────────────┐
│ Model Provider Layer     │  │ Tool Runtime           │
│ AI SDK / Native Runtime  │  │ FS/Shell/Git/LSP/MCP   │
│ OpenAI/Claude/Gemini/... │  │ Plugin/Skill/Sub-agent │
└──────────────────────────┘  └────────────────────────┘
```

客户端/服务端分离是 OpenCode 的重要设计。运行 `opencode` 时，并不只是启动一个终端程序：TUI 是客户端，核心能力运行在本地服务端。桌面端、Web、IDE 插件和 SDK 可以复用同一套服务端能力。

## 4. 一次请求到底经历什么

以“修复登录接口的空指针并运行测试”为例，真实链路可以概括为：

1. 客户端把用户消息写入 Session。
2. `session/prompt.ts` 进入外层循环，读取当前有效会话历史。
3. 根据消息选择 Agent 和模型。
4. 检查是否需要执行子任务或上下文压缩。
5. 创建本轮 Assistant Message，用于承载后续流式 Part。
6. 根据 Agent、模型和权限解析本轮可见工具。
7. 并行构造环境信息、项目指令、MCP 指令、Skills 列表和历史消息。
8. 模型适配层把这些内容转换成具体服务商能接受的请求。
9. 模型流式输出文本、推理片段或 Tool Call。
10. `session/processor.ts` 消费流事件，并持续写入会话状态。
11. 如果模型调用工具，OpenCode 先进行权限判断，再执行真实文件或命令操作。
12. 工具结果成为会话的一部分，下一轮重新发送给模型。
13. 模型根据结果决定继续读取、修改、测试，或输出最终答案。
14. 达到停止条件后退出循环，并生成文件变化信息。

关键点是：**每次工具执行结果都会重新成为模型的观察输入**。因此模型不是一次性给出完整脚本，而是在一个“观察—行动—反馈”的闭环中工作。

## 5. Agent Loop：核心控制循环

外层主循环位于 `session/prompt.ts` 的 `runLoop()`。省略错误处理和辅助逻辑后，可抽象为：

```typescript
while (true) {
  messages = loadEffectiveHistory(session)
  latest = findLatestUserAssistantAndTasks(messages)

  if (assistantHasFinishedWithoutPendingToolCalls(latest)) {
    break
  }

  model = resolveModel(latest.user)

  if (hasSubtask(latest)) {
    executeSubtask()
    continue
  }

  if (needsCompaction(latest)) {
    compactHistory()
    continue
  }

  agent = resolveAgent(latest.user.agent)
  tools = resolveTools(agent, model, permissions)
  system = buildSystemContext(agent, model, project)
  history = convertMessagesForModel(messages, model)

  result = processor.process({
    system,
    history,
    tools,
    model
  })

  if (result === "stop") break
  if (result === "compact") scheduleCompaction()
}
```

### 5.1 为什么外层还要循环

一次模型响应不一定代表任务结束。模型可能只返回：

```text
Tool Call: read({ filePath: "src/LoginService.ts" })
```

工具执行结束后，模型还没有给用户最终答案。外层循环需要再次构造请求，把读取结果附在历史中，让模型继续推理。

### 5.2 停止条件

典型停止条件包括：

- 模型返回正常完成原因，并且没有待处理 Tool Call。
- 权限被拒绝，且配置没有要求拒绝后继续。
- 发生不可恢复错误。
- 达到 Agent 的最大步骤数。
- 内容被模型服务商过滤。
- 结构化输出已经成功产生。
- 长上下文压缩本身也无法放入模型窗口。

源码还兼容某些服务商的异常行为：即使服务商把 finish reason 错误地标成 `stop`，只要消息中仍存在 Tool Call，循环仍会继续，把工具结果送回模型。

## 6. 内层 Processor：流式事件状态机

`session/processor.ts` 不是简单地等待模型返回整段 JSON，而是消费统一的 `LLMEvent` 流。

主要事件类型：

| 事件 | 处理方式 |
| --- | --- |
| `reasoning-start/delta/end` | 创建并增量更新 reasoning Part |
| `text-start/delta/end` | 创建并增量更新 text Part |
| `tool-input-start/delta/end` | 保存流式工具参数 |
| `tool-call` | 把工具状态切换为 running |
| `tool-result` | 保存输出、附件和元数据，标记 completed |
| `tool-error` | 保存错误，标记 error |
| `step-start` | 创建步骤快照 |
| `step-finish` | 统计 Token/费用、生成 patch、判断是否溢出 |
| `error` | 进入重试、压缩或停止分支 |

工具状态机大致是：

```text
pending
  ↓ 参数接收完成
running
  ├─→ completed：保存 title、output、metadata、attachments
  └─→ error：保存错误或 aborted 标记
```

流式 Part 会被持续写入 Session，因此 UI 能实时显示模型文字、推理片段、工具输入和工具结果，不需要等待整轮完成。

### 6.1 中断与清理

Processor 使用 `AbortController`、Effect 的 interruption/finalizer 和 Deferred 处理取消：

- 用户取消时中断模型请求。
- 等待仍在执行的工具短暂结束。
- 未完成的工具被标记为 `Tool execution aborted`。
- 当前文本和 reasoning Part 会补齐结束时间。
- Assistant Message 最终被标记完成或错误，避免会话永久停留在 running 状态。

### 6.2 重试

模型流发生可重试错误时，Processor 使用服务商相关的 Retry Policy 进行重试，并向客户端发布重试次数、下一次时间和原因。上下文溢出不会按普通网络错误处理，而会转入 Compaction 分支。

## 7. Prompt 与上下文是如何构建的

OpenCode 每一轮并不是简单发送“用户问题 + 全部代码”。它动态拼装多个上下文层。

```text
模型专用基础 Prompt
  + 当前模型与运行环境
  + AGENTS.md / CLAUDE.md / 自定义 instructions
  + 可用 MCP Server 的说明
  + 可用 Skills 的索引
  + 历史消息与工具结果
  + 当前用户消息
  + 工具 JSON Schema
```

### 7.1 模型专用 Prompt

`session/system.ts` 会根据模型 ID 选择不同模板：

```text
GPT / Codex / Claude / Gemini / Kimi / Trinity / 默认模板等
```

原因是各模型虽然都支持文本和工具调用，但它们对提示词风格、工具纪律、计划方式和输出格式的响应并不完全一致。OpenCode 没有假设“一套 System Prompt 对所有模型都最优”。

### 7.2 动态环境信息

环境块包含：

- 精确 provider/model ID
- 当前工作目录
- Workspace 根目录
- 是否为 Git 仓库
- 操作系统平台
- 当前日期
- 可访问的项目引用目录

这些信息减少模型在路径、平台命令和日期方面的猜测。

### 7.3 项目指令

`session/instruction.ts` 负责加载：

- 全局 `AGENTS.md`
- 项目 `AGENTS.md`
- 兼容 Claude Code 的 `CLAUDE.md`
- `opencode.json` 中声明的本地文件或远程 URL
- 读取某个文件时，其附近目录中的局部指令

系统级指令会在每轮构造时读取。对具体文件的局部指令则按需发现，并避免在同一消息中重复注入。

这体现了两层上下文策略：

```text
稳定的全局/项目规则：每轮作为 System Context
局部模块规则：读取相关文件时按需加载
```

### 7.4 工具定义也是上下文

工具不只是程序中的函数。模型必须看到每个工具的：

- 名称
- 自然语言描述
- JSON Schema 参数
- 哪些 Agent/子 Agent 可用

工具越多，Prompt Token 和模型选错工具的概率通常越高。因此 OpenCode 会按模型、Agent、权限和实验开关过滤工具。

## 8. 模型适配层

不同服务商在以下方面都有差异：

- 模型命名
- System/Developer Message 语义
- Tool Schema 限制
- Tool Call 流事件格式
- reasoning 输出
- Token 与缓存计费字段
- temperature、topP、最大输出等参数
- OpenAI Responses API 与 Chat Completions API 差异

OpenCode 使用两条运行路径：

### 8.1 默认路径：Vercel AI SDK

`session/llm.ts` 默认调用 AI SDK 的 `streamText()`：

```typescript
streamText({
  model,
  messages,
  tools,
  activeTools,
  toolChoice,
  providerOptions,
  abortSignal,
  maxOutputTokens
})
```

调用前会经过：

1. Provider 解析和认证加载。
2. `LLMRequestPrep.prepare()` 构建最终 System、Messages、Headers 和参数。
3. ProviderTransform 修改消息和 Tool Schema，以适配具体模型。
4. 插件 Hook 可以变换 System Prompt、消息或工具定义。
5. AI SDK 的 `fullStream` 被适配为 OpenCode 自己的统一 `LLMEvent`。

### 8.2 实验路径：Native LLM Runtime

OpenCode 还存在实验性原生 LLM Runtime。开启后，先判断当前服务商和模型是否支持；支持就直接返回统一 `LLMEvent` 流，不支持则自动回退到 AI SDK。

这是一种典型的 Adapter Seam：上层 Processor 不关心底层是 AI SDK 还是原生实现，因为两者最终都输出同一事件协议。

### 8.3 Tool Call 修复

源码会对部分无效 Tool Call 做有限修复，例如：

- 模型把工具名大小写写错时，尝试转成小写匹配。
- 无法匹配时转交 `invalid` 工具，将错误信息作为结构化结果返回。

这比直接让整个会话崩溃更容易让模型自行纠正。

## 9. 工具系统：模型如何真正操作电脑

`tool/registry.ts` 维护内置和插件工具，典型工具包括：

```text
read / glob / grep
edit / write / apply_patch
shell
task
webfetch / websearch
todo
skill
question
lsp
```

### 9.1 工具装配过程

`session/tools.ts` 将 Tool Definition 转成 AI SDK Tool：

```typescript
tools[id] = tool({
  description,
  inputSchema,
  execute(args, options) {
    checkPermission()
    plugin.before()
    result = realTool.execute(args)
    plugin.after()
    return normalizeAndTruncate(result)
  }
})
```

每次工具执行都会获得一个 `Tool.Context`，其中包含：

- Session ID、Message ID、Tool Call ID
- 当前 Agent 和模型
- Abort Signal
- 历史消息
- 更新工具状态的回调
- 权限请求函数

### 9.2 模型相关的工具选择

工具注册表会根据模型选择不同编辑工具。例如源码当前对部分 GPT 模型优先暴露 `apply_patch`，并隐藏 `edit/write`；其他模型则相反。

这说明“工具集合”也是模型适配的一部分。有些模型更擅长统一 Diff Patch，有些模型更稳定地调用按文件编辑工具。

### 9.3 输出截断

命令输出、文件内容或 MCP 返回可能非常大。OpenCode 会把工具输出通过 Truncate 层处理：

- 发送给模型的是截断后的内容。
- 元数据记录是否发生截断。
- 必要时保存完整输出路径，后续可以按需读取。

这是防止一次日志或大文件占满上下文窗口的关键机制。

### 9.4 插件 Hook

插件可以在多个位置介入：

```text
tool.definition
tool.execute.before
tool.execute.after
experimental.chat.system.transform
experimental.chat.messages.transform
experimental.session.compacting
```

因此插件既能新增工具，也能改变 Prompt、参数和工具结果。它很强大，但也意味着插件运行在高权限信任边界内。

## 10. 权限系统的真实原理

OpenCode 的权限不是操作系统沙箱，而是工具执行前的 **应用层规则引擎**。

规则结构：

```typescript
type Rule = {
  permission: string
  pattern: string
  action: "allow" | "ask" | "deny"
}

type Ruleset = Rule[]
```

### 10.1 匹配算法

源码核心逻辑可以简化为：

```typescript
function evaluate(permission, pattern, ...rulesets) {
  return rulesets
    .flat()
    .findLast(rule =>
      wildcardMatch(permission, rule.permission) &&
      wildcardMatch(pattern, rule.pattern)
    ) ?? { action: "ask" }
}
```

重要语义：

1. **最后一个匹配规则获胜**，所以规则顺序会改变结果。
2. Agent 规则与 Session 规则会合并，后合并的规则优先。
3. 没有规则匹配时默认为 `ask`。
4. `permission` 和 `pattern` 都支持通配符。

### 10.2 `ask` 如何暂停执行

如果规则结果是 `ask`：

```text
创建 Permission Request
  ↓
存入 pending Map
  ↓
发布 Asked 事件给客户端
  ↓
Deferred.await() 暂停当前工具
  ↓
用户选择 once / always / reject
  ↓
Deferred 成功或失败，工具继续或终止
```

- `once`：仅批准当前请求。
- `always`：把允许规则放入本次实例的 approved 规则中，并自动放行匹配的待处理请求。
- `reject`：拒绝当前请求，同时拒绝同 Session 中其他待处理审批。

### 10.3 默认权限并不等于严格沙箱

当前源码中，基础默认规则首先设置了 `"*": "allow"`，然后对以下高风险项覆盖：

- 重复工具死循环：`ask`
- 项目外目录：通常 `ask`
- `.env` 和 `.env.*`：读取时 `ask`
- `question`、Plan 切换工具：按 Agent 单独控制

`build` Agent 基于这些默认规则工作，因此整体偏可执行。`plan` Agent 追加 `edit: deny`，但用户配置最后合并，理论上仍可以覆盖内置规则。

所以：

> OpenCode 的权限层是可配置的策略门，不是不可绕过的进程隔离。插件缺陷、未经过权限层的代码路径或配置错误，不能依靠它提供容器级安全保证。

对不可信仓库或高敏感环境，应额外使用容器、虚拟机、低权限系统账户、网络隔离和只读凭证。

### 10.4 Doom Loop 防护

Processor 会检查最近的工具调用。如果连续 3 次出现：

```text
相同工具名 + 完全相同输入
```

就触发 `doom_loop` 权限请求，询问用户是否继续。这可以阻止模型在失败状态下无限重复同一个动作并持续消耗 Token。

## 11. Agent 与子 Agent

Agent 在 OpenCode 中不是一个独立模型，而是一组运行配置：

```text
Agent
  = prompt
  + model/variant
  + tools
  + permissions
  + max steps
  + mode(primary/subagent)
```

当前内置 Agent 包括：

- `build`：主要执行 Agent。
- `plan`：主要规划 Agent，默认禁止编辑普通项目文件。
- `general`：通用多步骤子 Agent。
- `explore`：偏只读搜索和代码探索。
- 其他用于标题、摘要、压缩等内部任务的 Agent。

### 11.1 Task Tool

主 Agent 调用 `task` 工具时，会：

1. 根据名字解析子 Agent。
2. 创建与父 Session 关联的子会话。
3. 使用子 Agent 的 Prompt、模型、工具和权限运行新的 Agent Loop。
4. 等待子任务结束。
5. 把子任务结果作为 Tool Result 返回父 Agent。

这能隔离子任务上下文：例如 Explore Agent 可以读取大量搜索结果，但父 Agent 最终只接收总结，避免所有中间信息直接挤进主上下文。

当前内置 Task Tool 主要是同步等待式调用。并行与后台编排能力取决于具体版本、客户端或插件实现，不能简单把“存在子 Agent”理解为所有子任务都会自动并发。

## 12. 长上下文管理

Coding Agent 很容易积累巨量上下文：文件内容、测试日志、Tool Schema、模型输出和多轮历史都会占用 Token。

OpenCode 使用两级策略。

### 12.1 工具输出裁剪 Prune

源码当前常量：

```text
PRUNE_PROTECT = 40,000 tokens
PRUNE_MINIMUM = 20,000 tokens
```

它从新到旧扫描已完成工具输出：

- 保护最近两轮。
- 保留最近约 40K Token 的工具结果。
- 超出保护范围的旧工具输出累计超过 20K 后，标记为已压缩。
- `skill` 等特定工具输出受到额外保护。

工具调用本身和对话结构仍保留，只减少历史大输出。

### 12.2 会话摘要 Compaction

当模型 Token 使用接近可用上下文上限时：

1. 创建一个 synthetic compaction 用户消息。
2. 选择 Compaction Agent 和模型。
3. 找出需要压缩的历史 head。
4. 保留最近 tail；默认关注最近 2 个 Turn。
5. 最近内容预算默认为可用上下文的 25%，并限制在 2K～8K Token。
6. 压缩请求去除媒体附件。
7. 工具输出在压缩请求中最多保留约 2,000 字符。
8. 模型生成结构化交接摘要。
9. 后续循环使用“历史摘要 + 最近原始消息”继续。

```text
压缩前：Turn1 + Turn2 + Turn3 + Turn4 + Turn5

压缩后：[Turn1~Turn3 摘要] + Turn4 + Turn5
```

当前实现会识别已经完成的旧摘要，避免重复把摘要对应的原始消息再次完整压缩，并把上一次摘要作为下一次压缩的输入。

### 12.3 Compaction 的信息损失

Compaction 是有损压缩。摘要可能遗漏：

- 精确错误文本
- 尚未验证的假设
- 用户细粒度限制
- 文件中的具体行级细节
- 工具输出中看似次要但后来关键的信息

因此长任务应把稳定事实写入计划、Todo、项目文档或代码，而不是只依赖会话记忆。

## 13. 文件快照、Diff 与撤销

OpenCode 没有只依赖当前项目 Git 状态来记录 Agent 修改。`snapshot/` 会创建一个独立 Git 目录：

```text
git-dir  → OpenCode 全局数据目录下的 snapshot
work-tree → 当前项目目录
```

### 13.1 每一步如何记录变化

```text
step-start
  ↓ Snapshot.track()，记录修改前树状态
模型执行 edit/write/shell
  ↓
step-finish
  ↓ Snapshot.track()，记录修改后状态
  ↓ Snapshot.patch(beforeHash)
  ↓ 生成本步骤变化文件列表和 Diff
```

### 13.2 与用户仓库的关系

快照使用独立 Git 索引和对象目录，不会等同于替用户执行 `git add` 或 `git commit`。为了降低大型仓库成本，它还会复用原仓库对象数据库和索引哈希。

当前实现还会：

- 尊重项目 `.gitignore`。
- 不把忽略文件的删除展示为普通 patch。
- 避免把大于约 2 MB 的新未跟踪文件加入快照。
- 支持按快照恢复已有文件。
- 对快照中不存在的新文件执行撤销时删除该文件。
- 用 Semaphore 串行化同一快照仓库操作，避免 Git 索引竞争。

这就是 `/undo`、会话 Diff 和逐步变化展示的技术基础。

## 14. MCP 的内部接入方式

OpenCode 使用官方 MCP SDK Client，并支持：

- 本地进程：`StdioClientTransport`
- 远程服务：`StreamableHTTPClientTransport`
- 兼容旧服务：`SSEClientTransport`

连接后，OpenCode 会获取 MCP Server 的：

- Tools
- Prompts
- Resources
- Resource Templates
- Server Instructions

MCP Tool 会被转换成 AI SDK Tool，再进入与内置工具相同的执行链：

```text
MCP Tool Definition
  ↓ Schema 转换
模型 Tool Call
  ↓ OpenCode Permission.ask()
MCP Client execute()
  ↓
文本 / 图片 / Resource
  ↓ 截断、大小检查、附件标准化
Tool Result 返回模型
```

源码会限制可直接附加的二进制 MIME，并拒绝过大的 MCP 二进制资源，避免一次资源返回无限膨胀上下文。

MCP Server 自己提供的 instructions 也会进入 System Context，但只有至少一个相关工具在权限上可见时才注入。

## 15. LSP 的作用

LSP 给 Agent 提供语义级代码信息：

- Definition
- References
- Symbols
- Diagnostics
- Hover/类型信息

与 `grep` 的区别：

```text
grep：字符串匹配，快，但不知道语义
LSP：理解语言符号、类型和引用关系
```

OpenCode 既可以通过 LSP Tool 暴露给模型，也会在内部使用 LSP。例如用户指定文件中的某一行时，源码会尝试通过 `documentSymbol` 扩展到完整符号范围，避免只读取一个缺少上下文的孤立行。

LSP 不会替代文件搜索：大型项目通常仍是先用 glob/grep 缩小范围，再用 LSP 确认语义关系。

## 16. Effect 为什么重要

OpenCode 大量使用 Effect，而不是只用 Promise + try/catch。

Effect 在这里主要解决：

### 16.1 依赖注入

Session、Provider、Permission、MCP、LSP、Snapshot 等都定义为 Service，再通过 Layer 组合。核心逻辑不需要直接创建全局单例，测试时也更容易替换实现。

### 16.2 结构化并发

例如构建 Prompt 时，环境、Skills、Instructions、MCP 和历史转换可以并发执行；退出 Scope 时，相关子任务和资源有统一的生命周期。

### 16.3 Stream

模型输出被表示成 `Stream<LLMEvent>`，Processor 用 `tap` 消费事件，并能在触发上下文压缩时 `takeUntil` 主动停止。

### 16.4 中断和 Finalizer

模型请求、Shell 子进程、MCP 连接和 HTTP Listener 都能注册清理逻辑。即使用户取消或异常发生，也会进入确保执行的 cleanup。

### 16.5 类型化错误与重试

网络错误、权限拒绝、上下文溢出和用户中断可以走不同分支，不必都变成无法区分的 `catch (e)`。

代价是源码学习曲线明显高于普通 TypeScript 项目：Generator 风格的 `yield*`、Layer、Context、Scope、Stream 和 Effect 错误通道需要一起理解。

## 17. 客户端/服务端通信

OpenCode 服务端使用 Effect HTTP 和 Node HTTP，API 可以生成 OpenAPI 描述。

服务端主要承担：

- 创建和管理 Session
- 接收 Prompt
- 发布文本、工具、权限和状态事件
- 管理项目实例和目录上下文
- 查询 Agent、MCP、LSP 和 Formatter 状态
- 让 TUI、Desktop、Web、IDE 和 SDK 复用核心能力

`opencode serve` 可以把它作为 headless HTTP 服务运行。默认应监听回环地址；如果监听 `0.0.0.0`，应设置 `OPENCODE_SERVER_PASSWORD` 并限制网络访问。

客户端/服务端设计的优势是 UI 与 Agent Runtime 解耦；风险是这个服务端可能拥有文件和 Shell 权限，不能把未鉴权端口暴露到不可信网络。

## 18. 安全信任边界

完整信任链不是只有 OpenCode：

```text
用户
 ↓
OpenCode Core
 ├─ 模型服务商
 ├─ 插件
 ├─ MCP Server
 ├─ LSP Server
 ├─ Shell / 系统命令
 └─ 项目代码与依赖脚本
```

### 18.1 Prompt Injection

模型读取的仓库文件、网页和 MCP 数据可能包含恶意指令。模型无法天然区分“业务数据”和“应该遵守的命令”。权限层可以拦截部分副作用，但不能保证模型判断永远正确。

### 18.2 远程模型数据

OpenCode 开源不等于代码不离开本机。使用远程模型时，被选入上下文的项目内容会发送给对应服务商。保存、训练、地域和合规政策取决于服务商和账户协议。

### 18.3 插件与 MCP

- 插件可以修改 Prompt、工具定义和执行结果，通常属于高信任代码。
- 本地 MCP 可以启动子进程。
- 远程 MCP 可以返回不可信数据并执行外部操作。
- MCP 的权限名称可以动态出现，配置时应采用默认询问或白名单策略。

### 18.4 Shell

只要 Agent 获得 Shell 权限，理论能力就接近当前系统用户。应用层工具权限无法限制一个已经获准执行的任意脚本在内部做什么。

高风险项目应使用 OS 级隔离，而不只依靠 OpenCode 配置。

## 19. OpenCode 技术设计的优点

1. **Provider-neutral**：模型适配和 Agent Runtime 分离。
2. **统一事件流**：AI SDK 和原生 Runtime 都转换成 `LLMEvent`。
3. **工具是强类型接口**：描述、JSON Schema、权限和执行上下文统一。
4. **上下文是分层构建的**：模型模板、环境、规则、Skills、MCP 和历史职责清晰。
5. **长会话不是简单截断**：先裁旧工具输出，再摘要 head，保留 recent tail。
6. **每步有文件快照**：Diff 和回滚不是依靠模型记忆。
7. **客户端与 Runtime 解耦**：便于扩展 Desktop、Web、IDE 和 SDK。
8. **Effect 提供结构化生命周期**：取消、重试、资源清理和并发组织比较系统。

## 20. 局限与工程权衡

### 20.1 多模型兼容不是免费抽象

不同模型的 Tool Calling 行为差异很大。OpenCode 需要维护模型专用 Prompt、Schema 转换、消息转换和工具组合，适配层会持续增长。

### 20.2 权限是软隔离

规则引擎灵活，但无法替代容器和操作系统权限。最后匹配规则获胜也意味着复杂配置可能出现意外覆盖。

### 20.3 Compaction 有损

摘要可以让任务继续，但可能改变细节。非常长的任务仍需要外部化计划和状态。

### 20.4 工具数量与能力存在矛盾

工具越多，能力越强，但 Prompt 越长，模型误选工具和生成错误参数的概率也可能提高。

### 20.5 Effect 增加理解成本

Effect 带来强大的并发和资源模型，但会让贡献者调试调用链、错误通道和 Layer 依赖时付出更高学习成本。

### 20.6 Agent 的上限仍受模型限制

Harness 可以改善反馈和可靠性，却不能消除模型幻觉。模型仍可能：

- 误判根因
- 读取错误文件
- 写出能通过测试但业务语义错误的代码
- 误解工具输出
- 在 Compaction 后忘记细节

## 21. 最值得关注的源码阅读顺序

如果要继续研究，建议按以下顺序阅读：

1. [`session/prompt.ts`](https://github.com/anomalyco/opencode/blob/dev/packages/opencode/src/session/prompt.ts)  
   先看 `runLoop()`，掌握外层控制流。

2. [`session/processor.ts`](https://github.com/anomalyco/opencode/blob/dev/packages/opencode/src/session/processor.ts)  
   看模型事件如何变成 Text/Tool/Patch 状态。

3. [`session/llm.ts`](https://github.com/anomalyco/opencode/blob/dev/packages/opencode/src/session/llm.ts)  
   看 AI SDK、Native Runtime 和统一事件适配。

4. [`session/tools.ts`](https://github.com/anomalyco/opencode/blob/dev/packages/opencode/src/session/tools.ts) 与 [`tool/registry.ts`](https://github.com/anomalyco/opencode/blob/dev/packages/opencode/src/tool/registry.ts)  
   看工具如何注册、过滤、包装和执行。

5. [`permission/index.ts`](https://github.com/anomalyco/opencode/blob/dev/packages/opencode/src/permission/index.ts)  
   看 `evaluate()`、`ask()`、`reply()` 和规则优先级。

6. [`session/system.ts`](https://github.com/anomalyco/opencode/blob/dev/packages/opencode/src/session/system.ts) 与 [`session/instruction.ts`](https://github.com/anomalyco/opencode/blob/dev/packages/opencode/src/session/instruction.ts)  
   看 Prompt 和项目规则如何进入上下文。

7. [`session/compaction.ts`](https://github.com/anomalyco/opencode/blob/dev/packages/opencode/src/session/compaction.ts)  
   看 Prune、Tail 保留和摘要压缩。

8. [`snapshot/index.ts`](https://github.com/anomalyco/opencode/blob/dev/packages/opencode/src/snapshot/index.ts)  
   看独立 Git 快照、Diff 和 Revert。

9. [`mcp/index.ts`](https://github.com/anomalyco/opencode/blob/dev/packages/opencode/src/mcp/index.ts)  
   看 Stdio/HTTP/SSE MCP Transport 和工具转换。

10. [`agent/agent.ts`](https://github.com/anomalyco/opencode/blob/dev/packages/opencode/src/agent/agent.ts)  
    看 build、plan、explore 和内部 Agent 的权限差异。

## 22. 简要安装与运行附录

Windows 官方推荐 WSL：

```powershell
wsl --install
```

在 WSL 中：

```bash
curl -fsSL https://opencode.ai/install | bash
cd /mnt/d/code/study-note
opencode
```

启动后：

```text
/connect   连接模型
/models    选择模型
/init      生成或完善 AGENTS.md
```

Windows 原生也可以使用：

```powershell
scoop install opencode
```

## 23. 参考资料

- [OpenCode GitHub 仓库](https://github.com/anomalyco/opencode)
- [OpenCode 官方文档](https://opencode.ai/docs/)
- [Server 与 OpenAPI 架构](https://opencode.ai/docs/server/)
- [Agent 与权限配置](https://opencode.ai/docs/agents)
- [模型服务商配置](https://opencode.ai/docs/providers)
- [项目规则与 AGENTS.md](https://opencode.ai/docs/rules)
- [MCP 配置](https://opencode.ai/docs/mcp-servers)
- [OpenCode 开发指南](https://github.com/anomalyco/opencode/blob/dev/CONTRIBUTING.md)
- [MIT License](https://github.com/anomalyco/opencode/blob/dev/LICENSE)

## 24. 最终理解

从源码角度看，OpenCode 的本质可以浓缩为四层：

```text
第一层：Prompt/Context —— 告诉模型它是谁、项目是什么、能做什么
第二层：LLM Adapter   —— 屏蔽不同模型服务商协议差异
第三层：Agent Loop    —— 持续执行“观察—行动—反馈”
第四层：Engineering   —— 权限、会话、压缩、快照、回滚、客户端通信
```

真正让它从“聊天机器人”变成“编程 Agent”的，不是某一句神奇 Prompt，而是模型外部这整套工程闭环。
