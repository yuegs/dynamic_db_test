package com.quantx.config;// DynamicDataSourceConfig.java

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.provider.AbstractDataSourceProvider;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 动态数据源配置类
 * 这个类负责配置和管理动态数据源，包括从配置文件加载数据源信息和创建数据源对象
 */
@Configuration
public class DynamicDataSourceConfig {

    @Autowired
    private DynamicDataSourceProperties properties;

    /**
     * 创建动态数据源提供者
     *
     * @return 动态数据源提供者
     */
    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider() {
        return new AbstractDataSourceProvider() {
            @Override
            public Map<String, DataSource> loadDataSources() {
                return createDataSourceMap(properties.getDatasource());
            }
        };
    }

    /**
     * 创建主数据源
     * 这个方法创建一个 DynamicRoutingDataSource 对象，它是实际的动态数据源
     *
     * @return 动态路由数据源
     */
    @Primary
    @Bean
    public DataSource dataSource() {
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();

        Map<String, DataSourceProperty> dataSourcePropertiesMap = properties.getDatasource();

        // 设置主数据源
        String primary = properties.getPrimary();
        dataSource.setPrimary(primary);

        // 添加所有数据源
        for (Map.Entry<String, DataSourceProperty> entry : dataSourcePropertiesMap.entrySet()) {
            String name = entry.getKey();
            DataSource ds = createDataSource(entry.getValue());
            dataSource.addDataSource(name, ds);
        }

        return dataSource;
    }

    /**
     * 根据配置属性创建数据源映射
     *
     * @param propertiesMap 数据源配置属性映射
     * @return 数据源对象映射
     */
    private Map<String, DataSource> createDataSourceMap(Map<String, DataSourceProperty> propertiesMap) {
        return propertiesMap.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> createDataSource(entry.getValue())
                ));
    }


    /**
     * 根据单个数据源配置属性创建数据源对象
     *
     * @param property 数据源配置属性
     * @return 数据源对象
     */
    private DataSource createDataSource(DataSourceProperty property) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(property.getDriverClassName());
        dataSource.setUrl(property.getUrl());
        dataSource.setUsername(property.getUsername());
        dataSource.setPassword(property.getPassword());
        return dataSource;
    }
}