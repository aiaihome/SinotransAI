# Sinotrans 空白提单管理系统 - Copilot 全局指令（v1.1 Demo）

> **文档类型**: 项目开发规范与约束
> **适用范围**: Sinotrans 空白提单管理系统
> **生效对象**: 所有开发人员、GitHub Copilot AI助手
> **最后更新**: 2026-04-01

---

## 📋 项目概述

Sinotrans 空白提单管理系统是一个 B 端货代物流内部管理系统，聚焦于空白提单号码段的全流程数字化管理。当前 Demo 版本采用前后端分离架构，默认已登录，不区分用户角色与权限，核心技术栈如下：

- 后端: Java 17 + SpringBoot 3.1.x
- 数据库: MySQL 8.x（开发/生产）
- 前端: Vue 3.3.x + TypeScript + Element Plus
- 构建工具: Maven 3.8+ / Vite 4.x
- 组件库: 企业组件库 v3.2.1

**主要业务场景覆盖**：

- 号码段入库、编辑、归档
- 列表筛选与分页
- 序列号查询、详情、回收、作废
- 作废清单导出、操作日志审计

### 架构文档查阅要求

- 如果是前端任务实现，必须先查看 `.github/rules/前端架构设计文档.md`，并按其中的页面划分、目录结构、组件分层、状态管理和 API 分层约束执行。
- 如果是前端任务实现，且涉及接口调用，必须同时查看 `docs/接口文档v2.md`，并按其中的接口路径、请求参数、返回结构和字段语义进行开发与联调。
- 如果是后端任务实现，必须先查看 `.github/rules/后端架构设计文档.md`，并按其中的模块划分、分层职责、目录结构、DTO 设计、事务与审计约束执行。
- 前后端实现过程中，不得脱离对应架构文档单独设计目录结构或分层方式；如确需调整，必须先同步更新对应架构文档。

---

## 🔀 Git提交与分支规范

- 提交格式：Conventional Commits（如 feat(query): 支持序列号模糊查询）
- Type: feat/fix/docs/style/refactor/perf/test/chore/ci/revert
- Scope: user/report/query/export/security/db/ui/api
- 分支命名：main、develop、feature/USxxx-xxx、bugfix/xxx、release/vX.X.X
- main分支受保护，必须PR+Review+测试通过
- develop建议PR合并，本地测试通过

---

### Java后端代码规范

#### 基础规范

```yaml
编码标准: 阿里巴巴Java开发手册
代码简化: 使用Lombok减少样板代码
代码检查: 
	- CheckStyle: 代码风格检查
	- SonarLint: 代码质量检查
	- PMD: 代码缺陷检查
```

#### 命名规范

```java
// ✅ 正确示例
public class ReportService {
		private static final int MAX_ROWS = 5000;
  
		private final ReportRepository reportRepository;
  
		public ReportDTO createReport(CreateReportRequest request) {
				// 方法名使用动词开头，驼峰命名
		}
  
		private boolean isValidSql(String sql) {
				// 布尔方法使用is/has/can开头
		}
}

// ❌ 错误示例
public class report_service {  // 类名应使用PascalCase
		private int max = 5000;    // 常量应使用全大写+下划线
  
		public void Report(String s) {  // 方法名应使用camelCase
				// ...
		}
}
```

#### 注解使用规范

```java
// Service层
@Service
@RequiredArgsConstructor  // Lombok生成构造器
@Slf4j                    // Lombok生成日志对象
public class ReportService {
  
		private final ReportRepository reportRepository;
  
		// 事务注解
		@Transactional(rollbackFor = Exception.class)
		public Report createReport(CreateReportRequest request) {
				// ...
		}
  
		// 缓存注解（如使用）
		@Cacheable(value = "reports", key = "#id")
		public Report getReportById(Long id) {
				// ...
		}
}

// Controller层
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Validated  // 参数校验
public class ReportController {
  
		@PostMapping
		public ResponseEntity<ReportDTO> createReport(
				@Valid @RequestBody CreateReportRequest request) {
				// @Valid触发参数校验
		}
}
```

#### 异常处理规范

```java
// ✅ 正确示例：统一异常处理
@ControllerAdvice
public class GlobalExceptionHandler {
  
		@ExceptionHandler(BusinessException.class)
		public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
				log.error("业务异常: {}", e.getMessage(), e);
				return ResponseEntity
						.status(HttpStatus.BAD_REQUEST)
						.body(new ErrorResponse(e.getCode(), e.getMessage()));
		}
  
		@ExceptionHandler(Exception.class)
		public ResponseEntity<ErrorResponse> handleException(Exception e) {
				log.error("系统异常: {}", e.getMessage(), e);
				return ResponseEntity
						.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(new ErrorResponse("SYSTEM_ERROR", "系统错误，请联系管理员"));
		}
}

// ❌ 错误示例：吞掉异常
try {
		// ...
} catch (Exception e) {
		// 不记录日志，不向上抛出
}

// ❌ 错误示例：捕获异常后仅打印堆栈
try {
		// ...
} catch (Exception e) {
		e.printStackTrace();  // 应使用日志框架
}
```

#### 日志规范

```java
// ✅ 正确示例
@Slf4j
public class ReportService {
  
		public Report createReport(CreateReportRequest request) {
				log.info("创建报表开始, 请求参数: {}", request);
  
				try {
						Report report = buildReport(request);
						reportRepository.save(report);
      
						log.info("创建报表成功, 报表ID: {}, 报表名称: {}", 
								report.getId(), report.getName());
						return report;
      
				} catch (Exception e) {
						log.error("创建报表失败, 请求参数: {}, 错误信息: {}", 
								request, e.getMessage(), e);
						throw new BusinessException("CREATE_REPORT_FAILED", "创建报表失败");
				}
		}
}
// 日志级别使用规范
// ERROR: 系统错误，需要立即处理
// WARN:  警告信息，系统可继续运行但需关注
// INFO:  关键业务节点（新增、编辑、删除、导出等）
// DEBUG: 调试信息，生产环境关闭
```

#### SQL安全规范（重要！）

```java
// ✅ 正确示例：使用参数化查询
@Repository
public class ReportQueryService {
  
		@Autowired
		private NamedParameterJdbcTemplate jdbcTemplate;
  
		public List<Map<String, Object>> executeQuery(String sql, Map<String, Object> params) {
				// 使用命名参数，防止SQL注入
				return jdbcTemplate.queryForList(sql, params);
		}
}

// ✅ 正确示例：SQL校验
public class SqlValidator {
  
		private static final Set<String> FORBIDDEN_KEYWORDS = Set.of(
				"DROP", "DELETE", "TRUNCATE", "UPDATE", "INSERT", 
				"ALTER", "CREATE", "EXEC", "EXECUTE"
		);
  
		public boolean isValidSql(String sql) {
				String upperSql = sql.trim().toUpperCase();
  
				// 只允许SELECT语句
				if (!upperSql.startsWith("SELECT")) {
						return false;
				}
  
				// 检查是否包含危险关键字
				for (String keyword : FORBIDDEN_KEYWORDS) {
						if (upperSql.contains(keyword)) {
								return false;
						}
				}
  
				return true;
		}
}

// ❌ 错误示例：字符串拼接（SQL注入风险）
public List<Map<String, Object>> executeQueryUnsafe(String sql, Map<String, Object> params) {
		// 危险！不要这样做
		for (Map.Entry<String, Object> entry : params.entrySet()) {
				sql = sql.replace(":" + entry.getKey(), String.valueOf(entry.getValue()));
		}
		return jdbcTemplate.queryForList(sql);
}
```

### 前端代码规范

#### Vue 3组合式API规范

```typescript
// ✅ 正确示例：组合式API + TypeScript
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import type { Report } from '@/types/report'

// Props定义
interface Props {
	reportId: number
	readonly?: boolean
}

const props = withDefaults(defineProps<Props>(), {
	readonly: false
})

// Emits定义
const emit = defineEmits<{
	(e: 'update', report: Report): void
	(e: 'delete', id: number): void
}>()

// 响应式状态
const report = ref<Report | null>(null)
const loading = ref(false)

// 计算属性
const isEditable = computed(() => !props.readonly && report.value?.status === 'DRAFT')

// 方法
const loadReport = async () => {
	loading.value = true
	try {
		const response = await api.getReport(props.reportId)
		report.value = response.data
	} catch (error) {
		console.error('加载报表失败:', error)
		ElMessage.error('加载报表失败')
	} finally {
		loading.value = false
	}
}

// 生命周期
onMounted(() => {
	loadReport()
})
</script>

<template>
	<div class="report-detail">
		<el-skeleton v-if="loading" :rows="5" animated />
		<div v-else-if="report" class="report-content">
			<!-- 内容 -->
		</div>
	</div>
</template>

<style scoped>
.report-detail {
	padding: 20px;
}
</style>
```

#### 命名规范

```typescript
// 组件命名：PascalCase
ReportList.vue
ReportDetail.vue
UserManagement.vue

// 文件命名：kebab-case
report-service.ts
user-api.ts
format-utils.ts

// 变量命名：camelCase
const userName = ref('')
const isLoading = ref(false)
const reportList = ref<Report[]>([])

// 常量命名：UPPER_SNAKE_CASE
const MAX_UPLOAD_SIZE = 5 * 1024 * 1024
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

// 类型命名：PascalCase
interface UserInfo {
	id: number
	name: string
}


```

#### API调用规范

```typescript
// ✅ 正确示例：统一API管理
// src/api/report.ts
import request from '@/utils/request'
import type { Report, CreateReportRequest } from '@/types/report'

export const reportApi = {
	// 获取报表列表
	getReports: (params?: { page?: number; size?: number }) => 
		request.get<Report[]>('/api/v1/reports', { params }),
  
	// 获取报表详情
	getReport: (id: number) => 
		request.get<Report>(`/api/v1/reports/${id}`),
  
	// 创建报表
	createReport: (data: CreateReportRequest) => 
		request.post<Report>('/api/v1/reports', data),
  
	// 更新报表
	updateReport: (id: number, data: Partial<Report>) => 
		request.put<Report>(`/api/v1/reports/${id}`, data),
  
	// 删除报表
	deleteReport: (id: number) => 
		request.delete(`/api/v1/reports/${id}`)
}

// 使用示例
const loadReports = async () => {
	try {
		const { data } = await reportApi.getReports({ page: 1, size: 20 })
		reports.value = data
	} catch (error) {
		console.error('加载失败:', error)
		ElMessage.error('加载报表列表失败')
	}
}
```

#### 错误处理规范

```typescript
// ✅ 正确示例：统一错误处理
import axios, { type AxiosError } from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
	baseURL: import.meta.env.VITE_API_BASE_URL,
	timeout: 10000
})

// 响应拦截器
request.interceptors.response.use(
	response => response,
	(error: AxiosError) => {
		if (error.response) {
			const { status, data } = error.response
  
			switch (status) {
				case 400:
					ElMessage.error(data?.message || '请求参数错误')
					break
				case 422:
					ElMessage.error(data?.message || '业务校验未通过')
					break
				case 404:
					ElMessage.error('请求的资源不存在')
					break
				case 500:
					ElMessage.error(data?.message || '服务器错误')
					break
				default:
					ElMessage.error(data?.message || '请求失败')
			}
		} else {
			ElMessage.error('网络错误，请检查网络连接')
		}
  
		return Promise.reject(error)
	}
)
```

## 🛡️ 质量保障

- Definition of Done：代码编写、单元测试>80%、集成测试、代码审查、验收标准、无P0/P1 Bug、API文档、日志、静态检查
- 代码审查清单：功能、异常、空指针、边界、安全、性能、可维护性、测试、文档

---

## 📦 依赖管理

- 后端：Maven dependencyManagement统一版本，禁止直接指定冲突版本
- 前端：npm主版本锁定，使用pnpm-lock.yaml或package-lock.json，禁止*号

---

## 📖 文档规范

- JavaDoc/Swagger注释，API契约清晰
- 业务/交互/UI/非功能/安全/性能/测试/验收标准文档需同步维护
- 用户故事、验收标准、SRS、原型、开发日志等文档需按业务需求实时更新

---

## 🚫 禁止事项

1. 禁止提交敏感信息（密码、密钥、真实数据、内部地址）
2. 禁止字符串拼接SQL
3. 禁止硬编码（如数据库URL等）
4. 禁止吞掉异常/仅打印堆栈
5. 禁止System.out/console.log
6. 禁止无注释/无测试/无文档提交

---

## ✅ 检查清单

- 代码命名/注释/测试/日志/SQL/异常/性能/依赖/提交/分支/文档/验收标准/开发日志/同步README
- 每个用户故事/功能点交付后，需同步更新项目状态文档和README

---

## 📊 项目管理规范

- Sprint迭代计划文档（docs/SprintN-迭代任务计划.md）只读，不可修改
- 项目状态文档（PROJECT_STATUS_vX.X.md）动态更新，Story/Task/工时/日志/进度/风险/度量
- Story完成后，必须立即补充开发日志、同步README
- 开发日志需记录真实执行过程、关键决策、工时、问题与解决方案

---

> 本规范基于 Sinotrans 空白提单管理系统业务需求与 UI/交互/非功能/安全/性能/测试/验收标准，结合阿里巴巴Java手册、Vue3最佳实践、企业组件库UI规范定制。所有开发/AI助手/代码审查/测试/交付均须严格遵循。
