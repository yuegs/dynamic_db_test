package com.quantx.dto;

import lombok.Data;

@Data
public class QueryRequestDTO {

    private String datasourceName;

    private String sql;

    private Integer timeout;

    // 可选：添加分页参数
    private Integer pageSize;
    private Integer pageNumber;

    // 可选：添加排序参数
    private String sortColumn;
    private String sortOrder; // "ASC" 或 "DESC"
}