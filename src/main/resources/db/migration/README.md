# 数据库迁移脚本

本目录包含数据库迁移脚本，用于为用户身份管理和财务模块增强功能添加必要的数据库字段。

## 迁移脚本列表

### V1__add_user_role_to_yonghu.sql
为 `yonghu` 表添加 `user_role` 字段，用于存储用户角色信息。

**字段说明：**
- 字段名：`user_role`
- 类型：`VARCHAR(50)`
- 默认值：`'DEALER'`（经销商）
- 可选值：
  - `DEALER` - 经销商
  - `INTERNAL_STAFF` - 内部员工
  - `PLATFORM_ADMIN` - 平台管理员
  - `WAREHOUSE_ADMIN` - 仓库管理员

### V2__add_payment_fields_to_rukuxinxi.sql
为 `rukuxinxi` 表添加付款状态和付款时间字段。

**字段说明：**
- `payment_status`：付款状态
  - 类型：`VARCHAR(20)`
  - 默认值：`'UNPAID'`（未付款）
  - 可选值：`PAID`（已付款）、`UNPAID`（未付款）
- `payment_time`：付款时间
  - 类型：`DATETIME`
  - 默认值：`NULL`

### V3__add_payment_fields_to_chukuxinxi.sql
为 `chukuxinxi` 表添加付款状态和付款时间字段。

**字段说明：**
- `payment_status`：付款状态
  - 类型：`VARCHAR(20)`
  - 默认值：`'UNPAID'`（未付款）
  - 可选值：`PAID`（已付款）、`UNPAID`（未付款）
- `payment_time`：付款时间
  - 类型：`DATETIME`
  - 默认值：`NULL`

## 执行迁移脚本

### 手动执行

如果您的项目没有使用 Flyway 或 Liquibase 等数据库迁移工具，请按照以下顺序手动执行 SQL 脚本：

1. 连接到 MySQL 数据库
2. 选择正确的数据库：`USE springboot38hdw40x;`
3. 按顺序执行脚本：
   ```bash
   mysql -u root -p springboot38hdw40x < V1__add_user_role_to_yonghu.sql
   mysql -u root -p springboot38hdw40x < V2__add_payment_fields_to_rukuxinxi.sql
   mysql -u root -p springboot38hdw40x < V3__add_payment_fields_to_chukuxinxi.sql
   ```

### 使用 Flyway（推荐）

如果您想使用 Flyway 自动管理数据库迁移，请按照以下步骤操作：

1. 在 `pom.xml` 中添加 Flyway 依赖：
   ```xml
   <dependency>
       <groupId>org.flywaydb</groupId>
       <artifactId>flyway-core</artifactId>
   </dependency>
   ```

2. 在 `application.yml` 中添加 Flyway 配置：
   ```yaml
   spring:
     flyway:
       enabled: true
       locations: classpath:db/migration
       baseline-on-migrate: true
   ```

3. 重启应用，Flyway 将自动执行迁移脚本。

## 验证迁移

执行迁移后，请验证以下内容：

1. 检查 `yonghu` 表是否包含 `user_role` 字段
2. 检查 `rukuxinxi` 表是否包含 `payment_status` 和 `payment_time` 字段
3. 检查 `chukuxinxi` 表是否包含 `payment_status` 和 `payment_time` 字段
4. 验证现有数据的默认值是否正确设置

验证 SQL：
```sql
-- 检查 yonghu 表结构
DESCRIBE yonghu;

-- 检查 rukuxinxi 表结构
DESCRIBE rukuxinxi;

-- 检查 chukuxinxi 表结构
DESCRIBE chukuxinxi;

-- 验证现有用户的默认角色
SELECT id, yonghuzhanghao, user_role FROM yonghu LIMIT 10;

-- 验证现有入库记录的默认付款状态
SELECT id, fuzhuangbianhao, payment_status, payment_time FROM rukuxinxi LIMIT 10;

-- 验证现有出库记录的默认付款状态
SELECT id, fuzhuangbianhao, payment_status, payment_time FROM chukuxinxi LIMIT 10;
```

## 回滚

如果需要回滚迁移，请执行以下 SQL：

```sql
-- 回滚 V3
ALTER TABLE chukuxinxi DROP COLUMN payment_status;
ALTER TABLE chukuxinxi DROP COLUMN payment_time;

-- 回滚 V2
ALTER TABLE rukuxinxi DROP COLUMN payment_status;
ALTER TABLE rukuxinxi DROP COLUMN payment_time;

-- 回滚 V1
ALTER TABLE yonghu DROP COLUMN user_role;
```

## 注意事项

1. **备份数据库**：在执行迁移脚本之前，请务必备份生产数据库。
2. **测试环境验证**：先在测试环境中执行迁移脚本，确认无误后再在生产环境执行。
3. **停机维护**：建议在低峰期或维护窗口执行迁移，以减少对用户的影响。
4. **权限检查**：确保执行迁移的数据库用户具有 ALTER TABLE 权限。
5. **字符集**：确保数据库和表使用 UTF-8 字符集，以支持中文注释。
