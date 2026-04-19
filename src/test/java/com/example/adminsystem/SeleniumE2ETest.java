package com.example.adminsystem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("真实浏览器端到端测试")
class SeleniumE2ETest extends BaseSeleniumTest {

    @Test
    @DisplayName("测试登录页面加载")
    void testLoginPageLoad() {
        navigateTo("/login");
        
        assertThat(driver.getTitle()).contains("登录");
        
        WebElement usernameInput = driver.findElement(By.name("username"));
        WebElement passwordInput = driver.findElement(By.name("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        
        assertThat(usernameInput.isDisplayed()).isTrue();
        assertThat(passwordInput.isDisplayed()).isTrue();
        assertThat(loginButton.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("测试使用正确凭据登录")
    void testLoginWithValidCredentials() {
        navigateTo("/login");
        
        WebElement usernameInput = driver.findElement(By.name("username"));
        WebElement passwordInput = driver.findElement(By.name("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        
        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("123456");
        loginButton.click();
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        
        assertThat(driver.getCurrentUrl()).contains("/dashboard");
        assertThat(driver.getPageSource()).contains("后台管理系统");
    }

    @Test
    @DisplayName("测试使用错误凭据登录")
    void testLoginWithInvalidCredentials() {
        navigateTo("/login");
        
        WebElement usernameInput = driver.findElement(By.name("username"));
        WebElement passwordInput = driver.findElement(By.name("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        
        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("wrongpassword");
        loginButton.click();
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/login?error"));
        
        assertThat(driver.getCurrentUrl()).contains("/login?error");
    }

    @Test
    @DisplayName("测试未登录访问受保护页面")
    void testAccessProtectedPageWithoutLogin() {
        navigateTo("/users");
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/login"));
        
        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @DisplayName("测试Dashboard页面内容")
    void testDashboardContent() {
        loginAsAdmin();
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        
        assertThat(driver.getPageSource()).contains("后台管理系统");
        assertThat(driver.getPageSource()).contains("用户管理");
        assertThat(driver.getPageSource()).contains("部门管理");
    }

    @Test
    @DisplayName("测试导航菜单")
    void testNavigationMenu() {
        loginAsAdmin();
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        
        WebElement userMenuLink = driver.findElement(By.linkText("用户管理"));
        userMenuLink.click();
        wait.until(ExpectedConditions.urlContains("/users"));
        assertThat(driver.getCurrentUrl()).contains("/users");
        
        WebElement deptMenuLink = driver.findElement(By.linkText("部门管理"));
        deptMenuLink.click();
        wait.until(ExpectedConditions.urlContains("/departments"));
        assertThat(driver.getCurrentUrl()).contains("/departments");
        
        WebElement dashboardLink = driver.findElement(By.linkText("后台管理系统"));
        dashboardLink.click();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertThat(driver.getCurrentUrl()).contains("/dashboard");
    }

    @Test
    @DisplayName("测试登出功能")
    void testLogout() {
        loginAsAdmin();
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        
        WebElement logoutButton = driver.findElement(By.cssSelector("button[type='submit']"));
        logoutButton.click();
        
        wait.until(ExpectedConditions.urlContains("/login"));
        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @DisplayName("测试用户列表显示")
    void testUserListDisplay() {
        loginAsAdmin();
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        
        WebElement userMenuLink = driver.findElement(By.linkText("用户管理"));
        userMenuLink.click();
        
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table tbody tr")));
        
        java.util.List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
        assertThat(rows.size()).isGreaterThanOrEqualTo(1);
        
        assertThat(driver.getPageSource()).contains("admin");
    }

    @Test
    @DisplayName("测试部门列表显示")
    void testDepartmentListDisplay() {
        loginAsAdmin();
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        
        WebElement deptMenuLink = driver.findElement(By.linkText("部门管理"));
        deptMenuLink.click();
        
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table tbody tr")));
        
        java.util.List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
        assertThat(rows.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("测试角色管理修改功能")
    void testRoleUpdate() {
        loginAsAdmin();
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        
        // 导航到权限管理 -> 角色管理
        WebElement permissionMenuLink = driver.findElement(By.linkText("权限管理"));
        permissionMenuLink.click();
        
        WebElement roleMenuLink = driver.findElement(By.linkText("角色管理"));
        roleMenuLink.click();
        
        wait.until(ExpectedConditions.urlContains("/role/list"));
        
        // 找到第一个角色的编辑按钮并点击
        WebElement editButton = driver.findElement(By.cssSelector("a.btn-info"));
        editButton.click();
        
        wait.until(ExpectedConditions.urlContains("/role/form"));
        
        // 修改角色描述
        WebElement descriptionInput = driver.findElement(By.name("description"));
        String newDescription = "测试修改后的角色描述 " + System.currentTimeMillis();
        descriptionInput.clear();
        descriptionInput.sendKeys(newDescription);
        
        // 保存修改
        WebElement saveButton = driver.findElement(By.cssSelector("button[type='submit']"));
        saveButton.click();
        
        wait.until(ExpectedConditions.urlContains("/role/list"));
        
        // 验证修改是否成功
        assertThat(driver.getPageSource()).contains(newDescription);
    }

    private void loginAsAdmin() {
        navigateTo("/login");
        
        WebElement usernameInput = driver.findElement(By.name("username"));
        WebElement passwordInput = driver.findElement(By.name("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        
        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("123456");
        loginButton.click();
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }
}
