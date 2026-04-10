package com.example.adminsystem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SuppressWarnings("null")
class E2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("访问登录页面")
    void testAccessLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("登录")));
    }

    @Test
    @DisplayName("使用正确的凭据登录")
    void testLoginWithValidCredentials() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "123456")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    @DisplayName("使用错误的凭据登录")
    void testLoginWithInvalidCredentials() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "wrongpassword")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    @DisplayName("未认证用户访问受保护页面会被重定向到登录页")
    void testUnauthenticatedUserAccessProtectedPage() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/departments"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("访问Dashboard页面")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAccessDashboard() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("后台管理系统")));
    }

    @Test
    @DisplayName("用户管理 - 查看用户列表")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testViewUserList() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("用户管理")));
    }

    @Test
    @DisplayName("用户管理 - 访问添加用户页面")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAccessAddUserPage() throws Exception {
        mockMvc.perform(get("/users/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/form"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("添加用户")));
    }

    @Test
    @DisplayName("用户管理 - 添加新用户")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddNewUser() throws Exception {
        mockMvc.perform(post("/users")
                        .param("username", "testuser")
                        .param("password", "test123")
                        .param("role", "USER")
                        .param("departmentId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("testuser")));
    }

    @Test
    @DisplayName("用户管理 - 访问编辑用户页面")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAccessEditUserPage() throws Exception {
        mockMvc.perform(get("/users/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/form"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("编辑用户")));
    }

    @Test
    @DisplayName("用户管理 - 删除用户")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteUser() throws Exception {
        mockMvc.perform(get("/users/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    @DisplayName("部门管理 - 查看部门列表")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testViewDepartmentList() throws Exception {
        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(view().name("department/list"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("部门管理")));
    }

    @Test
    @DisplayName("部门管理 - 访问添加部门页面")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAccessAddDepartmentPage() throws Exception {
        mockMvc.perform(get("/departments/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("department/form"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("添加部门")));
    }

    @Test
    @DisplayName("部门管理 - 添加新部门")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddNewDepartment() throws Exception {
        mockMvc.perform(post("/departments")
                        .param("name", "测试部门")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/departments"));

        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("测试部门")));
    }

    @Test
    @DisplayName("部门管理 - 访问编辑部门页面")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAccessEditDepartmentPage() throws Exception {
        mockMvc.perform(get("/departments/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("department/form"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("编辑部门")));
    }

    @Test
    @DisplayName("部门管理 - 删除部门")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteDepartment() throws Exception {
        mockMvc.perform(get("/departments/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/departments"));
    }

    @Test
    @DisplayName("完整的用户管理流程")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCompleteUserManagementFlow() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users")
                        .param("username", "newuser")
                        .param("password", "password123")
                        .param("role", "USER")
                        .param("departmentId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("newuser")));
    }

    @Test
    @DisplayName("完整的部门管理流程")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCompleteDepartmentManagementFlow() throws Exception {
        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/departments")
                        .param("name", "新部门")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/departments"));

        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("新部门")));
    }

    @Test
    @DisplayName("登出功能")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testLogout() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
