# 1 上下文管理（状态管理）

Function Calling: 需开发者自行维护多轮状态

MCP：内置会话上下文（如conversation_id和history）

## 1.1 场景说明

患者与AI医疗助手交互：

第一轮：患者问：＂手部出现红疹且瘙痒，可能是什么原因？＂

第二轮：患者追问：＂上周医生开的氯雷他定片需要调整剂量吗？＂

### 传统Function Calling方案（需开发者维护状态）

开发者需手动实现多轮状态管理

```python
# 伪代码：开发者需维护全局会话状态  
session_db = {}  # 用数据库/缓存存储会话历史  

def handle_query(user_id, query):  
    # 1. 从数据库加载该用户的历史对话  
    history = session_db.get(user_id, [])  

    # 2. 拼接完整上下文（含历史）  
    full_context = "\n".join(history) + f"\n用户: {query}"  

    # 3. 判断是否需调用工具（如病历查询）  
    if "调整剂量" in query:  
        # 手动从历史中提取药品名（易出错！）  
        medicine = extract_medicine(history)  
        # 调用药品查询函数  
        result = query_medicine_dosage(medicine)  
        response = f"建议剂量：{result}"  
    else:  
        # 调用诊断模型生成回复  
        response = generate_diagnosis(full_context)  

    # 4. 保存当前对话到数据库  
    session_db[user_id].append(f"用户: {query}\n助手: {response}")  
    return response  
```
### 问题分析

状态维护复杂：开发者需手动存储/加载会话历史，并解析历史中的关键信息（如药品名）。

信息易丢失：历史对话以纯文本拼接，模型可能忽略早期关键信息（如红疹症状）。

工具调用割裂：药品查询函数无法自动关联历史上下文，需开发者硬编码提取参数.

### MCP协议方案（内置会话上下文）

MCP通过协议层自动管理上下文，开发者只需关注工具逻辑：

```json
// MCP请求报文（第二轮追问时）  
{  
  "jsonrpc": "2.0",  
  "method": "medical_query",  
  "params": {  
    "context": {  
      "conversation_id": "med_conv_123", // 会话唯一ID  
      "history": [  
        {"role": "user", "content": "手部红疹瘙痒"},  
        {"role": "assistant", "content": "可能是接触性皮炎，建议..."},  
        {"role": "user", "content": "上周开的氯雷他定片要调整剂量吗？"}  
      ]  
    }  
  }  
}  
```

### MCP服务端的优势：

自动状态关联：

通过 conversation_id 自动关联历史对话，无需开发者手动管理存储。

模型直接看到完整上下文（含首轮诊断和药品名），无需额外拼接。


## 1.2 上下文存储在哪？

Spring AI Alibaba：通过ChatMemory接口抽象存储实现，支持内存 / Redis/MySQL，配置驱动存储逻辑；

FastAPI-MCP：中间层内置存储模块，默认内存，或分布式仓库（PostgreSQL + 向量库）

# 2 注册方式

## 2.1 Function Calling：静态函数 Schema 嵌入 Prompt——“随请求传递，固定且耦合

Function Calling 的 “注册” 本质是 将工具的元信息（名称、参数、描述等）以静态 Schema 形式嵌入模型输入（Prompt 或请求字段），

让模型在单次对话中 “临时知晓” 可用工具。这种方式与具体模型的交互强耦合，工具信息随请求传递，无法脱离单次对话独立存在。

### 核心机制

```text
厂商（如 OpenAI、Anthropic）要求开发者在调用模型时，

显式提供工具的 Schema 定义（通常是 JSON 格式的函数描述），

模型通过解析这些 Schema 理解工具的调用规范。

由于 Schema 与单次请求绑定，因此称为 “静态注册”—— 工具信息仅在当前对话中有效，

且无法动态更新（如需修改工具参数，需重新嵌入 Schema）
```

### OpenAI Function Calling：functions字段静态嵌入

示例：嵌入 “天气查询工具” Schema

```python
# 调用GPT-4时，需在请求中嵌入工具Schema
response = openai.ChatCompletion.create(
  model="gpt-4",
  messages=[{"role": "user", "content": "北京明天天气？"}],
  # 静态嵌入工具Schema：名称、参数、描述
  functions=[{  
    "name": "get_weather",
    "description": "获取指定地点的天气信息",
    "parameters": {
      "type": "object",
      "properties": {
        "location": {"type": "string", "description": "城市名称，如北京"},
        "date": {"type": "string", "description": "日期，如2025-07-29"}
      },
      "required": ["location"]  # 必填参数
    }
  }],
  function_call="auto"  # 让模型自动决定是否调用工具
)
```

Schema 内容：必须包含name（工具名）、description（功能描述，帮助模型判断何时调用）、parameters（参数结构，含类型和约束）；

传递时机：每次对话开始时嵌入，或在需要调用工具的轮次动态嵌入（但需确保模型能理解上下文）。

### Anthropic Claude：Schema 嵌入 System Prompt

Claude 的tool_use机制要求将工具 Schema 嵌入 System Prompt（系统提示词），通过自然语言描述工具功能和参数，模型从 Prompt 中提取工具信息。

示例：在 System Prompt 中描述工具

```python
system_prompt = """
你可以调用以下工具回答问题：
<tools>
[
  {
    "name": "get_weather",
    "description": "获取指定地点的天气信息",
    "input_schema": {
      "type": "object",
      "properties": {
        "location": {"type": "string", "description": "城市名称"},
        "date": {"type": "string", "description": "日期"}
      }
    }
  }
]
</tools>
调用工具时需使用<tool_use>标签包裹。
"""

response = anthropic.Client().messages.create(
  model="claude-3-opus",
  system=system_prompt,  # Schema嵌入System Prompt
  messages=[{"role": "user", "content": "北京明天天气？"}]
)
```

### 关键特点：静态、耦合、简单但不灵活
```text
静态性：工具 Schema 与单次对话绑定，无法跨对话复用（新对话需重新嵌入）；
强耦合：Schema 需嵌入模型输入（Prompt 或请求字段），与模型调用逻辑强耦合；
无版本控制：工具更新（如新增参数）需手动修改 Schema 并重新嵌入，易导致前后不一致；
适合场景：工具数量少（≤5 个）、调用逻辑简单（如单次查询）、无需动态更新的场景。
```
### 局限性
```text
Prompt 长度膨胀：工具数量超过 10 个时，Schema 会占用大量 Token（如每个工具 Schema 约 200Token，10 个工具即 2000Token），挤压对话上下文空间；
管理成本高：多团队协作时，工具 Schema 需手动同步（如 A 团队更新工具参数，B 团队未同步导致调用失败）；
动态性缺失：无法实时新增 / 下线工具（如需新增 “股票查询” 工具，需重启所有对话并重新嵌入 Schema）。
```

## 2.2 MCP：动态服务注册（YAML/JSON Manifest）——“独立配置，热插拔且解耦

MCP 的 “注册” 是 通过独立的 Manifest 配置文件（YAML/JSON）声明工具服务元信息，

由 MCP 中间层 / 服务端统一管理，工具信息与模型调用完全解耦，支持动态更新、版本控制和跨系统发现。

这种方式类似 “服务注册中心”，工具作为独立服务注册到 MCP 生态，模型通过中间层查询可用工具，

无需手动嵌入 Schema。

### Manifest 文件结构（标准化定义）

```text
MCP 要求工具提供者 编写标准化的 Manifest 文件（YAML 或 JSON），

声明工具的名称、描述、API 端点、参数 Schema、认证方式等元信息，

然后将 Manifest 注册到 MCP 服务端（如配置中心、文件系统或数据库）。

MCP 中间层加载 Manifest 后，自动向模型暴露可用工具，模型通过中间层调用工具，无需感知工具细节。
```

MCP Manifest 遵循 开放元数据规范（参考 CNCF Service Mesh 的 Service Manifest），

核心字段包括工具基本信息、接口规范、访问控制等。

示例：天气查询工具的 YAML Manifest

```yaml
# weather-tool-manifest.yaml
apiVersion: mcp.protocol/v1  # MCP协议版本
kind: ToolService  # 资源类型（工具服务）
metadata:
  name: get_weather  # 工具唯一名称（全局不可重复）
  version: v2.1  # 工具版本（支持多版本共存）
  description: "获取指定地点的实时天气和预报信息"  # 模型用于判断何时调用
spec:
  endpoint: "https://api.weather.com/weather"  # 工具API端点（HTTP/GRPC）
  protocol: "http"  # 通信协议（支持http/grpc/jsonrpc）
  inputSchema:  # 参数Schema（JSON Schema格式，模型自动解析）
    type: object
    properties:
      location: 
        type: string
        description: "城市名称，如北京、上海"
        required: true  # 必填参数
      date: 
        type: string
        format: date
        description: "查询日期，如2025-07-29（默认当天）"
  authentication:  # 认证方式（MCP中间层自动处理）
    type: apiKey
    in: header
    name: X-API-Key
  timeout: 5s  # 调用超时时间
```

### 注册流程：工具→MCP 服务端→模型

*工具提供者注册 Manifest*：通过 MCP CLI 或 UI 将weather-tool-manifest.yaml上传至 MCP 服务端（如 Nacos 配置中心、etcd 集群）；

*MCP 中间层加载 Manifest*：中间层定期从服务端拉取 Manifest（或监听变更事件），缓存工具元信息；

*模型查询可用工具*：当客户端发送 MCP 请求时，中间层自动向模型返回当前可用工具列表（基于 Manifest），模型无需依赖 Prompt 中的 Schema；

*动态更新*：工具更新时（如新增temperature_unit参数），工具提供者仅需修改 Manifest 并重新注册，MCP 中间层实时生效，无需重启模型或客户端。

### 关键特点：动态、解耦、标准化且可扩展

*动态性*：支持工具热插拔（新增 / 下线工具无需重启系统），Manifest 更新后中间层实时感知；

*解耦*：工具元信息（Manifest）与模型调用完全分离，模型通过中间层间接调用工具，无需嵌入 Schema；

*标准化*：Manifest 字段遵循 MCP 开放规范，任何厂商 / 工具均可按同一格式编写，支持跨系统复用；

*版本控制*：Manifest 包含version字段，支持多版本工具共存（如get_weather:v1和get_weather:v2），模型可指定版本调用；

*适合场景*：工具数量多（≥10 个）、跨团队协作、需动态更新工具的复杂系统（如企业级 AI 助手、多模型协同平台）。

### 优势：支撑大规模工具生态

*集中化管理*：所有工具 Manifest 存储在统一注册中心，支持权限控制（如仅允许特定团队访问财务工具）、审计日志（记录工具调用频次）；

*低 Token 消耗*：模型无需加载完整 Schema，中间层仅向模型传递工具名称和简要描述（如 “get_weather：获取天气信息”），节省 Token；

*跨模型复用*：同一 Manifest 可被 GPT、Claude、Llama 等所有模型共享，无需为不同模型编写不同 Schema；

*自动化校验*：MCP 中间层基于 Manifest 自动校验工具调用参数（如必填项缺失时返回错误），减少模型无效调用。

# 3 协议层区别

## 3.1 Function Calling

```text
是 LLM 厂商为自家模型设计的工具调用接口，

其格式、字段定义、交互逻辑完全由厂商自主决定，仅服务于该厂商的模型生态。

核心特点是 “绑定厂商，格式专有”，典型代表如 OpenAI 的function_call、

Anthropic 的tool_use、

Google Gemini 的function_calling等。

厂商根据自身模型架构和功能需求设计 Function Calling 接口，导致 字段名、参数结构、响应格式差异极大，

甚至同一厂商不同模型的格式也可能不兼容。
```
### 具体代码示例

OpenAI（GPT-4）：需在functions数组声明工具 schema，通过function_call指定调用：

```json
{
  "model": "gpt-4",
  "messages": [{"role": "user", "content": "查询北京天气"}],
  "functions": [  // 工具定义（厂商专有格式）
    {
      "name": "get_weather",
      "parameters": {
        "type": "object",
        "properties": {"location": {"type": "string"}}
      }
    }
  ],
  "function_call": {"name": "get_weather", "parameters": {"location": "北京"}}  // 调用字段
}
```
Anthropic（Claude 3）：工具定义用tools数组，调用字段为tool_use：

```json
{
  "model": "claude-3-opus",
  "messages": [{"role": "user", "content": "查询北京天气"}],
  "tools": [  // 工具定义（字段名与OpenAI不同）
    {
      "name": "get_weather",
      "description": "获取指定地点天气",
      "input_schema": {  // 参数定义字段名不同（OpenAI用parameters，Claude用input_schema）
        "type": "object",
        "properties": {"location": {"type": "string"}}
      }
    }
  ],
  "tool_use": {"name": "get_weather", "parameters": {"location": "北京"}}  // 调用字段名不同
}
```

Function Calling 作为厂商专有接口，其生态完全依附于厂商的模型和 API，呈现 “封闭性” 和 “厂商锁定” 特点

## 3.2 MCP：开放标准（JSON-RPC 2.0等）

```text
MCP（Model Context Protocol）是 独立于厂商的开放通信标准，

基于成熟的通用协议（如 JSON-RPC 2.0、HTTP/2）定义模型与外部系统（工具、客户端、其他模型）的交互格式，

目标是 “跨厂商、跨模型、跨系统的标准化通信.

MCP 以 开放标准为底层支撑（如 JSON-RPC 2.0 用于远程过程调用，HTTP/2 用于传输层），

通过标准化字段定义请求 / 响应结构，确保不同厂商、不同模型、不同工具遵循同一套 “语言” 通信。
```

### 跨厂商模型适配：中间层 + 开放转换规则

```text
MCP 通过 开放中间层（如 Java的Spring AI、Python的FastAPI-MCP） 实现与厂商模型的适配，

中间层将 MCP 标准化请求转换为厂商 Function Calling 格式，

再将厂商响应转换回 MCP 格式，开发者无需关心底层模型差异。
```

#### 例如 MCP 中间层适配 OpenAI 和 Claude

```text
客户端发送 MCP 请求（含tool_call字段）；

中间层自动将tool_call转换为 OpenAI 的function_call或 Claude 的tool_use；

模型响应后，中间层将厂商专有字段（如function_call/tool_use）统一转换为 MCP 的tool_call格式返回给客户端。

```

MCP 的定位类似于互联网中的 HTTP 协议：

HTTP 定义了客户端与服务器的通用通信格式（请求行、 headers、body），

无论服务器是 Apache、Nginx 还是 IIS，均遵循同一标准；

MCP 则定义了模型与外部系统的通用通信格式，无论模型是哪家厂商的，均能用同一套 “语言” 交互。

```text
Function Calling 是厂商为自家模型打造的 “私有功能接口”，解决单一模型的工具调用问题；

MCP 是基于开放协议的 “通用通信标准”，解决跨厂商、跨模型、跨系统的标准化交互问题。

前者是 “功能扩展”，后者是 “基础设施”—— 二者定位不同，

但开放标准（MCP）最终会成为多模型协同的主流选择，正如 USB PD 取代各种私有快充接口成为行业标准。
```

# 4 执行模式

## 4.1 Function Calling：同步调用 ——“模型阻塞等待，结果即时生成”

Function Calling 的执行模式是 “模型主导的同步阻塞流程”：模型决定调用工具后，会暂停生成响应，等待工具返回结果， 再基于结果继续生成最终回答。

整个过程中，模型与工具的交互是 “一问一答” 的串行关系，工具未返回时，模型处于阻塞状态。

### 核心流程：模型与工具 “串行交互”

以 “用户询问‘北京明天天气 + 推荐景点’” 为例，Function Calling 需先同步调用天气工具，再基于天气结果调用景点推荐工具，流程如下：

```text
用户提问 → 模型解析问题 → 调用天气工具（同步阻塞）
→ 工具返回天气结果 → 模型基于天气调用景点工具（同步阻塞）
→ 工具返回景点结果 → 模型生成最终回答
```

### Step 1：用户提问触发工具调用

用户输入：“北京明天天气怎么样？适合去哪些景点？”

模型判断需先获取天气（如雨天推荐室内景点，晴天推荐户外景点），生成 Function Calling 指令：

```json
{"function_call": {"name": "get_weather", "parameters": {"location": "北京", "date": "tomorrow"}}}
```

### Step 2：同步调用工具，模型阻塞等待

客户端解析 function_call 指令，同步发送 HTTP 请求调用天气工具：

```http request
POST https://api.weather.com/get_weather
{"location": "北京", "date": "tomorrow"}
```

此时模型不再生成任何响应，客户端也不会向用户返回内容，整个流程阻塞，直到工具返回结果（假设耗时 2 秒）

### Step 3：工具返回结果，模型继续处理

```json
{"temperature": "25-32℃", "condition": "晴", "suggestion": "适合户外活动"}
```

客户端将结果以 role="function" 格式返回给模型，模型基于天气结果（“晴，适合户外活动”），再次生成 Function Calling 指令调用景点工具：

```json
{"function_call": {"name": "get_attractions", "parameters": {"location": "北京", "type": "outdoor"}}}
```

### Step 4：再次同步调用工具，最终生成回答

景点工具返回户外景点列表（耗时 3 秒），模型整合天气和景点结果，生成最终回答：

“北京明天晴，气温25-32℃，适合户外活动，推荐景点：颐和园、八达岭长城。”

### 技术特点：阻塞式、即时依赖、结果直接消费

**同步阻塞**：模型在工具调用期间暂停生成，必须等待工具返回后才能继续，总耗时 = 工具 1 耗时 + 工具 2 耗时 + 模型处理时间（上例中为 2+3=5 秒阻塞）；

**即时依赖**：工具结果需即时传递给模型，模型必须基于结果生成下一步动作（如继续调用工具或回答用户），无法脱离结果独立推进；

**结果直接消费**：工具结果仅在当前调用链中使用，若后续对话需要，需重新调用工具（如用户追问 “后天天气”，需重新发起天气工具调用）；

**实现简单**：无需复杂的任务调度或状态管理，客户端按 “调用→等待→处理” 的线性流程执行，适合厂商 API 快速集成。


### 局限性：长耗时任务体验差，不支持并行调用

**用户等待时间长**：若工具耗时较长（如数据库查询耗时 10 秒），用户需等待 10 秒才能收到任何响应，体验差；

**无法并行调用工具**：多个独立工具需串行调用（如同时查询天气和股票），总耗时 = 各工具耗时之和，效率低；

**上下文断裂风险**：若工具调用失败（如超时），模型无法继续生成，需用户重新提问，容错性低。

## 4.2 MCP：异步调用 ——“模型非阻塞推进，结果结构化注入上下文”

同样以 “用户询问‘北京明天天气 + 推荐景点’” 为例，MCP 支持异步调用天气和景点工具（假设景点推荐无需依赖天气），流程如下：

```text
用户提问 → 模型通过MCP中间层异步发起天气+景点工具调用（非阻塞） → 模型生成即时响应（“正在查询天气和景点，结果将很快更新”） → 工具1（天气）完成 → 中间层将结果注入上下文 → 工具2（景点）完成 → 中间层将结果注入上下文 → 后续轮次模型引用上下文结果生成最终回答
```

### Step 1：用户提问，模型生成异步调用指令

用户输入：“北京明天天气怎么样？适合去哪些景点？”

模型基于 MCP 协议生成工具调用指令（包含任务 ID，用于后续结果关联），通过中间层异步发送：

```json
// MCP工具调用请求（异步）
{
  "jsonrpc": "2.0",
  "method": "tool.call",
  "params": {
    "tasks": [  // 支持同时发起多个异步任务
      {
        "task_id": "task_weather_001",  // 任务唯一标识
        "tool_name": "get_weather",
        "parameters": {"location": "北京", "date": "tomorrow"}
      },
      {
        "task_id": "task_attr_001",
        "tool_name": "get_attractions",
        "parameters": {"location": "北京"}  // 无需等待天气结果，直接调用
      }
    ]
  },
  "id": "req_001"
}
```

### Step 2：中间层管理异步任务，模型即时响应

MCP 中间层收到调用指令后，将任务加入异步队列（通过消息队列如 RabbitMQ/Kafka 调度），

立即向模型返回 “任务已受理” 确认，无需等待工具执行：

```json
// MCP中间层确认响应
{
  "jsonrpc": "2.0",
  "result": {
    "task_ids": ["task_weather_001", "task_attr_001"],
    "status": "accepted"  // 任务已接受，正在执行
  },
  "id": "req_001"
}
```

模型基于此确认，立即生成用户可见的即时响应：

“已为您查询北京明天天气和推荐景点，结果将在1-3秒内更新。”

用户无需长时间等待，体验更流畅。

### Step 3：工具完成，结果结构化注入上下文

天气工具完成（2 秒后），中间层将结果以 role="tool" 角色、task_id 标识，注入当前会话上下文

```json
// 上下文自动追加天气结果（结构化存储）
{
  "role": "tool",
  "task_id": "task_weather_001",  // 关联任务ID
  "content": {
    "status": "success",
    "result": {"temperature": "25-32℃", "condition": "晴"}
  }
}
```

景点工具完成（3 秒后），中间层同样将结果注入上下文：

```json
{
  "role": "tool",
  "task_id": "task_attr_001",
  "content": {
    "status": "success",
    "result": ["颐和园", "八达岭长城", "天坛"]
  }
}
```

### Step 4：模型后续轮次引用上下文结果，生成最终回答

上下文更新后，中间层自动触发模型 “继续处理”（或用户下次提问时），模型通过 task_id 引用上下文结果：

```json
// 模型引用task_id获取结果（MCP中间层自动解析）
"基于天气结果（task_weather_001：晴）和景点列表（task_attr_001），推荐户外景点：颐和园、八达岭长城。"
```
最终生成完整回答返回用户。

### 技术特点：非阻塞、结果持久化、延迟引用、并行调度

**异步非阻塞**：模型发起工具调用后立即推进流程（生成即时响应或并行调用其他工具），总耗时 = 最长工具耗时（上例中为 3 秒，而非 2+3=5 秒）；

**结果结构化注入上下文**：工具结果以标准化格式（含 task_id、状态、结构化数据）存入上下文，通过 conversation_id 持久化，后续对话可直接引用（如用户追问 “景点开放时间”，模型通过 task_attr_001 引用已获取的景点列表，无需重新调用）；

