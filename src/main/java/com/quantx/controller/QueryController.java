package com.quantx.controller;

import com.quantx.dto.DatabaseConnectionDTO;
import com.quantx.dto.QueryRequestDTO;
import com.quantx.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/query")
public class QueryController {

    private static final Logger logger = LoggerFactory.getLogger(QueryController.class);

    @Autowired
    private QueryService queryService;

    @PostMapping("/add-datasource")
    public ResponseEntity<?> addDataSource( @RequestBody DatabaseConnectionDTO connectionDTO) {
        try {
            logger.info("Adding new datasource: {}", connectionDTO.getName());
            queryService.addDataSource(connectionDTO);
            return ResponseEntity.ok("Data source added successfully: " + connectionDTO.getName());
        } catch (Exception e) {
            logger.error("Error adding datasource: {}", connectionDTO.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding datasource: " + e.getMessage());
        }
    }

    @PostMapping("/execute")
    public ResponseEntity<?> executeQuery( @RequestBody QueryRequestDTO queryRequest) {
        try {
            logger.info("Executing query on datasource: {}", queryRequest.getDatasourceName());
            List<Map<String, Object>> result = queryService.executeQuery(queryRequest);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error executing query on datasource: {}", queryRequest.getDatasourceName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error executing query: " + e.getMessage());
        }
    }

    @GetMapping("/datasources")
    public ResponseEntity<Set<String>> getDataSources() {
        try {
            logger.info("Fetching list of all datasources");
            Set<String> dataSources = queryService.getAllDataSourceNames();
            return ResponseEntity.ok(dataSources);
        } catch (Exception e) {
            logger.error("Error fetching datasource list", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/datasource/{name}")
    public ResponseEntity<?> removeDataSource(@PathVariable String name) {
        try {
            logger.info("Removing datasource: {}", name);
            boolean removed = queryService.removeDataSource(name);
            if (removed) {
                return ResponseEntity.ok("Data source removed successfully: " + name);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Data source not found: " + name);
            }
        } catch (Exception e) {
            logger.error("Error removing datasource: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error removing datasource: " + e.getMessage());
        }
    }

    @GetMapping("/test-connection/{name}")
    public ResponseEntity<?> testConnection(@PathVariable String name) {
        try {
            logger.info("Testing connection for datasource: {}", name);
            boolean isConnected = queryService.testConnection(name);
            if (isConnected) {
                return ResponseEntity.ok("Connection successful for datasource: " + name);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Connection failed for datasource: " + name);
            }
        } catch (Exception e) {
            logger.error("Error testing connection for datasource: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error testing connection: " + e.getMessage());
        }
    }
}