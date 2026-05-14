package com.labelcode.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 数据库初始化器
 * 使用 Spring 的 ResourceDatabasePopulator 确保脚本正确执行，包括处理复杂的触发器函数
 */
@Component
@Order(1)
public class DatabaseInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // 检查标签库表是否已存在，作为数据库是否初始化的特征
            Boolean hasTables = jdbcTemplate.queryForObject(
                    "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'label_library')",
                    Boolean.class
            );

            if (hasTables == null || !hasTables) {
                System.out.println("检测到数据库尚未初始化，开始执行脚本...");
                initializeDatabase();
            } else {
                System.out.println("数据库核心表已存在，跳过初始化流程。");
            }
        } catch (Exception e) {
            System.err.println("初始化检查过程中出现异常，尝试继续执行（可能由于部分表不存在导致）: " + e.getMessage());
            initializeDatabase();
        }
    }

    /**
     * 使用 ResourceDatabasePopulator 按顺序初始化数据库
     */
    private void initializeDatabase() {
        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("db/init-schema.sql"));
            // 如果您有测试数据，也可以在这里添加
             // populator.addScript(new ClassPathResource("test-data.sql"));
            
            populator.setContinueOnError(true); // 即使部分 SQL（如已存在的 index）失败也继续
            populator.setIgnoreFailedDrops(true);
            populator.setSqlScriptEncoding("UTF-8");
            
            populator.execute(dataSource);
            
            System.out.println(">>> 数据库初始化及测试数据构建成功！");
        } catch (Exception e) {
            System.err.println("!!! 数据库脚本执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
