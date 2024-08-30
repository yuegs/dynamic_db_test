package com.quantx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableTransactionManagement
@EnableAspectJAutoProxy
public class MultiDatasourceQueryApplication {

    private static final Logger logger = LoggerFactory.getLogger(MultiDatasourceQueryApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MultiDatasourceQueryApplication.class, args);
        logger.info("MultiDatasourceQueryApplication started successfully.");
    }
}