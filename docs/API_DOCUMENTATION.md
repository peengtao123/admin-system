# API 接口文档

## 📋 概述

本文档提供 Admin System 所有 RESTful API 接口的详细说明。

**Base URL**: `http://localhost:8080`  
**认证方式**: Session-based (Spring Security)  
**数据格式**: JSON / Form Data

---

## 🔐 认证接口

### 登录

```
POST /login
```

**请求参数** (Form Data):
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

**响应**:
- 成功: 302 重定向到 dashboard
- 失败: 重定向到 login?error

**示例**:
```bash
curl -X POST http://localhost:8080/login \
  -d "username=admin&password=admin123" \
  -c cookies.txt
```

---

### 登出

```
POST /logout
```

**响应**: 302 重定向到 login?logout

**示例**:
```bash
curl -X POST http://localhost:8080/logout \
  -b cookies.txt
```

---

## 👤 用户管理

### 查询所有用户

```
GET /users
```

**Headers**:
```
Cookie: JSESSIONID=xxx
```

**响应** (200 OK):
```json
[
  {
    "id": 1,
    "username": "admin",
    "department": {
      "id": 1,
      "name": "技术部"
    },
    "roles": [
      {
        "id": 1,
        "name": "ADMIN",
        "description": "管理员"
      }
    ]
  }
]
```

---

### 查询用户详情

```
GET /users/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 用户ID |

**响应** (200 OK):
```json
{
  "id": 1,
  "username": "admin",
  "department": {
    "id": 1,
    "name": "技术部"
  },
  "roles": [
    {
      "id": 1,
      "name": "ADMIN",
      "description": "管理员"
    }
  ]
}
```

---

### 创建用户

```
POST /users
```

**请求体** (Form Data):
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| departmentId | Long | 否 | 部门ID |
| roleIds | List<Long> | 否 | 角色ID列表 |

**响应** (302 Found): 重定向到 /users

**示例**:
```bash
curl -X POST http://localhost:8080/users \
  -b cookies.txt \
  -d "username=testuser&password=123456&departmentId=1&roleIds=2"
```

---

### 更新用户

```
PUT /users/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 用户ID |

**请求体** (Form Data):
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 否 | 密码（不填则不修改） |
| departmentId | Long | 否 | 部门ID |
| roleIds | List<Long> | 否 | 角色ID列表 |

**响应** (302 Found): 重定向到 /users

---

### 删除用户

```
DELETE /users/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 用户ID |

**响应** (302 Found): 重定向到 /users

---

## 🎭 角色管理

### 查询所有角色（分页）

```
GET /roles
```

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | 否 | 页码，默认 0 |
| size | Integer | 否 | 每页大小，默认 10 |

**响应** (200 OK):
```json
{
  "content": [
    {
      "id": 1,
      "name": "ADMIN",
      "description": "管理员"
    }
  ],
  "totalElements": 2,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

---

### 查询角色详情

```
GET /roles/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 角色ID |

**响应** (200 OK):
```json
{
  "id": 1,
  "name": "ADMIN",
  "description": "管理员"
}
```

---

### 创建角色

```
POST /roles
```

**请求体** (Form Data):
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 角色名 |
| description | String | 否 | 角色描述 |

**响应** (302 Found): 重定向到 /roles

**示例**:
```bash
curl -X POST http://localhost:8080/roles \
  -b cookies.txt \
  -d "name=MANAGER&description=经理"
```

---

### 更新角色

```
PUT /roles/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 角色ID |

**请求体** (Form Data):
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 角色名 |
| description | String | 否 | 角色描述 |

**响应** (302 Found): 重定向到 /roles

---

### 删除角色

```
DELETE /roles/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 角色ID |

**响应** (302 Found): 重定向到 /roles

---

### 为角色分配权限

```
POST /roles/{id}/permissions
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 角色ID |

**请求体** (Form Data):
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| permissionIds | List<Long> | 是 | 权限ID列表 |

**响应** (302 Found): 重定向到 /roles

---

## 🔑 权限管理

### 查询所有权限

```
GET /permissions
```

**响应** (200 OK):
```json
[
  {
    "id": 1,
    "name": "用户管理",
    "code": "user:manage",
    "type": "MENU",
    "parentId": null,
    "url": "/users",
    "icon": "user",
    "sort": 1
  }
]
```

---

### 创建权限

```
POST /permissions
```

**请求体** (Form Data):
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 权限名称 |
| code | String | 是 | 权限代码 |
| type | String | 是 | 类型（MENU/BUTTON） |
| parentId | Long | 否 | 父权限ID |
| url | String | 否 | 菜单URL |
| icon | String | 否 | 图标 |
| sort | Integer | 否 | 排序 |

**响应** (302 Found): 重定向到 /permissions

---

### 更新权限

```
PUT /permissions/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 权限ID |

**请求体** (Form Data): 同创建权限

**响应** (302 Found): 重定向到 /permissions

---

### 删除权限

```
DELETE /permissions/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 权限ID |

**响应** (302 Found): 重定向到 /permissions

---

## 🏢 部门管理

### 查询所有部门

```
GET /departments
```

**响应** (200 OK):
```json
[
  {
    "id": 1,
    "name": "技术部"
  },
  {
    "id": 2,
    "name": "市场部"
  }
]
```

---

### 创建部门

```
POST /departments
```

**请求体** (Form Data):
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 部门名称 |

**响应** (302 Found): 重定向到 /departments

---

### 更新部门

```
PUT /departments/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 部门ID |

**请求体** (Form Data):
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 部门名称 |

**响应** (302 Found): 重定向到 /departments

---

### 删除部门

```
DELETE /departments/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 部门ID |

**响应** (302 Found): 重定向到 /departments

---

## 📢 公告管理

### 查询所有公告

```
GET /announcements
```

**响应** (200 OK):
```json
[
  {
    "id": 1,
    "title": "系统维护通知",
    "content": "系统将于今晚进行维护...",
    "type": "NOTICE",
    "status": "PUBLISHED",
    "createdAt": "2026-05-05T10:00:00",
    "updatedAt": "2026-05-05T10:00:00"
  }
]
```

---

### 查询公告详情

```
GET /announcements/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 公告ID |

**响应** (200 OK): 公告详情对象

---

### 创建公告

```
POST /announcements
```

**请求体** (Form Data):
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | String | 是 | 公告标题 |
| content | String | 是 | 公告内容 |
| type | String | 是 | 类型（NOTICE/INFO等） |
| status | String | 是 | 状态（DRAFT/PUBLISHED/ARCHIVED） |

**响应** (302 Found): 重定向到 /announcements

---

### 更新公告

```
PUT /announcements/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 公告ID |

**请求体** (Form Data): 同创建公告

**响应** (302 Found): 重定向到 /announcements

---

### 删除公告

```
DELETE /announcements/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 公告ID |

**响应** (302 Found): 重定向到 /announcements

---

## ⚙️ 配置管理

### 查询所有配置

```
GET /configs
```

**响应** (200 OK):
```json
[
  {
    "id": 1,
    "configKey": "system.name",
    "configValue": "Admin System",
    "description": "系统名称"
  }
]
```

---

### 创建配置

```
POST /configs
```

**请求体** (Form Data):
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| configKey | String | 是 | 配置键 |
| configValue | String | 是 | 配置值 |
| description | String | 否 | 配置描述 |

**响应** (302 Found): 重定向到 /configs

---

### 更新配置

```
PUT /configs/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 配置ID |

**请求体** (Form Data): 同创建配置

**响应** (302 Found): 重定向到 /configs

---

### 删除配置

```
DELETE /configs/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 配置ID |

**响应** (302 Found): 重定向到 /configs

---

## 📖 字典管理

### 查询所有字典

```
GET /dictionaries
```

**响应** (200 OK):
```json
[
  {
    "id": 1,
    "type": "USER_STATUS",
    "code": "ACTIVE",
    "label": "激活",
    "value": "1",
    "sort": 1,
    "status": "ACTIVE"
  }
]
```

---

### 创建字典

```
POST /dictionaries
```

**请求体** (Form Data):
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | String | 是 | 字典类型 |
| code | String | 是 | 字典代码 |
| label | String | 是 | 显示标签 |
| value | String | 是 | 字典值 |
| sort | Integer | 否 | 排序 |
| status | String | 是 | 状态 |

**响应** (302 Found): 重定向到 /dictionaries

---

### 更新字典

```
PUT /dictionaries/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 字典ID |

**请求体** (Form Data): 同创建字典

**响应** (302 Found): 重定向到 /dictionaries

---

### 删除字典

```
DELETE /dictionaries/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 字典ID |

**响应** (302 Found): 重定向到 /dictionaries

---

## 📝 操作日志

### 查询操作日志（分页）

```
GET /operation-logs
```

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | 否 | 页码，默认 0 |
| size | Integer | 否 | 每页大小，默认 10 |

**响应** (200 OK):
```json
{
  "content": [
    {
      "id": 1,
      "username": "admin",
      "operation": "创建用户",
      "method": "POST",
      "params": "username=testuser",
      "ip": "127.0.0.1",
      "createTime": "2026-05-05T10:30:00"
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "size": 10,
  "number": 0
}
```

---

## 🔄 工作流管理

### 部署流程

```
POST /workflow/deploy
```

**请求体** (Multipart Form Data):
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | BPMN 文件 |
| name | String | 是 | 部署名称 |

**响应** (302 Found): 重定向到工作流列表

**示例**:
```bash
curl -X POST http://localhost:8080/workflow/deploy \
  -b cookies.txt \
  -F "file=@leave-process.bpmn20.xml" \
  -F "name=请假流程"
```

---

### 查询流程定义

```
GET /workflow/definitions
```

**响应** (200 OK):
```json
[
  {
    "id": "leave-process:1:12345",
    "key": "leave-process",
    "name": "请假流程",
    "version": 1,
    "resourceName": "leave-process.bpmn20.xml"
  }
]
```

---

### 启动流程实例

```
POST /workflow/start
```

**请求体** (Form Data):
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| processDefinitionKey | String | 是 | 流程定义Key |
| businessKey | String | 否 | 业务Key |
| variables | Map | 否 | 流程变量（JSON格式） |

**响应** (302 Found): 重定向到流程实例列表

**示例**:
```bash
curl -X POST http://localhost:8080/workflow/start \
  -b cookies.txt \
  -d "processDefinitionKey=leave-process" \
  -d "businessKey=LEAVE-001" \
  -d 'variables={"employeeName":"张三","leaveType":"annual","days":5}'
```

---

### 查询用户任务

```
GET /workflow/tasks
```

**响应** (200 OK):
```json
[
  {
    "id": "task-123",
    "name": "部门经理审批",
    "assignee": "manager",
    "processInstanceId": "proc-456",
    "createTime": "2026-05-05T10:00:00"
  }
]
```

---

### 查询任务详情

```
GET /workflow/tasks/{taskId}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| taskId | String | 任务ID |

**响应** (200 OK): 任务详情对象

---

### 完成任务

```
POST /workflow/complete/{taskId}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| taskId | String | 任务ID |

**请求体** (Form Data):
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| approved | Boolean | 否 | 是否批准 |
| comment | String | 否 | 审批意见 |
| variables | Map | 否 | 任务变量 |

**响应** (302 Found): 重定向到任务列表

**示例**:
```bash
curl -X POST http://localhost:8080/workflow/complete/task-123 \
  -b cookies.txt \
  -d "approved=true" \
  -d "comment=同意"
```

---

### 查询流程实例

```
GET /workflow/instances
```

**响应** (200 OK):
```json
[
  {
    "id": "proc-456",
    "name": "张三的年假申请",
    "processDefinitionId": "leave-process:1:12345",
    "businessKey": "LEAVE-001",
    "status": "RUNNING",
    "startedBy": "zhangsan",
    "startedTime": "2026-05-05T10:00:00"
  }
]
```

---

### 查询运行中的流程实例

```
GET /workflow/instances/running
```

**响应** (200 OK): 运行中的流程实例列表

---

### 查询已完成的流程实例

```
GET /workflow/instances/completed
```

**响应** (200 OK): 已完成的流程实例列表

---

### 根据用户查询流程实例

```
GET /workflow/instances/user/{username}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| username | String | 用户名 |

**响应** (200 OK): 该用户发起的流程实例列表

---

## 📊 仪表板

### 获取仪表板数据

```
GET /dashboard
```

**响应** (200 OK): 返回 dashboard.html 页面

---

## 🔍 通用响应格式

### 成功响应

大多数成功操作会返回 302 重定向或 200 OK 带数据。

### 错误响应

```json
{
  "timestamp": "2026-05-05T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 999",
  "path": "/users/999"
}
```

### 常见 HTTP 状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 302 | 重定向 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 🔒 安全注意事项

1. **所有接口都需要认证**（除了 /login）
2. **使用 HTTPS** 在生产环境中
3. **CSRF Token** 需要在表单中包含
4. **Session 超时** 默认 30 分钟
5. **密码加密** 使用 BCrypt

---

## 📝 使用示例

### JavaScript (Fetch API)

```javascript
// 登录
fetch('/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded',
  },
  body: 'username=admin&password=admin123',
  credentials: 'include'
})
.then(response => {
  if (response.redirected) {
    window.location.href = response.url;
  }
});

// 查询用户列表
fetch('/users', {
  method: 'GET',
  credentials: 'include'
})
.then(response => response.json())
.then(users => console.log(users));

// 创建用户
fetch('/users', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded',
  },
  body: 'username=newuser&password=123456&departmentId=1',
  credentials: 'include'
})
.then(response => {
  if (response.redirected) {
    window.location.href = response.url;
  }
});
```

### Python (Requests)

```python
import requests

# 创建会话
session = requests.Session()

# 登录
session.post('http://localhost:8080/login', data={
    'username': 'admin',
    'password': 'admin123'
})

# 查询用户列表
response = session.get('http://localhost:8080/users')
users = response.json()
print(users)

# 创建用户
session.post('http://localhost:8080/users', data={
    'username': 'newuser',
    'password': '123456',
    'departmentId': 1
})
```

---

## 📚 参考资料

- [RESTful API 设计指南](https://restfulapi.net/)
- [HTTP 状态码详解](https://httpstatuses.com/)
- [Spring MVC 文档](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc)

---

**文档版本**: 1.0  
**最后更新**: 2026-05-05  
**维护者**: Admin System 开发团队
