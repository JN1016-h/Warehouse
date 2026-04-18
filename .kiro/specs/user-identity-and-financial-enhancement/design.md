# 设计文档

## 概述

本设计文档描述了仓库管理系统中用户身份管理和财务模块增强功能的技术实现方案。系统将在现有Spring Boot + Vue.js架构基础上，扩展用户角色体系，增加身份标识UI组件，并完善财务模块的应收应付款项管理和报表导出功能。

核心设计目标：
- 在现有用户表基础上扩展角色字段，支持四种用户角色
- 在前端实现全局身份标识组件，持续显示用户身份
- 扩展入库和出库表，增加付款状态和付款时间字段
- 实现报表条件查询和Excel/PDF导出功能
- 提供财务金额统计和付款状态管理功能

## 架构

### 系统架构

系统采用前后端分离架构：

```
┌─────────────────────────────────────────────────────────────┐
│                        前端层 (Vue.js)                        │
├─────────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ 身份标识组件  │  │ 个人中心页面  │  │ 财务报表页面  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ 应收款项页面  │  │ 应付款项页面  │  │ 导出功能组件  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            │
                    HTTP/JSON API
                            │
┌─────────────────────────────────────────────────────────────┐
│                    后端层 (Spring Boot)                       │
├─────────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ 用户控制器    │  │ 财务控制器    │  │ 报表控制器    │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ 用户服务      │  │ 财务服务      │  │ 导出服务      │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ 用户DAO       │  │ 入库DAO       │  │ 出库DAO       │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            │
                    MyBatis Plus
                            │
┌─────────────────────────────────────────────────────────────┐
│                      数据库层 (MySQL)                         │
├─────────────────────────────────────────────────────────────┤
│  yonghu (用户表)  │  rukuxinxi (入库表)  │  chukuxinxi (出库表) │
└─────────────────────────────────────────────────────────────┘
```

### 技术栈

- **后端**: Spring Boot 2.x, MyBatis Plus, Apache POI (Excel), iText (PDF)
- **前端**: Vue.js 2.x/3.x, Element UI, Axios
- **数据库**: MySQL 5.7+
- **构建工具**: Maven (后端), npm/yarn (前端)

## 组件和接口

### 后端组件

#### 1. 用户角色枚举 (UserRole)

```java
public enum UserRole {
    DEALER("经销商"),
    INTERNAL_STAFF("内部员工"),
    PLATFORM_ADMIN("平台管理员"),
    WAREHOUSE_ADMIN("仓库管理员");
    
    private String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
```

#### 2. 付款状态枚举 (PaymentStatus)

```java
public enum PaymentStatus {
    UNPAID("未付款"),
    PAID("已付款");
    
    private String displayName;
    
    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
```

#### 3. 用户服务接口 (UserService)

```java
public interface UserService {
    /**
     * 获取用户详细信息（包含角色）
     */
    UserDTO getUserInfo(Long userId);
    
    /**
     * 更新用户角色
     */
    boolean updateUserRole(Long userId, UserRole role);
    
    /**
     * 验证角色是否有效
     */
    boolean isValidRole(String role);
}
```

#### 4. 财务服务接口 (FinanceService)

```java
public interface FinanceService {
    /**
     * 查询应收款项列表
     */
    PageResult<ReceivableDTO> queryReceivables(ReceivableQuery query);
    
    /**
     * 查询应付款项列表
     */
    PageResult<PayableDTO> queryPayables(PayableQuery query);
    
    /**
     * 更新付款状态
     */
    boolean updatePaymentStatus(Long orderId, String orderType, PaymentStatus status);
    
    /**
     * 统计应收款项金额
     */
    FinanceSummary calculateReceivableSummary(ReceivableQuery query);
    
    /**
     * 统计应付款项金额
     */
    FinanceSummary calculatePayableSummary(PayableQuery query);
}
```

#### 5. 报表导出服务接口 (ExportService)

```java
public interface ExportService {
    /**
     * 导出Excel文件
     */
    byte[] exportToExcel(List<? extends Object> data, String[] headers, String[] fields);
    
    /**
     * 导出PDF文件
     */
    byte[] exportToPDF(List<? extends Object> data, String title, String[] headers, String[] fields);
    
    /**
     * 验证导出数据量
     */
    boolean validateExportSize(int dataSize, String exportType);
}
```

#### 6. 财务控制器 (FinanceController)

```java
@RestController
@RequestMapping("/finance")
public class FinanceController {
    
    @GetMapping("/receivables")
    public R queryReceivables(@RequestParam Map<String, Object> params);
    
    @GetMapping("/payables")
    public R queryPayables(@RequestParam Map<String, Object> params);
    
    @PostMapping("/updatePaymentStatus")
    public R updatePaymentStatus(@RequestBody PaymentUpdateDTO dto);
    
    @GetMapping("/receivables/summary")
    public R getReceivableSummary(@RequestParam Map<String, Object> params);
    
    @GetMapping("/payables/summary")
    public R getPayableSummary(@RequestParam Map<String, Object> params);
    
    @GetMapping("/receivables/export/excel")
    public void exportReceivablesToExcel(@RequestParam Map<String, Object> params, HttpServletResponse response);
    
    @GetMapping("/receivables/export/pdf")
    public void exportReceivablesToPDF(@RequestParam Map<String, Object> params, HttpServletResponse response);
    
    @GetMapping("/payables/export/excel")
    public void exportPayablesToExcel(@RequestParam Map<String, Object> params, HttpServletResponse response);
    
    @GetMapping("/payables/export/pdf")
    public void exportPayablesToPDF(@RequestParam Map<String, Object> params, HttpServletResponse response);
}
```

### 前端组件

#### 1. 身份标识组件 (IdentityBadge.vue)

```vue
<template>
  <div class="identity-badge">
    <el-tag :type="roleType" size="small">
      <i :class="roleIcon"></i>
      {{ userName }} - {{ roleDisplayName }}
    </el-tag>
  </div>
</template>

<script>
export default {
  name: 'IdentityBadge',
  computed: {
    userName() {
      return this.$store.state.user.name;
    },
    userRole() {
      return this.$store.state.user.role;
    },
    roleDisplayName() {
      const roleMap = {
        'DEALER': '经销商',
        'INTERNAL_STAFF': '内部员工',
        'PLATFORM_ADMIN': '平台管理员',
        'WAREHOUSE_ADMIN': '仓库管理员'
      };
      return roleMap[this.userRole] || '未知角色';
    },
    roleType() {
      const typeMap = {
        'DEALER': 'info',
        'INTERNAL_STAFF': 'success',
        'PLATFORM_ADMIN': 'danger',
        'WAREHOUSE_ADMIN': 'warning'
      };
      return typeMap[this.userRole] || '';
    },
    roleIcon() {
      const iconMap = {
        'DEALER': 'el-icon-user',
        'INTERNAL_STAFF': 'el-icon-s-custom',
        'PLATFORM_ADMIN': 'el-icon-s-tools',
        'WAREHOUSE_ADMIN': 'el-icon-office-building'
      };
      return iconMap[this.userRole] || 'el-icon-user';
    }
  }
}
</script>
```

#### 2. 财务报表页面组件 (FinanceReport.vue)

```vue
<template>
  <div class="finance-report">
    <!-- 查询条件 -->
    <el-form :inline="true" :model="queryForm">
      <el-form-item label="日期范围">
        <el-date-picker
          v-model="queryForm.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="付款状态">
        <el-select v-model="queryForm.paymentStatus">
          <el-option label="全部" value=""></el-option>
          <el-option label="已付款" value="PAID"></el-option>
          <el-option label="未付款" value="UNPAID"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="名称">
        <el-input v-model="queryForm.name" placeholder="供应商/客户名称"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
    
    <!-- 金额统计 -->
    <el-card class="summary-card">
      <div class="summary-item">
        <span>总金额：</span>
        <span class="amount">{{ formatAmount(summary.totalAmount) }}</span>
      </div>
      <div class="summary-item">
        <span>已付款：</span>
        <span class="amount paid">{{ formatAmount(summary.paidAmount) }}</span>
      </div>
      <div class="summary-item">
        <span>未付款：</span>
        <span class="amount unpaid">{{ formatAmount(summary.unpaidAmount) }}</span>
      </div>
    </el-card>
    
    <!-- 导出按钮 -->
    <div class="export-buttons">
      <el-button type="success" @click="exportExcel">导出Excel</el-button>
      <el-button type="warning" @click="exportPDF">导出PDF</el-button>
    </div>
    
    <!-- 数据表格 -->
    <el-table :data="tableData" border>
      <el-table-column prop="orderNo" label="订单号"></el-table-column>
      <el-table-column prop="partnerName" label="名称"></el-table-column>
      <el-table-column prop="amount" label="金额" :formatter="formatAmountColumn"></el-table-column>
      <el-table-column prop="paymentStatus" label="付款状态">
        <template slot-scope="scope">
          <el-tag :type="scope.row.paymentStatus === 'PAID' ? 'success' : 'danger'">
            {{ scope.row.paymentStatus === 'PAID' ? '已付款' : '未付款' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="orderDate" label="订单日期"></el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button 
            v-if="scope.row.paymentStatus === 'UNPAID'"
            type="text" 
            @click="markAsPaid(scope.row)">
            标记为已付款
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页 -->
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pagination.page"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pagination.limit"
      :total="pagination.total"
      layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>
  </div>
</template>

<script>
export default {
  name: 'FinanceReport',
  data() {
    return {
      queryForm: {
        dateRange: [],
        paymentStatus: '',
        name: ''
      },
      tableData: [],
      summary: {
        totalAmount: 0,
        paidAmount: 0,
        unpaidAmount: 0
      },
      pagination: {
        page: 1,
        limit: 10,
        total: 0
      }
    };
  },
  methods: {
    handleQuery() {
      // 查询逻辑
    },
    handleReset() {
      // 重置逻辑
    },
    exportExcel() {
      // Excel导出逻辑
    },
    exportPDF() {
      // PDF导出逻辑
    },
    markAsPaid(row) {
      // 更新付款状态逻辑
    },
    formatAmount(amount) {
      return '¥' + Number(amount).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      });
    },
    formatAmountColumn(row, column, cellValue) {
      return this.formatAmount(cellValue);
    }
  }
}
</script>
```

## 数据模型

### 数据库表结构修改

#### 1. 用户表 (yonghu) - 新增字段

```sql
ALTER TABLE yonghu ADD COLUMN user_role VARCHAR(50) DEFAULT 'DEALER' COMMENT '用户角色：DEALER-经销商, INTERNAL_STAFF-内部员工, PLATFORM_ADMIN-平台管理员, WAREHOUSE_ADMIN-仓库管理员';
```

#### 2. 入库信息表 (rukuxinxi) - 新增字段

```sql
ALTER TABLE rukuxinxi ADD COLUMN payment_status VARCHAR(20) DEFAULT 'UNPAID' COMMENT '付款状态：PAID-已付款, UNPAID-未付款';
ALTER TABLE rukuxinxi ADD COLUMN payment_time DATETIME NULL COMMENT '付款时间';
```

#### 3. 出库信息表 (chukuxinxi) - 新增字段

```sql
ALTER TABLE chukuxinxi ADD COLUMN payment_status VARCHAR(20) DEFAULT 'UNPAID' COMMENT '付款状态：PAID-已付款, UNPAID-未付款';
ALTER TABLE chukuxinxi ADD COLUMN payment_time DATETIME NULL COMMENT '付款时间';
```

### 数据传输对象 (DTO)

#### 1. 用户信息DTO (UserDTO)

```java
public class UserDTO {
    private Long id;
    private String username;
    private String name;
    private String phone;
    private String email;
    private UserRole role;
    private String roleDisplayName;
    private Date createTime;
}
```

#### 2. 应收款项DTO (ReceivableDTO)

```java
public class ReceivableDTO {
    private Long id;
    private String orderNo;           // 订单号
    private String customerName;      // 客户名称
    private BigDecimal amount;        // 应收金额
    private PaymentStatus paymentStatus;  // 付款状态
    private Date orderDate;           // 订单日期
    private Date paymentTime;         // 付款时间
}
```

#### 3. 应付款项DTO (PayableDTO)

```java
public class PayableDTO {
    private Long id;
    private String orderNo;           // 订单号
    private String supplierName;      // 供应商名称
    private BigDecimal amount;        // 应付金额
    private PaymentStatus paymentStatus;  // 付款状态
    private Date orderDate;           // 订单日期
    private Date paymentTime;         // 付款时间
}
```

#### 4. 财务汇总DTO (FinanceSummary)

```java
public class FinanceSummary {
    private BigDecimal totalAmount;   // 总金额
    private BigDecimal paidAmount;    // 已付款金额
    private BigDecimal unpaidAmount;  // 未付款金额
    private Integer totalCount;       // 总记录数
    private Integer paidCount;        // 已付款记录数
    private Integer unpaidCount;      // 未付款记录数
}
```

#### 5. 查询条件对象 (ReceivableQuery / PayableQuery)

```java
public class ReceivableQuery {
    private Date startDate;           // 开始日期
    private Date endDate;             // 结束日期
    private PaymentStatus paymentStatus;  // 付款状态
    private String customerName;      // 客户名称（模糊查询）
    private Integer page;             // 页码
    private Integer limit;            // 每页数量
}

public class PayableQuery {
    private Date startDate;           // 开始日期
    private Date endDate;             // 结束日期
    private PaymentStatus paymentStatus;  // 付款状态
    private String supplierName;      // 供应商名称（模糊查询）
    private Integer page;             // 页码
    private Integer limit;            // 每页数量
}
```

#### 6. 付款状态更新DTO (PaymentUpdateDTO)

```java
public class PaymentUpdateDTO {
    private Long orderId;             // 订单ID
    private String orderType;         // 订单类型：INBOUND-入库, OUTBOUND-出库
    private PaymentStatus paymentStatus;  // 新的付款状态
}
```

### Vuex状态管理

```javascript
// store/modules/user.js
export default {
  state: {
    id: null,
    username: '',
    name: '',
    role: '',
    roleDisplayName: '',
    token: ''
  },
  mutations: {
    SET_USER_INFO(state, userInfo) {
      state.id = userInfo.id;
      state.username = userInfo.username;
      state.name = userInfo.name;
      state.role = userInfo.role;
      state.roleDisplayName = userInfo.roleDisplayName;
    },
    SET_TOKEN(state, token) {
      state.token = token;
    }
  },
  actions: {
    async getUserInfo({ commit }) {
      const response = await axios.get('/user/info');
      commit('SET_USER_INFO', response.data);
    }
  }
}
```

## 正确性属性

*属性是一个特征或行为，应该在系统的所有有效执行中保持为真——本质上是关于系统应该做什么的正式声明。属性作为人类可读规范和机器可验证正确性保证之间的桥梁。*


### 属性 1: 用户角色分配和查询一致性

*对于任意*新创建的用户，当查询该用户信息时，返回的数据应该包含一个有效的角色字段，且该角色必须是四种预定义角色之一（DEALER、INTERNAL_STAFF、PLATFORM_ADMIN、WAREHOUSE_ADMIN）。

**验证需求: 1.2, 1.3**

### 属性 2: 角色验证拒绝无效输入

*对于任意*不在预定义角色列表中的字符串，当尝试更新用户角色时，系统应该拒绝该操作并返回错误。

**验证需求: 1.4**

### 属性 3: 角色显示名称映射正确性

*对于任意*用户角色（DEALER、INTERNAL_STAFF、PLATFORM_ADMIN、WAREHOUSE_ADMIN），身份标识组件显示的中文名称应该与该角色对应的预定义显示名称完全匹配（经销商、内部员工、平台管理员、仓库管理员）。

**验证需求: 2.4, 2.5, 2.6, 2.7**

### 属性 4: 身份标识包含必需信息

*对于任意*已登录用户，渲染的身份标识组件应该同时包含用户名称和角色类型信息。

**验证需求: 2.3**

### 属性 5: 个人中心信息完整性

*对于任意*用户，个人中心页面应该显示用户的基本信息（用户名、姓名、联系方式）、角色类型和账户创建时间。

**验证需求: 3.1, 3.2, 3.3**

### 属性 6: 条件查询结果正确性

*对于任意*查询条件组合（日期范围、付款状态、名称），返回的所有记录都应该满足所有指定的查询条件。

**验证需求: 4.4**

### 属性 7: Excel导出数据完整性

*对于任意*查询结果集，导出的Excel文件应该包含所有记录和所有必需的列（订单号、名称、金额、付款状态、日期），且数据内容应该与查询结果一致。

**验证需求: 5.1, 5.2**

### 属性 8: PDF导出数据完整性

*对于任意*查询结果集，导出的PDF文件应该包含所有记录、报表标题、生成时间，且数据内容应该与查询结果一致。

**验证需求: 6.1, 6.2**

### 属性 9: 应收款项列表完整性

*对于任意*出库订单集合，应收款项列表应该包含所有这些订单，且每条记录都应该包含订单号、客户名称、应收金额、付款状态和订单日期。

**验证需求: 7.1, 7.2**

### 属性 10: 应付款项列表完整性

*对于任意*入库订单集合，应付款项列表应该包含所有这些订单，且每条记录都应该包含订单号、供应商名称、应付金额、付款状态和订单日期。

**验证需求: 8.1, 8.2**

### 属性 11: 付款状态筛选正确性

*对于任意*付款状态筛选条件（已付款或未付款），返回的所有记录的付款状态都应该与筛选条件匹配。

**验证需求: 7.3, 8.3**

### 属性 12: 付款状态更新时间记录

*对于任意*订单，当付款状态从"未付款"更新为"已付款"时，系统应该记录付款时间戳；当从"已付款"更新为"未付款"时，系统应该清除付款时间戳。

**验证需求: 7.4, 8.4, 10.3, 10.4**

### 属性 13: 金额统计不变量

*对于任意*款项列表（应收或应付），总金额应该等于已付款金额加上未付款金额。

**验证需求: 9.1, 9.2, 9.3, 9.4**

### 属性 14: 筛选后统计范围正确性

*对于任意*筛选条件，统计金额（总额、已付款、未付款）应该仅计算符合筛选条件的记录。

**验证需求: 9.5**

### 属性 15: 金额格式化一致性

*对于任意*金额值，格式化后的字符串应该保留两位小数并使用千分位分隔符（例如：1,234.56）。

**验证需求: 9.6**

### 属性 16: 新订单默认付款状态

*对于任意*新创建的入库或出库订单，其付款状态应该默认设置为"未付款"（UNPAID）。

**验证需求: 10.2**

### 属性 17: 订单详情显示付款状态

*对于任意*订单，订单详情页面应该显示该订单当前的付款状态。

**验证需求: 10.5**

## 错误处理

### 输入验证

1. **角色验证**：
   - 更新用户角色时，验证角色值是否在枚举范围内
   - 无效角色返回400错误，错误消息："无效的用户角色"

2. **日期范围验证**：
   - 开始日期不能晚于结束日期
   - 日期范围不能超过1年
   - 无效日期范围返回400错误，错误消息："无效的日期范围"

3. **导出数量限制**：
   - Excel导出：最多10000条记录
   - PDF导出：最多1000条记录
   - 超出限制返回400错误，错误消息："导出数据量过大，请缩小查询范围"

4. **付款状态验证**：
   - 验证付款状态值是否为PAID或UNPAID
   - 无效状态返回400错误，错误消息："无效的付款状态"

### 业务逻辑错误

1. **订单不存在**：
   - 更新付款状态时，如果订单ID不存在
   - 返回404错误，错误消息："订单不存在"

2. **权限不足**：
   - 非授权用户尝试修改付款状态
   - 返回403错误，错误消息："权限不足"

3. **数据库操作失败**：
   - 数据库连接失败或SQL执行错误
   - 返回500错误，错误消息："系统错误，请稍后重试"
   - 记录详细错误日志

### 导出错误处理

1. **Excel生成失败**：
   - Apache POI异常
   - 返回500错误，错误消息："Excel生成失败"
   - 记录异常堆栈

2. **PDF生成失败**：
   - iText异常
   - 返回500错误，错误消息："PDF生成失败"
   - 记录异常堆栈

3. **文件下载失败**：
   - 响应流写入异常
   - 记录错误日志
   - 前端显示友好提示

### 前端错误处理

1. **API请求失败**：
   - 使用Axios拦截器统一处理
   - 显示Element UI的Message提示
   - 根据HTTP状态码显示不同消息

2. **数据加载失败**：
   - 显示空状态页面
   - 提供重试按钮

3. **导出超时**：
   - 设置30秒超时
   - 超时后显示提示："导出时间较长，请稍后重试"

## 测试策略

### 单元测试

使用JUnit 5和Mockito进行后端单元测试，使用Jest进行前端单元测试。

**后端单元测试覆盖**：
1. 枚举类测试（UserRole、PaymentStatus）
2. DTO转换测试
3. 服务层业务逻辑测试
4. 工具类测试（金额格式化、日期处理）

**前端单元测试覆盖**：
1. Vuex状态管理测试
2. 工具函数测试（金额格式化、日期格式化）
3. 组件渲染测试（使用Vue Test Utils）

### 属性测试

使用JUnit-Quickcheck进行Java属性测试，使用fast-check进行JavaScript属性测试。每个属性测试至少运行100次迭代。

**属性测试配置**：
```java
@Property(trials = 100)
public void testPropertyName(@ForAll Generator generator) {
    // 测试逻辑
}
```

**属性测试标签格式**：
```java
// Feature: user-identity-and-financial-enhancement, Property 1: 用户角色分配和查询一致性
```

**关键属性测试**：
1. 属性1：用户角色分配和查询一致性
2. 属性2：角色验证拒绝无效输入
3. 属性6：条件查询结果正确性
4. 属性7：Excel导出数据完整性
5. 属性8：PDF导出数据完整性
6. 属性11：付款状态筛选正确性
7. 属性12：付款状态更新时间记录
8. 属性13：金额统计不变量
9. 属性15：金额格式化一致性

### 集成测试

使用Spring Boot Test进行后端集成测试，使用Cypress进行前端E2E测试。

**后端集成测试覆盖**：
1. Controller层API测试（使用MockMvc）
2. 数据库操作测试（使用H2内存数据库）
3. 导出功能集成测试

**前端E2E测试覆盖**：
1. 用户登录后身份标识显示
2. 个人中心页面信息展示
3. 财务报表查询和筛选
4. 付款状态更新流程
5. 报表导出功能（模拟下载）

### 测试数据生成

**属性测试数据生成器**：
1. 用户生成器：随机生成用户名、姓名、角色
2. 订单生成器：随机生成订单号、金额、日期、付款状态
3. 查询条件生成器：随机生成日期范围、状态、名称
4. 金额生成器：生成各种边界值（0、负数、大数、小数）

**边界值测试**：
1. 空字符串、null值
2. 最大/最小日期
3. 极大/极小金额
4. 空列表、单元素列表、大列表
5. 特殊字符、Unicode字符

### 性能测试

**关键性能指标**：
1. 报表查询响应时间：< 2秒（1000条记录）
2. Excel导出时间：< 10秒（10000条记录）
3. PDF导出时间：< 5秒（1000条记录）
4. 金额统计计算时间：< 1秒（10000条记录）

**性能测试工具**：
- JMeter进行后端API压力测试
- Chrome DevTools进行前端性能分析

### 测试环境

**开发环境**：
- 本地MySQL数据库
- 使用测试数据脚本初始化

**CI/CD环境**：
- H2内存数据库（单元测试和集成测试）
- Docker容器化MySQL（E2E测试）
- 自动化测试在每次提交时运行

## 实施注意事项

### 数据库迁移

1. **迁移脚本顺序**：
   - V1: 添加yonghu表的user_role字段
   - V2: 添加rukuxinxi表的payment_status和payment_time字段
   - V3: 添加chukuxinxi表的payment_status和payment_time字段
   - V4: 为现有数据设置默认值

2. **数据迁移策略**：
   - 现有用户默认角色设置为DEALER
   - 现有订单默认付款状态设置为UNPAID
   - 使用事务确保数据一致性

3. **回滚计划**：
   - 保留迁移前的数据库备份
   - 准备回滚SQL脚本

### 兼容性考虑

1. **前端兼容性**：
   - 支持Chrome 90+、Firefox 88+、Safari 14+
   - 使用Babel转译ES6+代码
   - 使用Autoprefixer处理CSS兼容性

2. **后端兼容性**：
   - Java 8+
   - Spring Boot 2.3+
   - MySQL 5.7+

3. **API版本控制**：
   - 新增API使用/api/v1前缀
   - 保持现有API向后兼容

### 安全考虑

1. **权限控制**：
   - 使用Spring Security进行认证和授权
   - 不同角色具有不同的操作权限
   - 敏感操作需要二次验证

2. **数据验证**：
   - 所有用户输入必须验证和清理
   - 防止SQL注入（使用参数化查询）
   - 防止XSS攻击（前端输出转义）

3. **导出安全**：
   - 限制导出数据量
   - 导出操作记录审计日志
   - 敏感数据脱敏处理

### 性能优化

1. **数据库优化**：
   - 为payment_status字段添加索引
   - 为user_role字段添加索引
   - 优化查询SQL，避免全表扫描

2. **缓存策略**：
   - 用户角色信息缓存（Redis）
   - 金额统计结果缓存（5分钟过期）
   - 前端Vuex状态缓存

3. **分页优化**：
   - 使用游标分页替代偏移分页（大数据量）
   - 前端虚拟滚动（长列表）

### 监控和日志

1. **关键操作日志**：
   - 用户角色变更
   - 付款状态更新
   - 报表导出操作

2. **性能监控**：
   - API响应时间监控
   - 数据库查询性能监控
   - 导出操作耗时监控

3. **错误监控**：
   - 异常堆栈记录
   - 错误率统计
   - 告警机制

## 部署计划

### 部署步骤

1. **数据库迁移**（停机维护窗口）：
   - 备份生产数据库
   - 执行迁移脚本
   - 验证数据完整性

2. **后端部署**：
   - 构建JAR包
   - 部署到应用服务器
   - 健康检查

3. **前端部署**：
   - 构建生产版本
   - 部署到Web服务器
   - 清除CDN缓存

4. **验证测试**：
   - 冒烟测试关键功能
   - 验证新功能可用性
   - 检查日志无异常

### 回滚方案

1. **数据库回滚**：
   - 恢复数据库备份
   - 执行回滚脚本

2. **应用回滚**：
   - 部署上一个稳定版本
   - 重启应用服务

3. **验证回滚**：
   - 验证系统功能正常
   - 检查数据一致性

## 附录

### 依赖库版本

**后端依赖**：
```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.3</version>
</dependency>
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itextpdf</artifactId>
    <version>5.5.13.3</version>
</dependency>
<dependency>
    <groupId>net.java.quickcheck</groupId>
    <artifactId>quickcheck</artifactId>
    <version>0.6</version>
    <scope>test</scope>
</dependency>
```

**前端依赖**：
```json
{
  "dependencies": {
    "vue": "^2.6.14",
    "vuex": "^3.6.2",
    "element-ui": "^2.15.13",
    "axios": "^0.27.2"
  },
  "devDependencies": {
    "fast-check": "^3.1.0",
    "jest": "^28.1.0",
    "@vue/test-utils": "^1.3.0"
  }
}
```

### API接口文档

详细的API接口文档将使用Swagger/OpenAPI规范编写，包括：
- 请求方法和路径
- 请求参数和请求体
- 响应格式和状态码
- 错误码说明
- 示例请求和响应

### 数据库ER图

```
┌─────────────┐         ┌──────────────┐         ┌──────────────┐
│   yonghu    │         │  rukuxinxi   │         │  chukuxinxi  │
├─────────────┤         ├──────────────┤         ├──────────────┤
│ id (PK)     │         │ id (PK)      │         │ id (PK)      │
│ username    │         │ order_no     │         │ order_no     │
│ name        │         │ supplier_id  │         │ customer_id  │
│ phone       │         │ amount       │         │ amount       │
│ email       │         │ order_date   │         │ order_date   │
│ user_role   │◄───┐    │ payment_     │         │ payment_     │
│ create_time │    │    │   status     │         │   status     │
└─────────────┘    │    │ payment_time │         │ payment_time │
                   │    └──────────────┘         └──────────────┘
                   │            │                        │
                   │            │                        │
                   │            ▼                        ▼
                   │    ┌──────────────┐         ┌──────────────┐
                   │    │gongyingshang │         │   kehu       │
                   │    ├──────────────┤         ├──────────────┤
                   └────│ user_id (FK) │         │ user_id (FK) │
                        │ name         │         │ name         │
                        │ contact      │         │ contact      │
                        └──────────────┘         └──────────────┘
```
