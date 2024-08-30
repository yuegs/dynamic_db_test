package com.quantx.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.provider.AbstractDataSourceProvider;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * 动态数据源配置类
 * 这个类负责配置和管理动态数据源，包括从配置文件加载数据源信息和创建数据源对象
 */
@Configuration
public class DynamicDataSourceConfig {

    @Autowired
    private DynamicDataSourceProperties properties;

    @Autowired
    private DefaultDataSourceCreator dataSourceCreator;

    /**
     * 创建动态数据源提供者
     *
     * @return 动态数据源提供者
     */
    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider() {
        return new AbstractDataSourceProvider(dataSourceCreator) {
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
    public DataSource dataSource(List<DynamicDataSourceProvider> providers) {
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource(providers);
        dataSource.setPrimary(properties.getPrimary());
        dataSource.setStrict(properties.getStrict());
        dataSource.setStrategy(properties.getStrategy());
        dataSource.setP6spy(properties.getP6spy());
        dataSource.setSeata(properties.getSeata());
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
                        entry -> dataSourceCreator.createDataSource(entry.getValue())
                ));
    }
}