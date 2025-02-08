package com.frog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.*;

/**
 * 启动程序
 *
 * @author ruoyi
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableScheduling
//@MapperScan(basePackages = "com.frog.IaAgriculture.mapper")  // MyBatis-Plus Mapper
//@ComponentScan(basePackages = {"com.frog.IaAgriculture", "com.frog.framework.web"})
public class FrogApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(FrogApplication.class, args);
        InputStream inputStream = ResourceReader.class.getClassLoader().getResourceAsStream("logo.txt");
        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}