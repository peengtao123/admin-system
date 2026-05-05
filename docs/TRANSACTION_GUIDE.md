# 事务管理指南

## 📖 概述

本文档详细介绍 Admin System 项目中的事务管理机制，包括事务的使用原则、最佳实践和常见问题。

---

## 🎯 事务管理策略

### 核心原则

本项目采用 **声明式事务管理**，通过 `@Transactional` 注解实现：

1. **所有 Service 层方法都必须有事务控制**
2. **读操作使用只读事务** (`readOnly = true`)
3. **写操作使用读写事务** (`rollbackFor = Exception.class`)
4. **事务边界在 Service 层，不在 Controller 或 Repository 层**

---

## 🔧 事务注解详解

### 1. 只读事务 - `@Transactional(readOnly = true)`

#### 适用场景
- 所有查询方法（find、get、list、count 等）
- 报表统计
- 数据导出
- 任何不修改数据库的操作

#### 示例
```java
@Service
public class UserService {
    
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsernameWithRoles(username);
    }
    
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAllWithRoles();
    }
    
    @Transactional(readOnly = true)
    public Page<User> findByPage(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
```

#### 优势
✅ **性能优化**：数据库可以针对只读操作进行优化  
✅ **减少锁竞争**：不需要获取排他锁，提高并发能力  
✅ **资源节约**：跳过不必要的回滚日志准备  
✅ **语义清晰**：明确标识这是查询操作  

#### 注意事项
⚠️ 不要在此类方法中执行写操作  
⚠️ 某些数据库会拒绝写操作并抛出异常  
⚠️ H2 数据库可能允许但不推荐  

---

### 2. 读写事务 - `@Transactional(rollbackFor = Exception.class)`

#### 适用场景
- 所有修改操作（save、update、delete）
- 多表联合操作
- 需要保证原子性的业务逻辑
- 工作流操作

#### 示例
```java
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional(rollbackFor = Exception.class)
    public User save(User user) {
        // 密码加密
        if (user != null && user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        // 保存用户
        return userRepository.save(user);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        if (id != null) {
            userRepository.deleteById(id);
        }
    }
}
```

#### 特性
✅ **自动回滚**：遇到异常自动回滚事务  
✅ **数据一致性**：保证多步操作的原子性  
✅ **异常控制**：指定哪些异常触发回滚  

#### 为什么使用 `rollbackFor = Exception.class`？

默认情况下，Spring 只对 **运行时异常**（RuntimeException）和 **Error** 进行回滚：

```java
// ❌ 默认行为 - 只回滚 RuntimeException
@Transactional
public void save(User user) {
    userRepository.save(user);
    // 如果抛出 checked exception，不会回滚！
}

// ✅ 推荐做法 - 所有异常都回滚
@Transactional(rollbackFor = Exception.class)
public void save(User user) {
    userRepository.save(user);
    // 任何异常都会回滚
}
```

---

## 📋 已添加事务的 Service 列表

### 1. UserService
| 方法 | 事务类型 | 说明 |
|------|---------|------|
| `findByUsername()` | readOnly | 根据用户名查询 |
| `findAll()` | readOnly | 查询所有用户 |
| `findById()` | readOnly | 根据ID查询 |
| `save()` | rollbackFor | 保存/更新用户 |
| `deleteById()` | rollbackFor | 删除用户 |

### 2. RoleServiceImpl
| 方法 | 事务类型 | 说明 |
|------|---------|------|
| `findAll()` | readOnly | 分页查询角色 |
| `findById()` | readOnly | 根据ID查询 |
| `findByName()` | readOnly | 根据名称查询 |
| `findAll()` | readOnly | 查询所有角色 |
| `findAllById()` | readOnly | 批量查询 |
| `save()` | rollbackFor | 保存/更新角色 |
| `deleteById()` | rollbackFor | 删除角色 |

### 3. PermissionServiceImpl
| 方法 | 事务类型 | 说明 |
|------|---------|------|
| `findAll()` | readOnly | 分页查询权限 |
| `findById()` | readOnly | 根据ID查询 |
| `findByType()` | readOnly | 按类型查询 |
| `findByParentId()` | readOnly | 查询子权限 |
| `findAll()` | readOnly | 查询所有权限 |
| `findAllById()` | readOnly | 批量查询 |
| `save()` | rollbackFor | 保存/更新权限 |
| `deleteById()` | rollbackFor | 删除权限 |

### 4. OperationLogServiceImpl
| 方法 | 事务类型 | 说明 |
|------|---------|------|
| `findAll()` | readOnly | 分页查询日志 |
| `save()` | rollbackFor | 记录操作日志 |

### 5. AnnouncementService
| 方法 | 事务类型 | 说明 |
|------|---------|------|
| `findAll()` | readOnly | 查询所有公告 |
| `findById()` | readOnly | 根据ID查询 |
| `findByStatus()` | readOnly | 按状态查询 |
| `findByType()` | readOnly | 按类型查询 |
| `save()` | rollbackFor | 保存/更新公告 |
| `deleteById()` | rollbackFor | 删除公告 |

### 6. ConfigService
| 方法 | 事务类型 | 说明 |
|------|---------|------|
| `findAll()` | readOnly | 查询所有配置 |
| `findById()` | readOnly | 根据ID查询 |
| `findByConfigKey()` | readOnly | 根据键查询 |
| `getConfigValue()` | readOnly | 获取配置值 |
| `save()` | rollbackFor | 保存/更新配置 |
| `deleteById()` | rollbackFor | 删除配置 |

### 7. DepartmentService
| 方法 | 事务类型 | 说明 |
|------|---------|------|
| `findAll()` | readOnly | 查询所有部门 |
| `findById()` | readOnly | 根据ID查询 |
| `save()` | rollbackFor | 保存/更新部门 |
| `deleteById()` | rollbackFor | 删除部门 |

### 8. DictionaryService
| 方法 | 事务类型 | 说明 |
|------|---------|------|
| `findAll()` | readOnly | 查询所有字典 |
| `findById()` | readOnly | 根据ID查询 |
| `findByType()` | readOnly | 按类型查询 |
| `findByTypeAndStatus()` | readOnly | 条件查询 |
| `save()` | rollbackFor | 保存/更新字典 |
| `deleteById()` | rollbackFor | 删除字典 |

### 9. WorkflowService
| 方法 | 事务类型 | 说明 |
|------|---------|------|
| `getProcessDefinitions()` | readOnly | 查询流程定义 |
| `getTasks()` | readOnly | 查询用户任务 |
| `getTaskById()` | readOnly | 根据ID查询任务 |
| `getProcessInstances()` | readOnly | 查询流程实例 |
| `getRunningProcessInstances()` | readOnly | 查询运行中实例 |
| `getCompletedProcessInstances()` | readOnly | 查询已完成实例 |
| `getProcessInstancesByUser()` | readOnly | 按用户查询实例 |
| `deployProcess()` | rollbackFor | 部署流程 |
| `startProcess()` | rollbackFor | 启动流程实例 |
| `completeTask()` | rollbackFor | 完成任务 |

---

## 🔄 事务传播行为

### 默认传播行为：REQUIRED

Spring 默认使用 `Propagation.REQUIRED`：

```java
@Transactional(readOnly = true)
public void methodA() {
    // 如果已有事务，加入该事务
    // 如果没有事务，创建新事务
    methodB();
}

@Transactional(readOnly = true)
public void methodB() {
    // 会加入 methodA 的事务
}
```

### 其他传播行为

| 传播行为 | 说明 | 使用场景 |
|---------|------|---------|
| REQUIRED | 默认，加入现有事务或创建新事务 | 大多数场景 |
| REQUIRES_NEW | 总是创建新事务，挂起现有事务 | 日志记录等独立操作 |
| NESTED | 嵌套事务，父事务回滚子事务也回滚 | 复杂业务逻辑 |
| SUPPORTS | 有事务就加入，没有就以非事务方式执行 | 可选事务 |
| NOT_SUPPORTED | 以非事务方式执行，挂起现有事务 | 不需要事务的操作 |
| MANDATORY | 必须在事务中执行，否则抛异常 | 强制要求事务 |
| NEVER | 必须在非事务中执行，否则抛异常 | 禁止事务 |

---

## ⚙️ 事务隔离级别

### 默认隔离级别：数据库默认

```java
@Transactional(isolation = Isolation.DEFAULT)
```

### 常见隔离级别

| 隔离级别 | 脏读 | 不可重复读 | 幻读 | 性能 |
|---------|------|-----------|------|------|
| READ_UNCOMMITTED | ✅ | ✅ | ✅ | 最高 |
| READ_COMMITTED | ❌ | ✅ | ✅ | 高 |
| REPEATABLE_READ | ❌ | ❌ | ✅ | 中 |
| SERIALIZABLE | ❌ | ❌ | ❌ | 最低 |

### 设置隔离级别示例

```java
@Transactional(
    readOnly = true, 
    isolation = Isolation.READ_COMMITTED
)
public List<User> findAll() {
    return userRepository.findAll();
}
```

---

## 🚨 常见陷阱与解决方案

### 1. 同类方法调用事务失效

❌ **错误示例**
```java
@Service
public class UserService {
    
    public void createUser(User user) {
        // 直接调用，事务不会生效！
        saveUser(user);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
```

✅ **正确做法**
```java
@Service
public class UserService {
    
    @Autowired
    private UserService self; // 注入自己
    
    public void createUser(User user) {
        // 通过代理调用，事务生效
        self.saveUser(user);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
```

或者拆分到不同的 Service 类。

---

### 2. 异常被捕获导致不回滚

❌ **错误示例**
```java
@Transactional(rollbackFor = Exception.class)
public void saveUser(User user) {
    try {
        userRepository.save(user);
        // 发生异常
        int i = 1 / 0;
    } catch (Exception e) {
        // 捕获异常但不抛出，事务不会回滚！
        log.error("Error", e);
    }
}
```

✅ **正确做法**
```java
@Transactional(rollbackFor = Exception.class)
public void saveUser(User user) {
    try {
        userRepository.save(user);
        int i = 1 / 0;
    } catch (Exception e) {
        log.error("Error", e);
        // 手动标记回滚
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        // 或者重新抛出异常
        throw e;
    }
}
```

---

### 3. 私有方法事务无效

❌ **错误示例**
```java
@Transactional(rollbackFor = Exception.class)
private void saveUser(User user) {
    userRepository.save(user);
}
```

✅ **正确做法**
```java
@Transactional(rollbackFor = Exception.class)
public void saveUser(User user) {
    userRepository.save(user);
}
```

**原因**：Spring AOP 基于代理，只能拦截 public 方法。

---

### 4. 数据库引擎不支持事务

确保使用支持事务的存储引擎：

- MySQL: 使用 InnoDB（默认）
- PostgreSQL: 原生支持
- H2: 原生支持

检查 MySQL 表引擎：
```sql
SHOW TABLE STATUS WHERE Name = 'users';
```

---

## 📊 事务监控与调试

### 1. 启用 SQL 日志

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### 2. 查看事务状态

```java
@Transactional(rollbackFor = Exception.class)
public void saveUser(User user) {
    TransactionStatus status = TransactionAspectSupport.currentTransactionStatus();
    log.info("事务是否只读: {}", status.isReadOnly());
    log.info("事务是否已完成: {}", status.isCompleted());
    
    userRepository.save(user);
}
```

### 3. 使用 Spring Boot Actuator

添加依赖：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

访问端点：
- `/actuator/metrics` - 查看事务相关指标
- `/actuator/health` - 健康检查

---

## 🎓 最佳实践总结

### ✅ DO - 应该做的

1. **所有 Service 方法都添加事务注解**
2. **查询方法使用 `readOnly = true`**
3. **修改方法使用 `rollbackFor = Exception.class`**
4. **保持事务简短，避免长事务**
5. **在 Service 层控制事务边界**
6. **合理设置隔离级别**
7. **记录事务相关的日志**

### ❌ DON'T - 不应该做的

1. **不要在 Controller 层添加事务**
2. **不要在 Repository 层添加事务**
3. **不要捕获异常后不处理**
4. **不要在事务中进行远程调用**
5. **不要在循环中开启事务**
6. **不要忽略事务传播行为**
7. **不要在只读事务中执行写操作**

---

## 🔍 代码审查清单

在提交代码前，检查以下事项：

- [ ] 所有新的 Service 方法都添加了 `@Transactional`
- [ ] 查询方法使用了 `readOnly = true`
- [ ] 修改方法使用了 `rollbackFor = Exception.class`
- [ ] 没有在 Controller 层添加事务
- [ ] 没有在私有方法上添加事务
- [ ] 异常处理不会导致事务失效
- [ ] 事务方法没有被同类直接调用
- [ ] 测试覆盖了事务场景

---

## 📚 参考资料

- [Spring 事务管理官方文档](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#transaction)
- [Spring @Transactional 详解](https://spring.io/guides/gs/managing-transactions/)
- [数据库事务隔离级别](https://en.wikipedia.org/wiki/Isolation_(database_systems))

---

**文档版本**: 1.0  
**最后更新**: 2026-05-05  
**维护者**: Admin System 开发团队
