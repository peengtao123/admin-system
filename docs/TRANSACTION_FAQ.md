# Spring 事务管理 FAQ - 常见问题解答

## 📋 目录

- [基础概念](#基础概念)
- [使用方法](#使用方法)
- [常见问题](#常见问题)
- [最佳实践](#最佳实践)
- [故障排查](#故障排查)

---

## 🎯 基础概念

### Q1: 什么是事务？为什么需要事务？

**A:** 事务是数据库操作的最小工作单位，它保证一组操作要么全部成功，要么全部失败。

**四大特性（ACID）：**
- **原子性（Atomicity）**: 所有操作要么全部完成，要么全部不完成
- **一致性（Consistency）**: 事务执行前后，数据保持一致状态
- **隔离性（Isolation）**: 多个事务互不干扰
- **持久性（Durability）**: 事务完成后，数据永久保存

**示例场景：**
```java
// 转账操作：必须保证两个操作同时成功或同时失败
@Transactional(rollbackFor = Exception.class)
public void transferMoney(Long fromId, Long toId, BigDecimal amount) {
    // 扣款
    accountRepository.deduct(fromId, amount);
    // 入账
    accountRepository.add(toId, amount);
    // 如果任何一步失败，两步都会回滚
}
```

---

### Q2: Spring 事务管理的两种方式是什么？

**A:** Spring 提供两种事务管理方式：

#### 1. 声明式事务（推荐）✅
使用 `@Transactional` 注解，通过 AOP 实现。

```java
@Service
public class UserService {
    @Transactional(rollbackFor = Exception.class)
    public User save(User user) {
        return userRepository.save(user);
    }
}
```

**优点：**
- ✅ 代码简洁，业务逻辑不受污染
- ✅ 易于维护和修改
- ✅ 推荐的生产环境使用方式

#### 2. 编程式事务
使用 `TransactionTemplate` 或 `PlatformTransactionManager`。

```java
@Autowired
private TransactionTemplate transactionTemplate;

public User save(User user) {
    return transactionTemplate.execute(status -> {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            status.setRollbackOnly();
            throw e;
        }
    });
}
```

**优点：**
- ✅ 更细粒度的控制
- ✅ 适合复杂的事务逻辑

**缺点：**
- ❌ 代码侵入性强
- ❌ 维护成本高

---

### Q3: `@Transactional` 可以加在哪些地方？

**A:** 可以加在以下位置：

| 位置 | 是否有效 | 说明 |
|------|---------|------|
| **Service 类** | ✅ 推荐 | 作用于所有 public 方法 |
| **Service 方法** | ✅ 推荐 | 作用于单个方法 |
| **Controller** | ❌ 不推荐 | 违反分层原则 |
| **Repository** | ❌ 不需要 | JPA 已有事务支持 |
| **私有方法** | ❌ 无效 | AOP 无法拦截 |
| **静态方法** | ❌ 无效 | AOP 无法拦截 |

**推荐做法：**
```java
@Service
public class UserService {
    
    // ✅ 推荐：加在具体方法上
    @Transactional(rollbackFor = Exception.class)
    public User save(User user) {
        return userRepository.save(user);
    }
    
    // ✅ 也可以：加在类上（所有 public 方法都有事务）
    // 但建议明确指定每个方法的事务类型
}
```

---

## 🔧 使用方法

### Q4: `@Transactional` 有哪些常用属性？

**A:** 主要属性如下：

```java
@Transactional(
    readOnly = false,              // 是否只读事务
    rollbackFor = Exception.class, // 哪些异常触发回滚
    noRollbackFor = {},            // 哪些异常不回滚
    isolation = Isolation.DEFAULT, // 隔离级别
    propagation = Propagation.REQUIRED, // 传播行为
    timeout = 30                   // 超时时间（秒）
)
```

#### 1. readOnly - 只读事务

```java
// 查询操作使用只读事务
@Transactional(readOnly = true)
public List<User> findAll() {
    return userRepository.findAll();
}
```

**作用：**
- 数据库优化（减少锁竞争）
- 提高并发性能
- 语义清晰

#### 2. rollbackFor - 回滚规则

```java
// 所有异常都回滚（推荐）
@Transactional(rollbackFor = Exception.class)
public void save(User user) {
    userRepository.save(user);
}

// 默认只对 RuntimeException 回滚（不推荐）
@Transactional
public void save(User user) {
    userRepository.save(user);
}
```

**区别：**
```java
// ❌ 默认行为：checked exception 不会回滚
@Transactional
public void method() throws IOException {
    userRepository.save(user);
    throw new IOException("IO错误"); // 不会回滚！
}

// ✅ 推荐：所有异常都回滚
@Transactional(rollbackFor = Exception.class)
public void method() throws IOException {
    userRepository.save(user);
    throw new IOException("IO错误"); // 会回滚
}
```

#### 3. isolation - 隔离级别

```java
@Transactional(isolation = Isolation.READ_COMMITTED)
public List<User> findAll() {
    return userRepository.findAll();
}
```

**可选值：**
- `DEFAULT`: 使用数据库默认（推荐）
- `READ_UNCOMMITTED`: 最低隔离，可能脏读
- `READ_COMMITTED`: 避免脏读
- `REPEATABLE_READ`: 避免不可重复读
- `SERIALIZABLE`: 最高隔离，性能最差

#### 4. propagation - 传播行为

```java
@Transactional(propagation = Propagation.REQUIRED)
public void methodA() {
    methodB(); // 加入同一事务
}

@Transactional(propagation = Propagation.REQUIRES_NEW)
public void methodB() {
    // 创建新事务，挂起现有事务
}
```

**常用传播行为：**
- `REQUIRED`: 默认，加入现有事务或创建新事务
- `REQUIRES_NEW`: 总是创建新事务
- `NESTED`: 嵌套事务
- `SUPPORTS`: 有事务就加入，没有就以非事务执行

---

### Q5: 如何正确使用只读事务和读写事务？

**A:** 根据操作类型选择：

#### 只读事务（查询操作）

```java
@Service
public class UserService {
    
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Page<User> findByPage(Pageable page) {
        return userRepository.findAll(page);
    }
}
```

**适用场景：**
- ✅ 所有查询方法（find、get、list、count）
- ✅ 报表统计
- ✅ 数据导出

#### 读写事务（修改操作）

```java
@Service
public class UserService {
    
    @Transactional(rollbackFor = Exception.class)
    public User save(User user) {
        return userRepository.save(user);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public User update(Long id, User user) {
        User existing = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        existing.setUsername(user.getUsername());
        return userRepository.save(existing);
    }
}
```

**适用场景：**
- ✅ 所有修改操作（save、update、delete）
- ✅ 多表联合操作
- ✅ 需要保证原子性的业务

---

### Q6: 如何在 Service 中调用其他 Service 的事务方法？

**A:** 直接注入并调用即可，事务会自动传播。

```java
@Service
public class OrderService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;
    
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Order order) {
        // 1. 验证用户（只读事务）
        User user = userService.findById(order.getUserId());
        
        // 2. 扣减库存（读写事务）
        productService.reduceStock(order.getProductId(), order.getQuantity());
        
        // 3. 创建订单（读写事务）
        return orderRepository.save(order);
        // 任何一步失败，所有操作都会回滚
    }
}
```

**事务传播：**
- 默认 `REQUIRED`：所有方法在同一个事务中
- 如果某个方法抛出异常，整个事务回滚

---

## ⚠️ 常见问题

### Q7: 为什么我的 `@Transactional` 不生效？

**A:** 这是最常见的问题，可能有以下原因：

#### 原因 1: 同类方法调用（最常见）❌

```java
@Service
public class UserService {
    
    public void createUser(User user) {
        // ❌ 直接调用，事务不会生效！
        saveUser(user);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
```

**解决方案：**

**方案 1: 注入自己**
```java
@Service
public class UserService {
    
    @Autowired
    private UserService self; // 注入代理对象
    
    public void createUser(User user) {
        // ✅ 通过代理调用，事务生效
        self.saveUser(user);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
```

**方案 2: 拆分到不同 Service**
```java
@Service
public class UserService {
    
    @Autowired
    private UserWriterService userWriterService;
    
    public void createUser(User user) {
        // ✅ 调用其他 Service，事务生效
        userWriterService.saveUser(user);
    }
}

@Service
public class UserWriterService {
    
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
```

**方案 3: 使用 AopContext（不推荐）**
```java
public void createUser(User user) {
    ((UserService) AopContext.currentProxy()).saveUser(user);
}
```

---

#### 原因 2: 方法是 private 或 final ❌

```java
@Service
public class UserService {
    
    // ❌ private 方法，事务无效
    @Transactional(rollbackFor = Exception.class)
    private void saveUser(User user) {
        userRepository.save(user);
    }
    
    // ❌ final 方法，事务无效
    @Transactional(rollbackFor = Exception.class)
    public final void updateUser(User user) {
        userRepository.save(user);
    }
}
```

**解决方案：**
```java
@Service
public class UserService {
    
    // ✅ 改为 public
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(User user) {
        userRepository.save(user);
    }
    
    // ✅ 去掉 final
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(User user) {
        userRepository.save(user);
    }
}
```

---

#### 原因 3: 异常被捕获但没有抛出 ❌

```java
@Service
public class UserService {
    
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(User user) {
        try {
            userRepository.save(user);
            int i = 1 / 0; // 抛出异常
        } catch (Exception e) {
            // ❌ 捕获异常但不抛出，事务不会回滚！
            log.error("Error", e);
        }
    }
}
```

**解决方案：**

**方案 1: 重新抛出异常**
```java
@Transactional(rollbackFor = Exception.class)
public void saveUser(User user) {
    try {
        userRepository.save(user);
        int i = 1 / 0;
    } catch (Exception e) {
        log.error("Error", e);
        throw e; // ✅ 重新抛出
    }
}
```

**方案 2: 手动标记回滚**
```java
@Transactional(rollbackFor = Exception.class)
public void saveUser(User user) {
    try {
        userRepository.save(user);
        int i = 1 / 0;
    } catch (Exception e) {
        log.error("Error", e);
        // ✅ 手动标记回滚
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
}
```

---

#### 原因 4: 数据库引擎不支持事务 ❌

```sql
-- MySQL MyISAM 引擎不支持事务
SHOW TABLE STATUS WHERE Name = 'users';
-- 如果 Engine 是 MyISAM，需要改为 InnoDB

ALTER TABLE users ENGINE=InnoDB;
```

**解决方案：**
- 确保使用 InnoDB 引擎（MySQL 8.0 默认）
- PostgreSQL、H2 等原生支持事务

---

#### 原因 5: 没有启用事务管理 ❌

```java
@SpringBootApplication
@EnableTransactionManagement // ✅ 确保启用（Spring Boot 默认已启用）
public class AdminSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminSystemApplication.class, args);
    }
}
```

**注意：** Spring Boot 自动配置已启用事务管理，通常不需要手动添加。

---

### Q8: 只读事务中可以执行写操作吗？

**A:** 技术上可能允许，但强烈不推荐！❌

```java
@Transactional(readOnly = true)
public void badExample(User user) {
    // ❌ 不要这样做！
    userRepository.save(user);
}
```

**可能的结果：**
- H2 数据库：可能允许但有警告
- MySQL：通常允许但不推荐
- PostgreSQL：可能会抛出异常
- Oracle：可能会抛出异常

**正确做法：**
```java
// 查询用只读事务
@Transactional(readOnly = true)
public User findById(Long id) {
    return userRepository.findById(id).orElse(null);
}

// 修改用读写事务
@Transactional(rollbackFor = Exception.class)
public User save(User user) {
    return userRepository.save(user);
}
```

---

### Q9: 事务的超时时间如何设置？

**A:** 使用 `timeout` 属性设置超时时间（秒）。

```java
// 设置 30 秒超时
@Transactional(rollbackFor = Exception.class, timeout = 30)
public void longRunningOperation() {
    // 如果执行超过 30 秒，事务会自动回滚
    userRepository.save(user);
    // ... 耗时操作
}
```

**适用场景：**
- 防止长事务占用数据库资源
- 避免死锁导致的无限等待
- 大批量数据处理

**注意事项：**
- 超时后会抛出 `TransactionTimedOutException`
- 超时时间包括所有数据库操作
- 默认无超时限制

---

### Q10: 如何处理嵌套事务？

**A:** 使用不同的传播行为。

#### 场景 1: 子方法加入父事务（默认）

```java
@Transactional(rollbackFor = Exception.class)
public void parentMethod() {
    childMethod(); // 加入同一事务
    // 如果 childMethod 抛出异常，整个事务回滚
}

@Transactional(rollbackFor = Exception.class)
public void childMethod() {
    userRepository.save(user);
}
```

#### 场景 2: 子方法独立事务

```java
@Transactional(rollbackFor = Exception.class)
public void parentMethod() {
    try {
        childMethod(); // 独立事务
    } catch (Exception e) {
        // 子事务失败不影响父事务
        log.error("Child failed", e);
    }
    // 父事务继续执行
}

@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
public void childMethod() {
    // 创建新事务，挂起父事务
    userRepository.save(user);
}
```

**传播行为对比：**

| 传播行为 | 父事务存在时 | 父事务回滚时 |
|---------|------------|------------|
| REQUIRED | 加入父事务 | 一起回滚 |
| REQUIRES_NEW | 创建新事务 | 不受影响 |
| NESTED | 嵌套事务 | 一起回滚 |

---

### Q11: 批量操作如何使用事务？

**A:** 将整个批量操作放在一个事务中。

#### 方式 1: 简单批量

```java
@Transactional(rollbackFor = Exception.class)
public void batchSave(List<User> users) {
    for (User user : users) {
        userRepository.save(user);
    }
    // 任何一个失败，全部回滚
}
```

**问题：** 数据量大时可能导致内存溢出或超时。

#### 方式 2: 分批提交（推荐）✅

```java
@Autowired
private PlatformTransactionManager transactionManager;

public void batchSave(List<User> users) {
    int batchSize = 100;
    
    for (int i = 0; i < users.size(); i += batchSize) {
        // 每批创建一个新事务
        TransactionStatus status = transactionManager.getTransaction(
            new DefaultTransactionDefinition()
        );
        
        try {
            List<User> batch = users.subList(i, Math.min(i + batchSize, users.size()));
            for (User user : batch) {
                userRepository.save(user);
            }
            transactionManager.commit(status); // 提交当前批次
        } catch (Exception e) {
            transactionManager.rollback(status); // 回滚当前批次
            throw e;
        }
    }
}
```

**优势：**
- ✅ 避免大事务
- ✅ 减少内存占用
- ✅ 提高性能
- ✅ 失败只影响当前批次

---

### Q12: 如何在事务中记录日志？

**A:** 使用独立事务记录日志，避免主事务回滚影响日志。

```java
@Service
public class OperationLogService {
    
    // 使用独立事务记录日志
    @Transactional(rollbackFor = Exception.class, 
                   propagation = Propagation.REQUIRES_NEW)
    public void logOperation(String username, String operation) {
        OperationLog log = new OperationLog();
        log.setUsername(username);
        log.setOperation(operation);
        log.setCreateTime(new Date());
        logRepository.save(log);
        // 即使主事务回滚，日志也会保留
    }
}

@Service
public class UserService {
    
    @Autowired
    private OperationLogService logService;
    
    @Transactional(rollbackFor = Exception.class)
    public User save(User user) {
        User saved = userRepository.save(user);
        
        // 记录日志（独立事务）
        logService.logOperation("admin", "创建用户: " + user.getUsername());
        
        return saved;
    }
}
```

**优势：**
- ✅ 日志不会被主事务回滚影响
- ✅ 便于问题追踪
- ✅ 审计合规

---

## 🎓 最佳实践

### Q13: 事务管理的最佳实践有哪些？

**A:** 遵循以下最佳实践：

#### ✅ DO - 应该做的

1. **所有 Service 方法都添加事务注解**
```java
@Transactional(readOnly = true)
public List<User> findAll() { ... }

@Transactional(rollbackFor = Exception.class)
public User save(User user) { ... }
```

2. **查询使用只读事务，修改使用读写事务**
```java
// 查询
@Transactional(readOnly = true)

// 修改
@Transactional(rollbackFor = Exception.class)
```

3. **保持事务简短**
```java
// ✅ 好：事务只做必要的数据库操作
@Transactional(rollbackFor = Exception.class)
public User save(User user) {
    return userRepository.save(user);
}

// ❌ 坏：事务中包含远程调用
@Transactional(rollbackFor = Exception.class)
public User save(User user) {
    User saved = userRepository.save(user);
    sendEmail(saved); // ❌ 不要在事务中调用外部服务
    sendSMS(saved);   // ❌ 不要在事务中调用外部服务
    return saved;
}
```

4. **在 Service 层控制事务边界**
```java
// ✅ Controller 不加事务
@RestController
public class UserController {
    @PostMapping("/users")
    public User create(@RequestBody User user) {
        return userService.save(user); // Service 层有事务
    }
}

// ✅ Service 层加事务
@Service
public class UserService {
    @Transactional(rollbackFor = Exception.class)
    public User save(User user) {
        return userRepository.save(user);
    }
}
```

5. **合理设置隔离级别**
```java
// 大多数场景使用默认隔离级别
@Transactional(rollbackFor = Exception.class)

// 特殊场景才需要指定
@Transactional(isolation = Isolation.READ_COMMITTED)
```

#### ❌ DON'T - 不应该做的

1. **不要在 Controller 层添加事务**
2. **不要在 Repository 层添加事务**
3. **不要捕获异常后不处理**
4. **不要在事务中进行远程调用**
5. **不要在循环中开启事务**
6. **不要忽略事务传播行为**
7. **不要在只读事务中执行写操作**

---

### Q14: 如何监控和调试事务？

**A:** 使用以下方法：

#### 1. 启用 SQL 日志

```properties
# application.properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

**输出示例：**
```
Hibernate: 
    insert 
    into
        users
        (username, password, department_id, id) 
    values
        (?, ?, ?, default)
binding parameter [1] as [VARCHAR] - [testuser]
binding parameter [2] as [VARCHAR] - [encoded_password]
```

#### 2. 查看事务状态

```java
@Transactional(rollbackFor = Exception.class)
public void save(User user) {
    TransactionStatus status = TransactionAspectSupport.currentTransactionStatus();
    
    log.info("事务是否只读: {}", status.isReadOnly());
    log.info("事务是否已完成: {}", status.isCompleted());
    log.info("事务是否标记回滚: {}", status.isRollbackOnly());
    
    userRepository.save(user);
}
```

#### 3. 使用 Spring Boot Actuator

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

访问端点：
- `/actuator/metrics` - 查看事务相关指标
- `/actuator/health` - 健康检查

#### 4. 添加事务监听器

```java
@Component
public class TransactionEventListener {
    
    @EventListener
    public void handleTransactionBegin(TransactionStartedEvent event) {
        log.info("事务开始: {}", event.getTransactionDefinition().getName());
    }
    
    @EventListener
    public void handleTransactionCommit(TransactionCommittedEvent event) {
        log.info("事务提交: {}", event.getTransactionDefinition().getName());
    }
    
    @EventListener
    public void handleTransactionRollback(TransactionRolledBackEvent event) {
        log.error("事务回滚: {}", event.getTransactionDefinition().getName());
    }
}
```

---

## 🔍 故障排查

### Q15: 事务回滚了但数据还是写入了，怎么回事？

**A:** 可能的原因：

#### 原因 1: 使用了不支持事务的存储引擎

```sql
-- 检查表引擎
SHOW TABLE STATUS WHERE Name = 'users';

-- 如果不是 InnoDB，转换引擎
ALTER TABLE users ENGINE=InnoDB;
```

#### 原因 2: 异常类型不对

```java
// ❌ 默认只对 RuntimeException 回滚
@Transactional
public void save() throws Exception {
    userRepository.save(user);
    throw new Exception("错误"); // checked exception，不会回滚
}

// ✅ 指定所有异常都回滚
@Transactional(rollbackFor = Exception.class)
public void save() throws Exception {
    userRepository.save(user);
    throw new Exception("错误"); // 会回滚
}
```

#### 原因 3: 异常被捕获

```java
// ❌ 异常被捕获，事务不知道发生了异常
@Transactional(rollbackFor = Exception.class)
public void save() {
    try {
        userRepository.save(user);
        throw new RuntimeException("错误");
    } catch (Exception e) {
        log.error("Error", e);
        // 没有重新抛出，事务不会回滚
    }
}

// ✅ 重新抛出异常
@Transactional(rollbackFor = Exception.class)
public void save() {
    try {
        userRepository.save(user);
        throw new RuntimeException("错误");
    } catch (Exception e) {
        log.error("Error", e);
        throw e; // 重新抛出
    }
}
```

---

### Q16: 出现死锁怎么办？

**A:** 死锁通常由以下原因引起：

#### 原因 1: 访问顺序不一致

```java
// 线程 1
@Transactional
public void transferAtoB() {
    lockAccount(A); // 先锁 A
    lockAccount(B); // 再锁 B
}

// 线程 2
@Transactional
public void transferBtoA() {
    lockAccount(B); // 先锁 B
    lockAccount(A); // 再锁 A → 死锁！
}
```

**解决方案：** 统一访问顺序

```java
// 两个方法都按相同顺序访问
@Transactional
public void transferAtoB() {
    lockAccount(A); // 先锁 A
    lockAccount(B); // 再锁 B
}

@Transactional
public void transferBtoA() {
    lockAccount(A); // 先锁 A
    lockAccount(B); // 再锁 B
}
```

#### 原因 2: 事务太长

```java
// ❌ 长事务容易死锁
@Transactional
public void longTransaction() {
    userRepository.save(user);
    Thread.sleep(10000); // 模拟长时间操作
    orderRepository.save(order);
}

// ✅ 缩短事务
public void shortTransaction() {
    userRepository.save(user);
    // 非数据库操作放在事务外
    Thread.sleep(10000);
    orderRepository.save(order);
}
```

#### 原因 3: 缺少索引

```sql
-- 添加索引减少锁范围
CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_order_user_id ON orders(user_id);
```

**排查步骤：**
1. 查看数据库死锁日志
2. 分析事务执行顺序
3. 优化访问顺序
4. 缩短事务时间
5. 添加合适的索引

---

### Q17: 事务性能差如何优化？

**A:** 从以下几个方面优化：

#### 1. 使用只读事务

```java
// ✅ 查询使用只读事务
@Transactional(readOnly = true)
public List<User> findAll() {
    return userRepository.findAll();
}
```

#### 2. 缩短事务时间

```java
// ❌ 坏：事务中包含耗时操作
@Transactional
public void badMethod() {
    userRepository.save(user);
    sendEmail(user); // 耗时操作
    sendSMS(user);   // 耗时操作
}

// ✅ 好：事务只做数据库操作
@Transactional
public void goodMethod() {
    userRepository.save(user);
}

// 耗时操作放在事务外
public void processUser(User user) {
    saveUser(user); // 事务内
    sendEmail(user); // 事务外
    sendSMS(user);   // 事务外
}
```

#### 3. 批量操作分批提交

```java
// 见 Q11 的分批提交示例
```

#### 4. 合理设置隔离级别

```java
// 不需要高隔离级别时使用较低的隔离级别
@Transactional(isolation = Isolation.READ_COMMITTED)
```

#### 5. 避免不必要的锁

```java
// ✅ 使用乐观锁代替悲观锁
@Version
private Long version;
```

---

## 📝 代码审查清单

在提交代码前，检查以下事项：

### 事务配置检查

- [ ] 所有 Service 方法都添加了 `@Transactional`
- [ ] 查询方法使用了 `readOnly = true`
- [ ] 修改方法使用了 `rollbackFor = Exception.class`
- [ ] 没有在 Controller 层添加事务
- [ ] 没有在 Repository 层添加事务
- [ ] 没有在私有方法上添加事务

### 事务有效性检查

- [ ] 事务方法没有被同类直接调用
- [ ] 异常处理不会导致事务失效
- [ ] 没有在只读事务中执行写操作
- [ ] 事务方法不是 final 或 static

### 性能检查

- [ ] 事务尽可能简短
- [ ] 没有在事务中进行远程调用
- [ ] 批量操作使用了分批提交
- [ ] 合理使用了只读事务

### 安全检查

- [ ] 敏感操作有适当的事务保护
- [ ] 并发场景考虑了隔离级别
- [ ] 关键业务有日志记录
- [ ] 异常情况有妥善处理

---

## 📚 参考资料

- [Spring 事务管理官方文档](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#transaction)
- [Spring @Transactional 详解](https://spring.io/guides/gs/managing-transactions/)
- [数据库事务隔离级别](https://en.wikipedia.org/wiki/Isolation_(database_systems))
- [MySQL InnoDB 事务模型](https://dev.mysql.com/doc/refman/8.0/en/innodb-transaction-model.html)

---

## 💡 快速参考

### 常用模板

#### 查询方法模板
```java
@Transactional(readOnly = true)
public ReturnType findXXX(Params params) {
    // 查询逻辑
    return repository.findXXX(params);
}
```

#### 保存方法模板
```java
@Transactional(rollbackFor = Exception.class)
public Entity save(Entity entity) {
    // 业务逻辑
    return repository.save(entity);
}
```

#### 删除方法模板
```java
@Transactional(rollbackFor = Exception.class)
public void delete(Long id) {
    // 验证逻辑
    repository.deleteById(id);
}
```

#### 更新方法模板
```java
@Transactional(rollbackFor = Exception.class)
public Entity update(Long id, UpdateRequest request) {
    Entity entity = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("不存在"));
    
    // 更新字段
    entity.setXxx(request.getXxx());
    
    return repository.save(entity);
}
```

---

**文档版本**: 1.0  
**最后更新**: 2026-05-05  
**维护者**: Admin System 开发团队

---

**还有其他问题？** 欢迎提交 Issue 或联系开发团队！
