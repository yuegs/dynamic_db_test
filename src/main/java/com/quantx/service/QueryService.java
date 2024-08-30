package com.quantx.service;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.quantx.dto.DatabaseConnectionDTO;
import com.quantx.dto.QueryRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class QueryService {

    private static final Logger logger = LoggerFactory.getLogger(QueryService.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void addDataSource(DatabaseConnectionDTO connectionDTO) {
        logger.info("Adding new datasource: {}", connectionDTO.getName());
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        DataSource newDataSource = createDataSource(connectionDTO);
        ds.addDataSource(connectionDTO.getName(), newDataSource);
        logger.info("Datasource added successfully: {}", connectionDTO.getName());
    }

    private DataSource createDataSource(DatabaseConnectionDTO connectionDTO) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(connectionDTO.getDriverClassName());
        dataSource.setUrl(connectionDTO.getUrl());
        dataSource.setUsername(connectionDTO.getUsername());
        dataSource.setPassword(connectionDTO.getPassword());
        return dataSource;
    }

    public List<Map<String, Object>> executeQuery(QueryRequestDTO queryRequest) {
        String datasourceName = queryRequest.getDatasourceName();
        logger.info("Executing query on datasource: {}", datasourceName);
        DynamicDataSourceContextHolder.push(datasourceName);
        try {
            String sql = queryRequest.getSql();
            logger.info("SQL query: {}", sql);
            return jdbcTemplate.queryForList(sql);
        } finally {
            DynamicDataSourceContextHolder.poll();
            logger.info("Finished executing query on datasource: {}", datasourceName);
        }
    }

    public Set<String> getAllDataSourceNames() {
        logger.info("Fetching all datasource names");
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        Set<String> dataSourceNames = ds.getDataSources().keySet();
        logger.info("Found {} datasources", dataSourceNames.size());
        return dataSourceNames;
    }

    public boolean removeDataSource(String name) {
        logger.info("Attempting to remove datasource: {}", name);
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        if (ds.getDataSources().containsKey(name)) {
            ds.removeDataSource(name);
            logger.info("Datasource removed successfully: {}", name);
            return true;
        }
        logger.warn("Datasource not found for removal: {}", name);
        return false;
    }

    public boolean testConnection(String name) {
        logger.info("Testing connection for datasource: {}", name);
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        if (ds.getDataSources().containsKey(name)) {
            DynamicDataSourceContextHolder.push(name);
            try (Connection conn = ds.getConnection()) {
                boolean isValid = conn.isValid(5); // 5 seconds timeout
                logger.info("Connection test result for {}: {}", name, isValid ? "Success" : "Failed");
                return isValid;
            } catch (SQLException e) {
                logger.error("Error testing connection for datasource: {}", name, e);
                return false;
            } finally {
                DynamicDataSourceContextHolder.poll();
            }
        }
        logger.warn("Datasource not found for connection test: {}", name);
        return false;
    }
}