package com.labelcode;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 标签管理系统主应用类
 * 基于Directus设计思想构建
 */
@SpringBootApplication
@MapperScan("com.labelcode.mapper")
public class LabelCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(LabelCodeApplication.class, args);
    }
}
