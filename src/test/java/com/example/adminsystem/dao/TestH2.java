package com.example.adminsystem.dao;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

public class TestH2 {

    @Test
    public void testH2() {
        System.out.println("H2测试成功！");
         // H2 内存数据库的连接 URL
        String url = "jdbc:h2:mem:testdb"; 
        String user = "sa";
        String password = "hhhh";

        try {
            // 1. 加载驱动 (JDBC 4.0+ 可自动加载，但显式声明更稳妥)
            Class.forName("org.h2.Driver");

            // 2. 建立连接
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("成功连接到 H2 数据库！");

            // 3. 创建表
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY, name VARCHAR(50))");

            // 4. 插入数据
            stmt.execute("INSERT INTO users VALUES (1, 'Admin')");

            // 5. 查询数据
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name"));
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name"));
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name"));
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name"));
            }

            // 6. 关闭资源
            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
