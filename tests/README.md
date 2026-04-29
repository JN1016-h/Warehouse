# 压测与接口自测（Windows）

与当前工程一致：`context-path` 为 **`/springboot38hdw40x`**。下列接口在代码中标注了 `@IgnoreAuth`，**无需 Token**，适合作为压测与冒烟请求。

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/springboot38hdw40x/shangpinfenlei/list` | 商品分类列表 |
| GET | `/springboot38hdw40x/config/list` | 系统配置列表 |

## 1. 本地先启动后端

在仓库根目录（需已安装 Maven / JDK8）：

```powershell
mvnw.cmd -Dmaven.test.skip=true spring-boot:run
```

默认监听 **http://127.0.0.1:8080**；数据库按 `src/main/resources/application.yml` 配置能连上即可。

## 2. JMeter 压测（推荐）

1. 安装 [Apache JMeter](https://jmeter.apache.org/download_jmeter.cgi)，将 `bin` 加入 `PATH`，或设置环境变量 **`JMETER_HOME`** 为解压目录。
2. 在 `tests` 目录执行：

```powershell
Set-Location tests
.\run-load-test.ps1
```

可选参数：`-ServiceHost`、`-Port`、`-Threads`、`-DurationSec`、`-RampSec`、`-Context`（默认 `springboot38hdw40x`）。

脚本会生成 `jmeter/output/result.jtl` 和 HTML 报告目录 `jmeter/output/html-report/index.html`。

仓库里的 **jmx 故意不嵌入监听器**（汇总报告、聚合报告等），避免不同 **Java 版本** 下加载或刷新界面时出现 `guicomp is null`、模块 `java.desktop` 等错误。在 JMeter 里 **打开** `warehouse-load.jmx` 后，请 **右键「测试计划」** → 添加 → 监听器 → 按需选 **汇总报告** / **用表格查看结果** 等，再运行压测即可在界面里看数据。看 HTML 报告仍用 `.\run-load-test.ps1`（生成 `jmeter/output/html-report`），不依赖在 jmx 里写监听器。

也可手动用仓库里的 jmx 跑（默认写死 `47.103.133.105:30081` 与 20/5/60 线程/爬坡/持续秒，**无** 形参函数字面量以便在 JMeter 图形界面里正常编辑；本机 `127.0.0.1:8080` 请改「HTTP 默认」或 `.\run-load-test.ps1 -ServiceHost 127.0.0.1 -Port 8080`）：

```text
jmeter -n -t jmeter/warehouse-load.jmx -l jmeter/output/result.jtl -e -o jmeter/output/html-report
```

## 3. Postman 集合

导入 `postman/warehouse.postman_collection.json`，环境用 `postman/warehouse.postman_environment.json`；将其中 `baseUrl` 改为你本机或部署机地址，例如 `http://127.0.0.1:8080/springboot38hdw40x`。

**说明**：`yonghu/login` 的示例体需改为你自己的测试账号，用于有登录态的联调，不作大规模压测。

## 4. 输出目录

`jmeter/output/` 可生成较大文件，建议加入版本忽略（如 `.jtl`、HTML 报告）后再提交。

## 5. K8s 部署访问方式（NodePort 示例）

与 `k8s/frontend.yaml`、`k8s/app.yaml` 一致：前端 **30080**，后端 **30081**。

| 用途 | 地址 |
|------|------|
| **浏览器打开管理端** | [http://47.103.133.105:30080/](http://47.103.133.105:30080/) |
| **直连 Spring Boot**（Postman、JMeter、curl、导出接口若需写死主机时） | `http://47.103.133.105:30081/springboot38hdw40x` |

**重要**：

- 用户应**只通过 30080** 使用系统。此时前端请求仍是 `http://47.103.133.105:30080/springboot38hdw40x/...`（与页面**同域**），由前端容器内 **Nginx** 反代到集群内 `warehouse-app:8080`，**不必**把 `http.js` 的 `baseURL` 改成 30081。
- **30081** 用于直连后端、压测或排错；导入 Postman 环境可选 `postman/warehouse.k8s-nodeport.environment.json`（`baseUrl` 指向 30081）。

**联调账号（测试环境，生产请修改）**：账号 `ddd`，密码 `dd`；Postman 中「用户登录」请求体填上述账号密码即可。

**对公网后端做 JMeter 压测示例**：

```powershell
.\run-load-test.ps1 -ServiceHost 47.103.133.105 -Port 30081
```
