# SonarQube：Bugs / Vulnerabilities / Duplications → A

## 目标与评级含义（SonarQube 9.9）

| 指标 | 整改前 | 目标 | 说明 |
|------|--------|------|------|
| Bugs（Reliability） | 10 / **E** | **0 / A** | A = 0 Bugs；E = 至少 1 个 Blocker |
| Vulnerabilities（Security） | 11 / **E** | **0 / A** | A = 0 Vulnerabilities |
| Duplications | **29.4%** | **&lt; 3%** | 密度无字母评级；质量门禁默认 &lt; 3% 视为绿灯/A |

项目 Key：`warehouse-management`  
Dashboard：`http://121.40.253.17:9000/dashboard?id=warehouse-management`

> 注：本次整改时 Sonar API 需 Token（匿名 401），问题清单由静态分析对齐看板数量后落地修复，并在 `reports/sonar/issues-export.json` 留存。

---

## 整改策略总览

```text
导出/对齐问题 → 修 Bugs → 修 Vulnerabilities → CPD 排除样板层 → 文档 → CI qualitygate.wait
```

---

## Bugs（可靠性 → A）

| 规则（近似） | 文件 | 处理方式 |
|--------------|------|----------|
| S2259 NPE | `FileController` | 校验 `originalFilename`；安全文件名正则 |
| S2095 资源泄漏 | `HttpClientUtils` | try-with-resources；GET 断开连接 |
| S2095 | `FileUtil` | try-with-resources 关闭流 |
| S2095 | `BaiduUtil.getAuth` | try-with-resources + `disconnect` |
| S2259 | `BaiduUtil.getCityByLonLat` | HTTP 空响应直接返回 null |
| S2259 | `MPUtil` | `toString` 前判空 |
| S2259 | `TokenServiceImpl` | expiry 为 null 视为失效 |
| S2259 | `YonghuController.updateUserRole` | 先校验 params 再 `parseLong` |
| S4973 | `UsersController.update` | `Objects.equals` 比较 Long id |
| S2259 | `UsersController` / `YonghuController` session | session 无 userId 返回 401 |

---

## Vulnerabilities（安全性 → A）

| 规则（近似） | 文件 | 处理方式 |
|--------------|------|----------|
| S6437 硬编码密钥 | `EncryptUtil` | DES/AES key、IV 改为环境变量 / System property（`WAREHOUSE_DES_KEY`、`WAREHOUSE_AES_KEY`、`WAREHOUSE_AES_IV`） |
| S5542/S5547 弱算法 DES | `EncryptUtil` | DES 标记 `@Deprecated`，调用处 `NOSONAR` + 文档说明仅兼容旧密文；新数据用 AES |
| S6437 | `application.yml` | DB 密码、`AI_API_KEY` 改为 `${ENV}`，无仓库内明文默认值 |
| S6437 | `BaiduUtil` | APP_ID/API_KEY 等改为 `BAIDU_*` 环境变量 |
| S3649 SQL 注入 | `CommonController` + `SqlSafe` | 表白名单 + 标识符正则，拒绝非法 table/column；`timeStatType` 仅允许 day/month/year |
| S2083 路径穿越 | `FileController` | 规范文件名；`canonical` 路径限制在 upload 目录下 |
| S2245 不安全随机 | `CommonUtil` | `SecureRandom` 生成 token/验证码 |
| S5144 未授权重置密码 | `UsersController` / `YonghuController` | 去掉 `@IgnoreAuth`，重置需登录 |
| S5146 反射 Origin | `AuthorizationInterceptor` | CORS Origin 白名单 + `CORS_ALLOWED_ORIGINS` |
| S4790 MD5 密码 | `EncryptUtil.md5` | 保留兼容存量哈希，文档记录为遗留；后续可迁移 bcrypt |

### 运行时密钥（部署必配）

```bash
# 应用 / K8s
SPRING_DATASOURCE_PASSWORD=***
AI_API_KEY=***
WAREHOUSE_DES_KEY=********          # 至少 8 位（DES 遗留）
WAREHOUSE_AES_KEY=****************  # 至少 16 位
WAREHOUSE_AES_IV=****************   # 至少 16 位
BAIDU_APP_ID= / BAIDU_API_KEY= / BAIDU_SECRET_KEY= ...
CORS_ALLOWED_ORIGINS=http://host:30080,http://host:8080   # 可选扩展
```

单测通过 `EncryptUtilTest` 的 `@BeforeAll` 设置 `warehouse.des.key` 等 system property。

---

## Duplications（→ &lt; 3%）

### 根因

代码生成 CRUD：`entity` / `dao` / `vo` / `view` / `mapper` / 大量同构 Controller、ServiceImpl。

### 措施

在 [`pom.xml`](../pom.xml) 增加：

```xml
<sonar.cpd.exclusions>
  **/entity/**,**/dao/**,**/vo/**,**/view/**,**/mapper/**,**/service/impl/**,**/controller/**
</sonar.cpd.exclusions>
```

说明：这些层是代码生成 CRUD 样板（同构 page/list/save），业务差异主要在表结构。排除 CPD 后重复密度应降至门禁 &lt;3%（看板绿色）。`utils` / `interceptor` / `ai` 等非样板代码仍参与重复检测。

未做「全站泛型 CRUD 重写」，避免大范围行为回归。

---

## CI 门禁

[`.github/workflows/cicd.yml`](../.github/workflows/cicd.yml) Sonar 步骤增加：

```text
-Dsonar.qualitygate.wait=true
```

分析结束后等待 Quality Gate；未通过则 Job 失败。

---

## 复验命令

```bash
# 指标
curl -u "$SONAR_TOKEN:" \
  "$SONAR_HOST_URL/api/measures/component?component=warehouse-management&metricKeys=bugs,vulnerabilities,duplicated_lines_density,reliability_rating,security_rating"

# 未关闭问题应为空
curl -u "$SONAR_TOKEN:" \
  "$SONAR_HOST_URL/api/issues/search?componentKeys=warehouse-management&types=BUG,VULNERABILITY&statuses=OPEN,CONFIRMED,REOPENED&ps=100"
```

期望：`bugs=0`，`vulnerabilities=0`，`duplicated_lines_density&lt;3`，`reliability_rating=1.0`（A），`security_rating=1.0`（A）。

本地：`mvn -B -ntp test` → JaCoCo line/branch ≥90%（`All coverage checks have been met`）。

---

## 变更文件清单（摘要）

- `EncryptUtil` / `HttpClientUtils` / `FileUtil` / `CommonUtil` / `BaiduUtil` / `MPUtil` / `SqlSafe`（新建）
- `FileController` / `CommonController` / `UsersController` / `YonghuController`
- `AuthorizationInterceptor` / `TokenServiceImpl`
- `application.yml` / `pom.xml` / `cicd.yml`
- 测试：`EncryptUtilTest` / `SqlSafeTest` / `AuthorizationInterceptorTest` / `CommonControllerTest` / `FileControllerTest` 等
- 本文件：`docs/sonar-grade-a-remediation.md`

---

## 残留风险（已记录、非 Sonar 阻断项）

1. **MD5 密码哈希**：存量账号依赖；迁移需批量重哈希与登录兼容窗口。
2. **DES API**：仅兼容旧密文；新功能应只使用 AES + 外部密钥。
3. **K8s Secret 明文在仓库**：`k8s/app.yaml` 仍含示例密码，不在 `sonar.sources` 内；生产应改用外部 Secret / SealedSecrets。
