# 常用数据库事务实现差异对比

## 📋 目录

- [概述](#概述)
- [MySQL (InnoDB)](#mysql-innodb)
- [PostgreSQL](#postgresql)
- [Oracle](#oracle)
- [SQL Server](#sql-server)
- [H2 Database](#h2-database)
- [SQLite](#sqlite)
- [对比总结](#对比总结)
- [Spring Boot 中的配置](#spring-boot-中的配置)
- [最佳实践](#最佳实践)

---

## 🎯 概述

不同数据库在事务实现上存在显著差异，主要体现在：

1. **隔离级别支持**
2. **锁机制实现**
3. **MVCC（多版本并发控制）实现**
4. **事务日志管理**
5. **死锁检测和处理**
6. **性能和并发能力**

理解这些差异对于选择合适的数据库和优化应用性能至关重要。

---

## 🐬 MySQL (InnoDB)

### 基本信息

- **存储引擎**: InnoDB（默认，支持事务）
- **事务模型**: MVCC + 行级锁
- **默认隔离级别**: REPEATABLE READ

### 支持的隔离级别

| 隔离级别 | 是否支持 | 说明 |
|---------|---------|------|
| READ UNCOMMITTED | ✅ | 可能脏读 |
| READ COMMITTED | ✅ | 避免脏读 |
| REPEATABLE READ | ✅ | 默认级别，避免不可重复读 |
| SERIALIZABLE | ✅ | 最高隔离，性能最差 |

### MVCC 实现

InnoDB 使用 **Undo Log** 实现 MVCC：

```
数据行结构：
┌──────────┬────────────┬──────────┬────────┐
│ 实际数据 │ TRX_ID     │ ROLL_PTR │ 其他   │
│          │ (事务ID)   │ (回滚指针)│        │
└──────────┴────────────┴──────────┴────────┘
                    ↓
              Undo Log (历史版本)
```

**工作原理：**
1. 每行数据有隐藏列：TRX_ID（最后修改的事务ID）、ROLL_PTR（回滚指针）
2. 修改数据时，旧版本写入 Undo Log
3. 读取时根据事务隔离级别和可见性判断返回哪个版本

### 锁机制

#### 1. 行级锁（Row Lock）

```sql
-- 共享锁（读锁）
SELECT * FROM users WHERE id = 1 LOCK IN SHARE MODE;

-- 排他锁（写锁）
SELECT * FROM users WHERE id = 1 FOR UPDATE;
```

#### 2. 间隙锁（Gap Lock）

只在 **REPEATABLE READ** 级别下生效：

```sql
-- 假设 users 表有 id: 1, 3, 5, 10
SELECT * FROM users WHERE id > 3 AND id < 10 FOR UPDATE;

-- 会锁定：
-- - id=5 的记录（记录锁）
-- - (3,5) 之间的间隙（间隙锁）
-- - (5,10) 之间的间隙（间隙锁）
```

**作用：** 防止幻读

#### 3. _next-key 锁_

记录锁 + 间隙锁的组合，是 InnoDB 在 RR 级别下的默认加锁方式。

### 事务日志

InnoDB 使用两种日志：

#### 1. Redo Log（重做日志）

- **作用：** 保证事务的持久性（Durability）
- **特点：** 循环写入，物理日志
- **位置：** `ib_logfile0`, `ib_logfile1`

```
事务提交流程：
1. 修改数据页（内存中）
2. 写入 Redo Log Buffer
3. 刷盘到 Redo Log（WAL 机制）
4. 异步刷盘到数据文件
```

#### 2. Undo Log（回滚日志）

- **作用：** 保证事务的原子性（Atomicity）和 MVCC
- **特点：** 逻辑日志，记录相反操作
- **用途：** 
  - 事务回滚
  - MVCC 版本链

### 死锁处理

- **检测机制：** 等待图（Wait-for Graph）
- **处理方式：** 自动检测并回滚代价较小的事务
- **查看死锁：**
```sql
SHOW ENGINE INNODB STATUS;
```

### 配置优化

```properties
# my.cnf / my.ini

# Redo Log 大小
innodb_log_file_size = 256M
innodb_log_files_in_group = 2

# 刷盘策略（1=最安全，0=性能最好）
innodb_flush_log_at_trx_commit = 1

# 缓冲池大小（建议设置为物理内存的 50-70%）
innodb_buffer_pool_size = 4G

# 锁等待超时时间（秒）
innodb_lock_wait_timeout = 50
```

### Spring Boot 配置

```properties
# application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/admin_system?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# 连接池配置（HikariCP）
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

### 特点总结

**优点：**
- ✅ MVCC 实现成熟，并发性能好
- ✅ 行级锁粒度细，锁竞争少
- ✅ Gap Lock 有效防止幻读
- ✅ Redo Log + Undo Log 保证 ACID

**缺点：**
- ❌ RR 级别下的 Gap Lock 可能导致锁范围过大
- ❌ 大事务容易导致 Undo Log 膨胀
- ❌ 主从复制可能存在延迟

---

## 🐘 PostgreSQL

### 基本信息

- **事务模型:** 纯 MVCC
- **默认隔离级别:** READ COMMITTED
- **锁机制:** 多粒度锁 + MVCC

### 支持的隔离级别

| 隔离级别 | 是否支持 | 说明 |
|---------|---------|------|
| READ UNCOMMITTED | ⚠️ | 实际等同于 READ COMMITTED |
| READ COMMITTED | ✅ | 默认级别 |
| REPEATABLE READ | ✅ | 基于快照 |
| SERIALIZABLE | ✅ | SSI（Serializable Snapshot Isolation） |

**注意：** PostgreSQL 不支持 READ UNCOMMITTED，会自动提升到 READ COMMITTED。

### MVCC 实现

PostgreSQL 使用 **多版本存储** 实现 MVCC：

```
表中的物理存储：
┌─────────────────────────────────────────┐
│ Tuple v1: xmin=100, xmax=0, data=A     │ ← 版本1
│ Tuple v2: xmin=200, xmax=0, data=B     │ ← 版本2
│ Tuple v3: xmin=300, xmax=0, data=C     │ ← 版本3
└─────────────────────────────────────────┘

xmin: 创建该版本的事务ID
xmax: 删除该版本的事务ID（0表示未删除）
```

**工作原理：**
1. UPDATE/DELETE 不修改原数据，而是插入新版本
2. 每个事务看到的数据快照由 xmin/xmax 决定
3. 旧版本通过 VACUUM 清理

**与 MySQL 的区别：**
- MySQL：旧版本存储在 Undo Log
- PostgreSQL：旧版本存储在数据表中

### 锁机制

#### 1. 行级锁

```sql
-- 共享锁
SELECT * FROM users WHERE id = 1 FOR SHARE;

-- 排他锁
SELECT * FROM users WHERE id = 1 FOR UPDATE;

-- NOWAIT（不等待，立即失败）
SELECT * FROM users WHERE id = 1 FOR UPDATE NOWAIT;

-- SKIP LOCKED（跳过已锁定的行）
SELECT * FROM users WHERE id = 1 FOR UPDATE SKIP LOCKED;
```

#### 2. 表级锁

```sql
LOCK TABLE users IN EXCLUSIVE MODE;
```

**锁模式：**
- ACCESS SHARE（读锁）
- ROW SHARE
- ROW EXCLUSIVE
- SHARE UPDATE EXCLUSIVE
- SHARE
- SHARE ROW EXCLUSIVE
- EXCLUSIVE
- ACCESS EXCLUSIVE（写锁，最强）

### 事务日志

PostgreSQL 使用 **WAL（Write-Ahead Logging）**：

```
事务提交流程：
1. 生成 WAL 记录
2. 写入 WAL Buffer
3. 刷盘到 WAL 文件（pg_wal/）
4. 检查点（Checkpoint）时刷盘数据文件
```

**WAL 文件：**
- 位置：`$PGDATA/pg_wal/`
- 作用：崩溃恢复、流复制

### VACUUM 机制

由于 MVCC 产生大量死元组（Dead Tuple），需要定期清理：

```sql
-- 手动清理
VACUUM users;

-- 清理并回收空间
VACUUM FULL users;

-- 分析统计信息
ANALYZE users;

-- 自动清理（默认启用）
-- 由 autovacuum daemon 自动执行
```

**参数配置：**
```conf
# postgresql.conf
autovacuum = on
autovacuum_max_workers = 3
autovacuum_naptime = 1min
autovacuum_vacuum_threshold = 50
autovacuum_analyze_threshold = 50
```

### 死锁处理

- **检测机制：** 定期检测等待图
- **处理方式：** 回滚其中一个事务
- **检测间隔：** `deadlock_timeout = 1s`（默认）

```sql
-- 查看死锁
SELECT * FROM pg_stat_activity WHERE wait_event_type = 'Lock';
```

### 配置优化

```conf
# postgresql.conf

# 共享缓冲区（建议物理内存的 25%）
shared_buffers = 2GB

# WAL 配置
wal_level = replica
max_wal_size = 1GB
min_wal_size = 80MB

# 检查点
checkpoint_completion_target = 0.9

# 并发连接
max_connections = 200

# 工作内存
work_mem = 4MB
```

### Spring Boot 配置

```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/admin_system
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# 连接池配置
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

### 特点总结

**优点：**
- ✅ 纯 MVCC，读写不阻塞
- ✅ SERIALIZABLE 级别使用 SSI，真正串行化
- ✅ 支持复杂的查询和优化器
- ✅ ACID 合规性好

**缺点：**
- ❌ VACUUM 维护成本高
- ❌ 表膨胀问题（需要定期 VACUUM）
- ❌ 写性能相对 MySQL 略低
- ❌ 不支持 READ UNCOMMITTED

---

## 🔶 Oracle

### 基本信息

- **事务模型:** MVCC（基于 Undo Tablespace）
- **默认隔离级别:** READ COMMITTED
- **锁机制:** 行级锁 + MVCC

### 支持的隔离级别

| 隔离级别 | 是否支持 | 说明 |
|---------|---------|------|
| READ COMMITTED | ✅ | 默认级别 |
| SERIALIZABLE | ✅ | 基于快照 |
| READ ONLY | ✅ | Oracle 特有 |

**注意：** Oracle 不支持 REPEATABLE READ，但 SERIALIZABLE 可以实现类似效果。

### MVCC 实现

Oracle 使用 **Undo Tablespace** 实现 MVCC：

```
数据块结构：
┌──────────────────────────┐
│ ITL (Interested Tx List) │ ← 事务槽
│ Row 1: data + lock info  │
│ Row 2: data + lock info  │
└──────────────────────────┘
         ↓
  Undo Tablespace
  (历史版本链)
```

**工作原理：**
1. 每个数据块有 ITL（事务槽列表）
2. 修改数据时，在 ITL 中记录事务信息
3. 旧版本写入 Undo Tablespace
4. 读取时根据 SCN（System Change Number）构建一致性读

### 锁机制

#### 1. 行级锁

```sql
-- Oracle 自动加锁，无需显式指定
UPDATE users SET name = 'test' WHERE id = 1;

-- 显式锁定
SELECT * FROM users WHERE id = 1 FOR UPDATE;
SELECT * FROM users WHERE id = 1 FOR UPDATE NOWAIT;
SELECT * FROM users WHERE id = 1 FOR UPDATE WAIT 5;
```

**特点：**
- Oracle 的行锁信息存储在数据块中（不在内存中）
- 无锁升级机制（只有行锁和表锁）

#### 2. 表级锁

```sql
LOCK TABLE users IN EXCLUSIVE MODE;
```

**锁模式：**
- ROW SHARE (SS)
- ROW EXCLUSIVE (SX)
- SHARE (S)
- SHARE ROW EXCLUSIVE (SSX)
- EXCLUSIVE (X)

### 事务日志

Oracle 使用 **Redo Log** 和 **Undo Log**：

#### 1. Redo Log

- **作用：** 崩溃恢复、介质恢复
- **特点：** 循环使用，联机重做日志
- **位置：** `redo01.log`, `redo02.log`, `redo03.log`

#### 2. Undo Tablespace

- **作用：** 事务回滚、一致性读、闪回查询
- **特点：** 自动管理，可配置保留时间

```sql
-- 查看 Undo 配置
SHOW PARAMETER undo;

-- 设置 Undo 保留时间（秒）
ALTER SYSTEM SET undo_retention = 900;
```

### SCN（System Change Number）

Oracle 使用 SCN 作为逻辑时间戳：

```sql
-- 查看当前 SCN
SELECT CURRENT_SCN FROM V$DATABASE;

-- 闪回查询（基于 SCN）
SELECT * FROM users AS OF SCN 12345678;

-- 闪回查询（基于时间）
SELECT * FROM users AS OF TIMESTAMP TO_TIMESTAMP('2026-05-05 10:00:00', 'YYYY-MM-DD HH24:MI:SS');
```

### 死锁处理

- **检测机制：** 自动检测
- **处理方式：** 回滚其中一个事务，返回 ORA-00060 错误
- **查看死锁：**
```sql
SELECT * FROM V$LOCK WHERE BLOCK > 0;
```

### 配置优化

```sql
-- Undo Tablespace 大小
ALTER DATABASE DATAFILE '/path/to/undo01.dbf' RESIZE 2G;

-- Redo Log 大小
ALTER DATABASE ADD LOGFILE GROUP 4 ('/path/to/redo04.log') SIZE 500M;

-- 归档模式（生产环境必须）
ARCHIVE LOG LIST;
ALTER DATABASE ARCHIVELOG;
```

### Spring Boot 配置

```properties
# application.properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:orcl
spring.datasource.username=admin_system
spring.datasource.password=your_password
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect

# 连接池配置
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.connection-timeout=30000
```

### 特点总结

**优点：**
- ✅ MVCC 实现优秀，读写不阻塞
- ✅ 一致性读基于 SCN，非常可靠
- ✅ 闪回查询功能强大
- ✅ 企业级稳定性和可靠性

**缺点：**
- ❌ 商业数据库，成本高
- ❌ 配置和管理复杂
- ❌ 学习曲线陡峭
- ❌ 资源占用较大

---

## 💾 SQL Server

### 基本信息

- **事务模型:** 传统锁 + SNAPSHOT（可选 MVCC）
- **默认隔离级别:** READ COMMITTED
- **锁机制:** 多粒度锁

### 支持的隔离级别

| 隔离级别 | 是否支持 | 说明 |
|---------|---------|------|
| READ UNCOMMITTED | ✅ | 脏读 |
| READ COMMITTED | ✅ | 默认级别 |
| REPEATABLE READ | ✅ | 保持锁 |
| SERIALIZABLE | ✅ | 范围锁 |
| SNAPSHOT | ✅ | 基于行版本（MVCC） |
| READ COMMITTED SNAPSHOT | ✅ | RCSI（推荐） |

### MVCC 实现（SNAPSHOT）

SQL Server 2005+ 支持基于 **TempDB** 的 MVCC：

```
启用快照隔离：
ALTER DATABASE admin_system SET ALLOW_SNAPSHOT_ISOLATION ON;
ALTER DATABASE admin_system SET READ_COMMITTED_SNAPSHOT ON;

数据存储：
┌──────────────┐
│ 数据文件     │ ← 当前版本
└──────────────┘
       ↓
┌──────────────┐
│ TempDB       │ ← 历史版本（行版本存储）
└──────────────┘
```

**两种快照模式：**

1. **SNAPSHOT Isolation**
   - 需要显式设置：`SET TRANSACTION ISOLATION LEVEL SNAPSHOT`
   - 事务开始时建立快照

2. **READ COMMITTED SNAPSHOT (RCSI)**
   - 数据库级别启用
   - 所有 READ COMMITTED 事务自动使用快照
   - **推荐使用**，无需修改代码

### 锁机制

#### 1. 锁粒度

- **RID:** 行标识符锁
- **KEY:** 索引键锁
- **PAGE:** 页锁
- **EXTENT:** 区锁
- **TABLE:** 表锁
- **DATABASE:** 数据库锁

#### 2. 锁模式

- **S (Shared):** 共享锁（读）
- **X (Exclusive):** 排他锁（写）
- **U (Update):** 更新锁
- **IS (Intent Shared):** 意向共享锁
- **IX (Intent Exclusive):** 意向排他锁
- **SIX:** 共享意向排他锁

```sql
-- 查看当前锁
SELECT * FROM sys.dm_tran_locks;

-- 查看锁等待
SELECT * FROM sys.dm_os_waiting_tasks;
```

### 事务日志

SQL Server 使用 **Transaction Log**：

```
日志文件：admin_system.ldf

恢复模式：
1. SIMPLE: 自动截断日志
2. FULL: 完整日志，支持时间点恢复
3. BULK_LOGGED: 批量操作最小日志
```

```sql
-- 查看恢复模式
SELECT name, recovery_model_desc FROM sys.databases;

-- 修改恢复模式
ALTER DATABASE admin_system SET RECOVERY FULL;

-- 备份日志
BACKUP LOG admin_system TO DISK = 'C:\backup\admin_system_log.trn';
```

### 死锁处理

- **检测机制：** 锁监视器（Lock Monitor）
- **处理方式：** 选择死锁牺牲品（Deadlock Victim）
- **跟踪死锁：**

```sql
-- 启用死锁跟踪
DBCC TRACEON (1222, -1);

-- 查看死锁图
SELECT * FROM sys.dm_exec_requests WHERE blocking_session_id <> 0;
```

### 配置优化

```sql
-- 最大并行度
EXEC sp_configure 'max degree of parallelism', 4;

-- 最大服务器内存
EXEC sp_configure 'max server memory', 8192;

-- TempDB 优化（多个数据文件）
ALTER DATABASE tempdb ADD FILE (NAME = tempdev2, FILENAME = '...');
```

### Spring Boot 配置

```properties
# application.properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=admin_system;encrypt=true;trustServerCertificate=true;
spring.datasource.username=sa
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
```

### 特点总结

**优点：**
- ✅ RCSI 提供优秀的并发性能
- ✅ 与 Windows 生态集成好
- ✅ 图形化管理工具强大
- ✅ 商业支持完善

**缺点：**
- ❌ 默认不使用 MVCC（需启用 RCSI）
- ❌ TempDB 可能成为瓶颈
- ❌ 主要在 Windows 平台表现最佳
- ❌ 许可成本高

---

## 🔷 H2 Database

### 基本信息

- **类型:** 嵌入式/内存数据库
- **事务模型:** 传统锁 + MVCC（可选）
- **默认隔离级别:** READ COMMITTED
- **适用场景:** 开发测试、小型应用

### 支持的隔离级别

| 隔离级别 | 是否支持 | 说明 |
|---------|---------|------|
| READ UNCOMMITTED | ✅ | |
| READ COMMITTED | ✅ | 默认级别 |
| REPEATABLE READ | ✅ | |
| SERIALIZABLE | ✅ | |

### MVCC 实现

H2 支持两种模式：

#### 1. 传统模式（默认）

- 使用锁机制
- 简单但并发性能一般

#### 2. MVCC 模式

```properties
# 启用 MVCC
jdbc:h2:mem:admin_system;MVCC=TRUE
```

- 类似 PostgreSQL 的多版本存储
- 提高并发读性能

### 事务日志

H2 支持多种持久化模式：

```java
// 内存模式（重启丢失）
jdbc:h2:mem:admin_system

// 文件模式（持久化）
jdbc:h2:file:./data/admin_system

// 混合模式
jdbc:h2:mem:admin_system;DB_CLOSE_DELAY=-1
```

### Spring Boot 配置

```properties
# application.properties（开发环境）
spring.datasource.url=jdbc:h2:mem:admin_system;MVCC=TRUE
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect

# H2 控制台
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### 特点总结

**优点：**
- ✅ 零配置，开箱即用
- ✅ 支持内存模式，速度极快
- ✅ 适合开发和测试
- ✅ 单 JAR 包，无依赖

**缺点：**
- ❌ 不适合生产环境（大数据量）
- ❌ 并发性能有限
- ❌ 功能相对简单
- ❌ 社区支持较少

---

## 📱 SQLite

### 基本信息

- **类型:** 嵌入式数据库
- **事务模型:** 文件级锁
- **默认隔离级别:** SERIALIZABLE
- **适用场景:** 移动应用、桌面应用、IoT

### 支持的隔离级别

| 隔离级别 | 是否支持 | 说明 |
|---------|---------|------|
| READ UNCOMMITTED | ✅ | |
| READ COMMITTED | ✅ | |
| REPEATABLE READ | ✅ | |
| SERIALIZABLE | ✅ | 默认级别 |

### 锁机制

SQLite 使用 **文件级锁**：

```
锁级别：
1. UNLOCKED: 无锁
2. SHARED: 共享锁（读）
3. RESERVED: 预留锁（准备写）
4. PENDING: 等待锁
5. EXCLUSIVE: 排他锁（写）
```

**特点：**
- 同一时刻只能有一个写操作
- 可以有多个读操作
- WAL 模式下可以提高并发

### WAL 模式（推荐）

```sql
-- 启用 WAL 模式
PRAGMA journal_mode=WAL;
```

**优势：**
- 读写可以并发
- 写操作不阻塞读操作
- 更好的性能

### 事务处理

```sql
-- 显式事务
BEGIN TRANSACTION;
INSERT INTO users VALUES (1, 'test');
UPDATE users SET name = 'updated' WHERE id = 1;
COMMIT;

-- 回滚
ROLLBACK;

-- 自动提交（默认）
INSERT INTO users VALUES (2, 'auto'); -- 自动提交
```

### Spring Boot 配置

```properties
# 需要添加 SQLite JDBC 驱动
# pom.xml: org.xerial:sqlite-jdbc

spring.datasource.url=jdbc:sqlite:./data/admin_system.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.properties.hibernate.dialect=org.hibernate.community.dialect.SQLiteDialect
```

### 特点总结

**优点：**
- ✅ 零配置，单文件
- ✅ 跨平台，无服务器
- ✅ 广泛应用于移动和嵌入式
- ✅ 可靠性高

**缺点：**
- ❌ 写并发性能差（文件锁）
- ❌ 不适合高并发场景
- ❌ 不支持用户管理
- ❌ 功能相对有限

---

## 📊 对比总结

### 隔离级别支持对比

| 数据库 | READ UNCOMMITTED | READ COMMITTED | REPEATABLE READ | SERIALIZABLE | 默认级别 |
|--------|-----------------|----------------|-----------------|--------------|---------|
| MySQL | ✅ | ✅ | ✅ | ✅ | REPEATABLE READ |
| PostgreSQL | ⚠️¹ | ✅ | ✅ | ✅ | READ COMMITTED |
| Oracle | ❌ | ✅ | ❌² | ✅ | READ COMMITTED |
| SQL Server | ✅ | ✅ | ✅ | ✅ | READ COMMITTED |
| H2 | ✅ | ✅ | ✅ | ✅ | READ COMMITTED |
| SQLite | ✅ | ✅ | ✅ | ✅ | SERIALIZABLE |

> ¹ PostgreSQL 将 READ UNCOMMITTED 提升为 READ COMMITTED  
> ² Oracle 不支持 REPEATABLE READ，但 SERIALIZABLE 可实现类似效果

### MVCC 实现对比

| 数据库 | MVCC 实现 | 历史版本存储 | 清理机制 |
|--------|----------|------------|---------|
| MySQL | Undo Log | Undo Tablespace | 自动清理 |
| PostgreSQL | 多版本存储 | 数据表中 | VACUUM |
| Oracle | Undo Tablespace | Undo Segments | 自动管理 |
| SQL Server | TempDB | TempDB | 自动清理 |
| H2 | 可选 | 内存/文件 | 自动清理 |
| SQLite | 无（传统锁） | N/A | N/A |

### 锁机制对比

| 数据库 | 锁粒度 | 锁升级 | 死锁检测 |
|--------|--------|--------|---------|
| MySQL | 行级 + 间隙锁 | 无 | 自动检测 |
| PostgreSQL | 行级 + 表级 | 无 | 自动检测 |
| Oracle | 行级 + 表级 | 无 | 自动检测 |
| SQL Server | 多粒度 | 有 | 自动检测 |
| H2 | 表级/行级 | 无 | 简单检测 |
| SQLite | 文件级 | 无 | 无（忙重试） |

### 性能特性对比

| 数据库 | 读性能 | 写性能 | 并发能力 | 适用场景 |
|--------|--------|--------|---------|---------|
| MySQL | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | Web 应用、通用场景 |
| PostgreSQL | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | 复杂查询、数据分析 |
| Oracle | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | 企业级应用、金融系统 |
| SQL Server | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | Windows 生态、企业应用 |
| H2 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐ | 开发测试、小型应用 |
| SQLite | ⭐⭐⭐ | ⭐⭐ | ⭐ | 移动应用、嵌入式 |

### 事务日志对比

| 数据库 | 日志类型 | 日志位置 | 主要用途 |
|--------|---------|---------|---------|
| MySQL | Redo + Undo | ib_logfile + Undo TS | 崩溃恢复、MVCC |
| PostgreSQL | WAL | pg_wal/ | 崩溃恢复、复制 |
| Oracle | Redo + Undo | redo log + Undo TS | 崩溃恢复、一致性读 |
| SQL Server | Transaction Log | .ldf 文件 | 崩溃恢复、备份 |
| H2 | Transaction Log | 内存/文件 | 持久化 |
| SQLite | Journal/WAL | 同目录文件 | 原子提交 |

---

## ⚙️ Spring Boot 中的配置

### 根据不同数据库配置事务管理器

Spring Boot 自动配置会根据数据源自动选择合适的事务管理器，通常无需手动配置。

### 通用配置

```properties
# application.properties

# 事务超时（秒）
spring.transaction.default-timeout=30

# 事务隔离级别（可选）
# spring.jpa.properties.hibernate.connection.isolation=2

# 连接池配置（HikariCP）
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

### MySQL 专用配置

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/admin_system?useSSL=false&serverTimezone=UTC&useLegacyDatetimeCode=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# InnoDB 优化
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

### PostgreSQL 专用配置

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/admin_system
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# 启用 RCSI（需要在数据库中执行）
# ALTER DATABASE admin_system SET read_committed_snapshot = on;
```

### Oracle 专用配置

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:orcl
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect

# Oracle 特定优化
spring.jpa.properties.hibernate.jdbc.batch_size=30
spring.jpa.properties.hibernate.jdbc.fetch_size=50
```

### 多数据源事务配置

如果使用多个数据源，需要配置分布式事务：

```java
@Configuration
public class MultiDataSourceConfig {
    
    @Bean
    @Primary
    public PlatformTransactionManager transactionManager1(
            @Qualifier("dataSource1") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
    
    @Bean
    public PlatformTransactionManager transactionManager2(
            @Qualifier("dataSource2") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
```

**注意：** 真正的分布式事务需要使用 JTA（如 Atomikos、Bitronix）。

---

## 🎓 最佳实践

### 1. 选择合适的数据库

| 场景 | 推荐数据库 | 原因 |
|------|-----------|------|
| Web 应用、通用场景 | MySQL | 成熟稳定，社区活跃 |
| 复杂查询、GIS | PostgreSQL | 功能强大，扩展性好 |
| 企业级、金融系统 | Oracle | 稳定性强，支持完善 |
| Windows 生态 | SQL Server | 集成度高，工具完善 |
| 开发测试 | H2 | 零配置，速度快 |
| 移动/嵌入式 | SQLite | 轻量级，零配置 |

### 2. 选择合适的隔离级别

```java
// 大多数场景使用默认隔离级别
@Transactional(rollbackFor = Exception.class)
public void standardOperation() {
    // ...
}

// 高并发读场景，可以降低隔离级别
@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
public void highConcurrencyRead() {
    // ...
}

// 需要强一致性的场景
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
public void criticalOperation() {
    // ...
}
```

### 3. 避免长事务

```java
// ❌ 坏：长事务
@Transactional
public void badLongTransaction() {
    userRepository.save(user);
    sendEmail(user);      // 耗时操作
    callExternalAPI(user); // 网络调用
    orderRepository.save(order);
}

// ✅ 好：短事务
@Transactional
public User saveUser(User user) {
    return userRepository.save(user);
}

public void processUser(User user) {
    User saved = saveUser(user); // 事务内
    sendEmail(saved);            // 事务外
    callExternalAPI(saved);      // 事务外
    saveOrder(saved);            // 新事务
}
```

### 4. 合理使用只读事务

```java
// ✅ 查询使用只读事务
@Transactional(readOnly = true)
public List<User> findAll() {
    return userRepository.findAll();
}

// ✅ 修改使用读写事务
@Transactional(rollbackFor = Exception.class)
public User save(User user) {
    return userRepository.save(user);
}
```

### 5. 监控和优化

```sql
-- MySQL: 查看慢查询
SHOW VARIABLES LIKE 'slow_query_log';
SHOW VARIABLES LIKE 'long_query_time';

-- PostgreSQL: 查看长时间运行的事务
SELECT * FROM pg_stat_activity WHERE state != 'idle';

-- Oracle: 查看活动会话
SELECT * FROM V$SESSION WHERE STATUS = 'ACTIVE';

-- SQL Server: 查看阻塞
SELECT * FROM sys.dm_exec_requests WHERE blocking_session_id <> 0;
```

### 6. 处理死锁

```java
@Transactional(rollbackFor = Exception.class)
public void transferMoney(Long fromId, Long toId, BigDecimal amount) {
    // 统一访问顺序，避免死锁
    if (fromId < toId) {
        lockAccount(fromId);
        lockAccount(toId);
    } else {
        lockAccount(toId);
        lockAccount(fromId);
    }
    
    // 执行转账
    deduct(fromId, amount);
    add(toId, amount);
}
```

### 7. 批量操作优化

```java
// 分批提交，避免大事务
@Autowired
private PlatformTransactionManager transactionManager;

public void batchInsert(List<User> users) {
    int batchSize = 100;
    
    for (int i = 0; i < users.size(); i += batchSize) {
        TransactionStatus status = transactionManager.getTransaction(
            new DefaultTransactionDefinition()
        );
        
        try {
            List<User> batch = users.subList(i, Math.min(i + batchSize, users.size()));
            userRepository.saveAll(batch);
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
}
```

---

## 📚 参考资料

- [MySQL InnoDB 事务模型](https://dev.mysql.com/doc/refman/8.0/en/innodb-transaction-model.html)
- [PostgreSQL MVCC](https://www.postgresql.org/docs/current/mvcc.html)
- [Oracle 事务指南](https://docs.oracle.com/en/database/oracle/oracle-database/19/cncpt/transactions.html)
- [SQL Server 事务隔离级别](https://docs.microsoft.com/en-us/sql/t-sql/statements/set-transaction-isolation-level-transact-sql)
- [H2 Database Documentation](http://www.h2database.com/html/main.html)
- [SQLite Transaction Control](https://www.sqlite.org/lang_transaction.html)

---

## 💡 快速决策指南

### 如何选择数据库？

```
需要事务支持？
├─ 是
│  ├─ 高并发 Web 应用？ → MySQL / PostgreSQL
│  ├─ 企业级关键业务？ → Oracle / SQL Server
│  ├─ 开发测试环境？ → H2
│  └─ 移动/嵌入式？ → SQLite
│
└─ 否
   └─ 考虑 NoSQL（MongoDB、Redis 等）
```

### 如何选择隔离级别？

```
并发要求高？
├─ 是 → READ COMMITTED
├─ 否 → REPEATABLE READ / SERIALIZABLE
└─ 不确定 → 使用数据库默认级别
```

### 如何优化事务性能？

```
1. 使用只读事务（查询操作）
2. 缩短事务时间
3. 避免在事务中进行远程调用
4. 批量操作分批提交
5. 合理设置隔离级别
6. 添加合适的索引
7. 监控和优化慢查询
```

---

**文档版本**: 1.0  
**最后更新**: 2026-05-05  
**维护者**: Admin System 开发团队

---

**理解数据库事务实现差异，选择合适的数据库和配置，是构建高性能应用的关键！** 🚀
