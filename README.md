# Admin System - 后台管理系统技术文档

## 📋 项目概述

Admin System 是一个基于 Spring Boot 的企业级后台管理系统，提供了完整的用户权限管理、数据管理和工作流引擎功能。

### 基本信息

- **项目名称**: admin-system
- **版本**: 1.0-SNAPSHOT
- **Java 版本**: 17
- **Spring Boot 版本**: 3.5.13
- **构建工具**: Maven
- **数据库**: H2 (开发环境) / MySQL (生产环境)

---

## 🏗️ 技术架构

### 技术栈

| 类别 | 技术 | 版本/说明 |
|------|------|----------|
| **核心框架** | Spring Boot | 3.5.13 |
| **安全框架** | Spring Security | 认证授权 |
| **数据访问** | Spring Data JPA | ORM 框架 |
| **工作流引擎** | Flowable | 7.0.0 |
| **模板引擎** | Thymeleaf | 服务端渲染 |
| **数据库** | H2 / MySQL | 内存/关系型数据库 |
| **测试框架** | JUnit 5 + Selenium | 单元测试 + E2E测试 |

### 项目结构

```
admin-system/
├── src/main/java/com/example/adminsystem/
│   ├── aspect/                  # AOP切面
│   │   └── OperationLogAspect.java    # 操作日志切面
│   ├── config/                  # 配置类
│   │   ├── DataInitializer.java       # 数据初始化
│   │   ├── FlowableConfig.java        # Flowable配置
│   │   └── SecurityConfig.java        # 安全配置
│   ├── controller/              # 控制器层
│   │   ├── AnnouncementController.java
│   │   ├── ConfigController.java
│   │   ├── DepartmentController.java
│   │   ├── DictionaryController.java
│   │   ├── LoginController.java
│   │   ├── OperationLogController.java
│   │   ├── PermissionController.java
│   │   ├── RoleController.java
│   │   ├── UserController.java
│   │   └── WorkflowController.java
│   ├── entity/                  # 实体类
│   │   ├── Announcement.java
│   │   ├── Config.java
│   │   ├── Department.java
│   │   ├── Dictionary.java
│   │   ├── OperationLog.java
│   │   ├── Permission.java
│   │   ├── ProcessInstance.java
│   │   ├── Role.java
│   │   └── User.java
│   ├── repository/              # 数据访问层
│   │   ├── AnnouncementRepository.java
│   │   ├── ConfigRepository.java
│   │   ├── DepartmentRepository.java
│   │   ├── DictionaryRepository.java
│   │   ├── OperationLogRepository.java
│   │   ├── PermissionRepository.java
│   │   ├── ProcessInstanceRepository.java
│   │   ├── RoleRepository.java
│   │   └── UserRepository.java
│   ├── service/                 # 业务逻辑层
│   │   ├── impl/               # 服务实现
│   │   │   ├── OperationLogServiceImpl.java
│   │   │   ├── PermissionServiceImpl.java
│   │   │   └── RoleServiceImpl.java
│   │   ├── AnnouncementService.java
│   │   ├── ConfigService.java
│   │   ├── DepartmentService.java
│   │   ├── DictionaryService.java
│   │   ├── OperationLogService.java
│   │   ├── PermissionService.java
│   │   ├── RoleService.java
│   │   ├── UserService.java
│   │   └── WorkflowService.java
│   └── AdminSystemApplication.java  # 启动类
├── src/main/resources/
│   ├── processes/              # Flowable流程定义
│   │   ├── leave-process.bpmn20.xml
│   │   ├── leave.bpmn20.xml
│   │   └── simple-process.bpmn20.xml
│   ├── templates/              # Thymeleaf模板
│   │   ├── announcement/
│   │   ├── config/
│   │   ├── department/
│   │   ├── dictionary/
│   │   ├── operation-log/
│   │   ├── permission/
│   │   ├── role/
│   │   ├── user/
│   │   ├── workflow/
│   │   ├── dashboard.html
│   │   └── login.html
│   └── application.properties  # 应用配置
└── pom.xml                     # Maven配置
```

---

## 🎯 核心功能模块

### 1. 用户权限管理

#### 用户管理 (User)
- 用户 CRUD 操作
- 密码加密存储（BCrypt）
- 用户角色关联
- 部门归属管理

#### 角色管理 (Role)
- 角色 CRUD 操作
- 角色权限分配
- 分页查询支持

#### 权限管理 (Permission)
- 菜单权限管理
- 按钮权限管理
- 权限树形结构
- 父子权限关系

#### 部门管理 (Department)
- 部门 CRUD 操作
- 组织架构管理

### 2. 系统管理

#### 公告管理 (Announcement)
- 公告发布与管理
- 公告类型分类
- 公告状态控制（草稿/发布/归档）
- 自动记录创建和更新时间

#### 配置管理 (Config)
- 系统参数配置
- 键值对存储
- 配置项动态获取

#### 字典管理 (Dictionary)
- 数据字典维护
- 字典类型分类
- 字典项状态管理

#### 操作日志 (OperationLog)
- 自动记录用户操作
- AOP 切面实现
- 分页查询日志
- 按时间倒序排列

### 3. 工作流引擎 (Flowable)

#### 流程管理
- 流程定义部署
- 流程实例启动
- 任务查询与完成
- 流程状态跟踪

#### 内置流程
- 请假流程（leave-process）
- 简单流程示例（simple-process）

---

## 🔐 安全机制

### Spring Security 配置

1. **认证方式**: 表单登录
2. **密码加密**: BCryptPasswordEncoder
3. **会话管理**: 基于 Session
4. **CSRF 保护**: 已启用
5. **URL 权限控制**: 基于角色的访问控制

### 权限控制流程

```
用户请求 → Security Filter Chain → 身份验证 → 授权检查 → 控制器处理
```

---

## 💾 数据管理

### 事务管理策略

项目采用声明式事务管理，所有 Service 层方法都添加了事务控制：

#### 只读事务（查询操作）
```java
@Transactional(readOnly = true)
public List<User> findAll() {
    return userRepository.findAll();
}
```

**优势：**
- 数据库性能优化
- 减少锁竞争
- 提高并发读取能力

#### 读写事务（修改操作）
```java
@Transactional(rollbackFor = Exception.class)
public User save(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
}
```

**特性：**
- 异常自动回滚
- 保证数据一致性
- 支持多表操作原子性

### 数据库配置

#### 开发环境（H2）
```properties
spring.datasource.url=jdbc:h2:mem:admin_system
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

#### 生产环境（MySQL）
需要修改 `application.properties`：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/admin_system
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

---

## 🧪 测试体系

### 单元测试
- UserServiceTest
- RoleServiceTest
- TestH2 (数据库测试)

### E2E 测试
- E2ETest (基础端到端测试)
- SeleniumE2ETest (Selenium UI 自动化测试)

### 运行测试
```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=UserServiceTest

# 跳过测试打包
mvn package -DskipTests
```

---

## 🚀 部署指南

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+ (生产环境)

### 本地开发

1. **克隆项目**
```bash
git clone <repository-url>
cd admin-system
```

2. **编译项目**
```bash
mvn clean compile
```

3. **运行应用**
```bash
mvn spring-boot:run
```

4. **访问应用**
- 应用地址: http://localhost:8080
- H2 控制台: http://localhost:8080/h2-console

### 生产部署

1. **打包应用**
```bash
mvn clean package -DskipTests
```

2. **运行 JAR 包**
```bash
java -jar target/admin-system-1.0-SNAPSHOT.jar
```

3. **自定义配置**
```bash
java -jar target/admin-system-1.0-SNAPSHOT.jar \
  --server.port=8080 \
  --spring.datasource.url=jdbc:mysql://localhost:3306/admin_system
```

---

## 📝 API 接口文档

### 用户管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 查询所有用户 | GET | /users | 获取用户列表 |
| 查询用户详情 | GET | /users/{id} | 根据ID获取用户 |
| 创建用户 | POST | /users | 新增用户 |
| 更新用户 | PUT | /users/{id} | 修改用户信息 |
| 删除用户 | DELETE | /users/{id} | 删除用户 |

### 角色管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 查询所有角色 | GET | /roles | 获取角色列表 |
| 查询角色详情 | GET | /roles/{id} | 根据ID获取角色 |
| 创建角色 | POST | /roles | 新增角色 |
| 更新角色 | PUT | /roles/{id} | 修改角色信息 |
| 删除角色 | DELETE | /roles/{id} | 删除角色 |

### 权限管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 查询所有权限 | GET | /permissions | 获取权限列表 |
| 创建权限 | POST | /permissions | 新增权限 |
| 更新权限 | PUT | /permissions/{id} | 修改权限 |
| 删除权限 | DELETE | /permissions/{id} | 删除权限 |

### 工作流管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 部署流程 | POST | /workflow/deploy | 上传并部署流程定义 |
| 查询流程定义 | GET | /workflow/definitions | 获取所有流程定义 |
| 启动流程 | POST | /workflow/start | 启动流程实例 |
| 查询任务 | GET | /workflow/tasks | 获取用户待办任务 |
| 完成任务 | POST | /workflow/complete/{taskId} | 完成任务 |

---

## 🔧 配置说明

### application.properties 主要配置

```properties
# 服务器配置
server.port=8080

# 数据库配置
spring.datasource.url=jdbc:h2:mem:admin_system
spring.datasource.username=sa
spring.datasource.password=

# JPA 配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# H2 控制台
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Thymeleaf 配置
spring.thymeleaf.cache=false
spring.thymeleaf.enabled=true
```

### 关键配置项说明

| 配置项 | 说明 | 推荐值 |
|--------|------|--------|
| `spring.jpa.hibernate.ddl-auto` | 数据库 schema 策略 | update/dev, validate/prod |
| `spring.jpa.show-sql` | 是否显示 SQL | true/dev, false/prod |
| `spring.thymeleaf.cache` | 模板缓存 | false/dev, true/prod |
| `spring.h2.console.enabled` | H2 控制台 | true/dev, false/prod |

---

## 📊 数据库设计

### 核心表结构

#### users (用户表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR | 用户名（唯一） |
| password | VARCHAR | 密码（BCrypt加密） |
| department_id | BIGINT | 部门ID（外键） |

#### roles (角色表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR | 角色名（唯一） |
| description | VARCHAR | 角色描述 |

#### permissions (权限表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR | 权限名称 |
| code | VARCHAR | 权限代码 |
| type | VARCHAR | 权限类型（MENU/BUTTON） |
| parent_id | BIGINT | 父权限ID |
| url | VARCHAR | 菜单URL |

#### user_roles (用户角色关联表)
| 字段 | 类型 | 说明 |
|------|------|------|
| user_id | BIGINT | 用户ID（外键） |
| role_id | BIGINT | 角色ID（外键） |

#### departments (部门表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR | 部门名称 |

#### announcements (公告表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| title | VARCHAR | 公告标题 |
| content | TEXT | 公告内容 |
| type | VARCHAR | 公告类型 |
| status | VARCHAR | 状态 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

#### operation_logs (操作日志表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR | 操作用户 |
| operation | VARCHAR | 操作描述 |
| method | VARCHAR | 请求方法 |
| params | TEXT | 请求参数 |
| ip | VARCHAR | IP地址 |
| create_time | TIMESTAMP | 操作时间 |

---

## 🎨 前端页面

### 页面结构

所有页面使用 Thymeleaf 模板引擎，位于 `src/main/resources/templates/` 目录。

### 主要页面

- **login.html** - 登录页面
- **dashboard.html** - 仪表板
- **user/list.html** - 用户列表
- **user/form.html** - 用户表单
- **role/list.html** - 角色列表
- **role/form.html** - 角色表单
- **role/permission.html** - 角色权限分配
- **permission/list.html** - 权限列表
- **department/list.html** - 部门列表
- **announcement/list.html** - 公告列表
- **config/list.html** - 配置列表
- **dictionary/list.html** - 字典列表
- **operation-log/list.html** - 操作日志列表
- **workflow/** - 工作流相关页面

---

## 🔍 常见问题

### 1. 如何修改默认端口？

修改 `application.properties`：
```properties
server.port=9090
```

或在启动时指定：
```bash
java -jar admin-system.jar --server.port=9090
```

### 2. 如何切换到 MySQL 数据库？

1. 修改 `pom.xml`，确保 MySQL 驱动未注释
2. 修改 `application.properties`：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/admin_system
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### 3. 如何初始化默认数据？

项目包含 `DataInitializer` 配置类，会在启动时自动创建：
- 默认管理员账号
- 基础角色（ADMIN, USER）
- 示例部门

### 4. 如何添加新的工作流？

1. 在 `src/main/resources/processes/` 目录下创建 BPMN 文件
2. 重启应用，Flowable 会自动部署新流程
3. 通过 `/workflow/deploy` 接口手动部署

### 5. 操作日志如何记录？

通过 `OperationLogAspect` AOP 切面自动记录：
- 拦截所有 Controller 方法
- 记录用户、操作、时间、IP 等信息
- 异步写入数据库

---

## 📈 性能优化建议

### 1. 数据库优化
- 生产环境使用连接池（HikariCP 已默认集成）
- 为常用查询字段添加索引
- 定期清理操作日志

### 2. 缓存策略
- 考虑引入 Redis 缓存热点数据
- 启用 Thymeleaf 模板缓存（生产环境）
- 使用 Spring Cache 注解

### 3. 事务优化
- 合理设置事务隔离级别
- 避免长事务
- 只读事务使用 `readOnly = true`

### 4. 前端优化
- 启用静态资源压缩
- 使用 CDN 加速
- 实施懒加载

---

## 🔒 安全建议

### 生产环境安全检查清单

- [ ] 修改默认管理员密码
- [ ] 禁用 H2 控制台 (`spring.h2.console.enabled=false`)
- [ ] 关闭 SQL 日志 (`spring.jpa.show-sql=false`)
- [ ] 启用 HTTPS
- [ ] 配置 CORS 策略
- [ ] 设置会话超时时间
- [ ] 启用 CSRF 保护
- [ ] 定期更新依赖版本
- [ ] 配置防火墙规则
- [ ] 备份数据库

---

## 📚 参考资料

- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [Spring Security 指南](https://spring.io/projects/spring-security)
- [Flowable 用户手册](https://www.flowable.com/open-source/docs)
- [Thymeleaf 教程](https://www.thymeleaf.org/documentation.html)
- [JPA/Hibernate 文档](https://hibernate.org/orm/documentation/)

---

## 📞 技术支持

如有问题或建议，请通过以下方式联系：

- 提交 Issue
- 发送邮件至开发团队
- 查看项目 Wiki

---

## 📄 许可证

本项目仅供学习和内部使用。

---

**文档版本**: 1.0  
**最后更新**: 2026-05-05  
**维护者**: Admin System 开发团队
