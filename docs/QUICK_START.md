# 快速开始指南

## 🚀 5分钟快速上手

本指南将帮助你在 5 分钟内启动并运行 Admin System。

---

## 📋 前置要求

- **JDK**: 17 或更高版本
- **Maven**: 3.6 或更高版本
- **Git**: 用于克隆项目（可选）

### 检查环境

```bash
# 检查 Java 版本
java -version

# 检查 Maven 版本
mvn -version
```

---

## 📥 获取项目

### 方式一：克隆仓库（推荐）

```bash
git clone <repository-url>
cd admin-system
```

### 方式二：下载 ZIP

从 GitHub/GitLab 下载项目 ZIP 文件并解压。

---

## 🔧 编译项目

```bash
mvn clean compile
```

**预期输出**:
```
[INFO] BUILD SUCCESS
[INFO] Total time:  X.XXX s
```

---

## ▶️ 启动应用

### 方式一：使用 Maven（开发环境）

```bash
mvn spring-boot:run
```

### 方式二：打包后运行

```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar target/admin-system-1.0-SNAPSHOT.jar
```

**预期输出**:
```
Started AdminSystemApplication in X.XXX seconds
Tomcat started on port(s): 8080 (http)
H2 console available at '/h2-console'
```

---

## 🌐 访问应用

### 主应用

打开浏览器访问：**http://localhost:8080**

默认管理员账号：
- **用户名**: `admin`
- **密码**: `admin123`

### H2 数据库控制台

访问：**http://localhost:8080/h2-console**

连接配置：
- **JDBC URL**: `jdbc:h2:mem:admin_system`
- **用户名**: `sa`
- **密码**: （留空）

---

## 🎯 首次使用

### 1. 登录系统

1. 打开 http://localhost:8080
2. 输入用户名 `admin` 和密码 `admin123`
3. 点击"登录"按钮

### 2. 查看仪表板

登录后会进入仪表板页面，显示：
- 用户统计
- 角色统计
- 最近操作日志

### 3. 探索功能

#### 用户管理
- 路径：菜单 → 用户管理
- 功能：查看、创建、编辑、删除用户

#### 角色管理
- 路径：菜单 → 角色管理
- 功能：管理角色和权限分配

#### 部门管理
- 路径：菜单 → 部门管理
- 功能：管理组织架构

#### 工作流
- 路径：菜单 → 工作流管理
- 功能：部署流程、启动流程、审批任务

---

## 🧪 运行测试

### 运行所有测试

```bash
mvn test
```

### 运行特定测试类

```bash
# 单元测试
mvn test -Dtest=UserServiceTest

# E2E 测试
mvn test -Dtest=E2ETest
```

---

## 🔍 常见问题

### 1. 端口 8080 已被占用

**解决方案**：修改端口

```bash
# 方式一：命令行指定
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"

# 方式二：修改 application.properties
echo "server.port=8081" >> src/main/resources/application.properties
```

### 2. Maven 依赖下载失败

**解决方案**：清理并重新下载

```bash
mvn clean
mvn dependency:purge-local-repository
mvn install
```

### 3. Java 版本不匹配

**错误信息**: `Unsupported class file major version`

**解决方案**：确保使用 JDK 17+

```bash
# 检查当前 Java 版本
java -version

# 设置 JAVA_HOME
export JAVA_HOME=/path/to/jdk17
```

### 4. 无法访问 H2 控制台

**检查项**：
1. 确认应用已启动
2. 访问 http://localhost:8080/h2-console
3. 使用正确的 JDBC URL: `jdbc:h2:mem:admin_system`

---

## 🛠️ 开发环境配置

### IDE 配置

#### IntelliJ IDEA

1. **导入项目**
   - File → Open → 选择项目目录
   - 选择 `pom.xml` 作为项目文件

2. **配置运行配置**
   - Run → Edit Configurations
   - Add New Configuration → Spring Boot
   - Main class: `com.example.adminsystem.AdminSystemApplication`

3. **启用热部署**（可选）
   ```xml
   <!-- pom.xml -->
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-devtools</artifactId>
       <optional>true</optional>
   </dependency>
   ```

#### Eclipse

1. **导入项目**
   - File → Import → Maven → Existing Maven Projects
   - 选择项目目录

2. **运行应用**
   - 右键项目 → Run As → Spring Boot App

---

## 📝 第一个自定义功能

### 示例：添加一个新的配置项

#### 1. 通过界面添加

1. 登录系统
2. 进入"配置管理"
3. 点击"新增配置"
4. 填写：
   - 配置键：`app.title`
   - 配置值：`我的管理系统`
   - 描述：`应用标题`
5. 保存

#### 2. 在代码中使用

```java
@Autowired
private ConfigService configService;

public String getAppTitle() {
    return configService.getConfigValue("app.title", "默认标题");
}
```

---

## 🔄 切换到 MySQL（生产环境）

### 1. 安装 MySQL

确保 MySQL 8.0+ 已安装并运行。

### 2. 创建数据库

```sql
CREATE DATABASE admin_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 修改配置

编辑 `src/main/resources/application.properties`:

```properties
# 注释掉 H2 配置
# spring.datasource.url=jdbc:h2:mem:admin_system
# spring.datasource.username=sa
# spring.datasource.password=
# spring.datasource.driver-class-name=org.h2.Driver
# spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
# spring.h2.console.enabled=true

# 启用 MySQL 配置
spring.datasource.url=jdbc:mysql://localhost:3306/admin_system?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### 4. 重启应用

```bash
mvn spring-boot:run
```

应用启动时会自动创建表结构。

---

## 📚 下一步

现在你已经成功启动了 Admin System，可以：

1. 📖 阅读 [完整技术文档](../README.md)
2. 🔐 了解 [事务管理机制](TRANSACTION_GUIDE.md)
3. 🌐 查看 [API 接口文档](API_DOCUMENTATION.md)
4. 🧪 编写自己的功能模块
5. 🎨 定制前端页面

---

## 💡 提示与技巧

### 1. 查看实时日志

```bash
mvn spring-boot:run | grep "Hibernate:"
```

### 2. 快速重启

使用 Spring Boot DevTools 实现自动重启：
- 修改代码后自动重新加载
- 无需手动重启应用

### 3. 数据库备份

H2 数据库导出：
```bash
# 通过 H2 控制台执行
SCRIPT TO 'backup.sql'
```

### 4. API 测试

使用 curl 测试 API：
```bash
# 查询用户列表
curl http://localhost:8080/users \
  -H "Cookie: JSESSIONID=xxx"
```

---

## 🆘 获取帮助

遇到问题？

1. 📖 查看完整文档
2. 🔍 搜索 Issues
3. 📧 联系开发团队
4. 💬 加入社区讨论

---

## ✅ 检查清单

启动前确认：

- [ ] JDK 17+ 已安装
- [ ] Maven 3.6+ 已安装
- [ ] 项目编译成功
- [ ] 应用在 8080 端口启动
- [ ] 可以访问登录页面
- [ ] 可以使用默认账号登录
- [ ] H2 控制台可访问

---

**祝你使用愉快！** 🎉

如有任何问题，请查阅完整文档或联系开发团队。

---

**文档版本**: 1.0  
**最后更新**: 2026-05-05  
**维护者**: Admin System 开发团队
