package com.example.adminsystem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseSeleniumTest {

    protected WebDriver driver;

    @LocalServerPort
    protected int port;

    protected String baseUrl;

    @BeforeEach
    void setupTest() {
        WebDriverManager.chromedriver().setup();
        
        // 创建项目内的目录作为Chrome的临时目录
        String tempDir = System.getProperty("user.dir") + "/target/chrome-temp";
        String userDataDir = System.getProperty("user.dir") + "/target/chrome-user-data";
        
        // 设置系统属性，让Chrome使用项目内的临时目录
        System.setProperty("java.io.tmpdir", tempDir);
        
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless"); // 无头模式运行
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-gpu");
        options.addArguments("--start-maximized"); // 最大化模式
        options.addArguments("--user-data-dir=" + userDataDir);
        options.addArguments("--temp-dir=" + tempDir);
        
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void navigateTo(String path) {
        driver.get(baseUrl + path);
    }

    protected void waitFor(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
