package com.quantx.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class DatabaseConnectionDTO {

    @NotBlank(message = "数据源名称不能为空")
    @Size(min = 1, max = 50, message = "数据源名称长度必须在1到50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "数据源名称只能包含字母、数字、下划线和连字符")
    private String name;

    @NotBlank(message = "数据库URL不能为空")
    private String url;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "驱动类名不能为空")
    private String driverClassName;

    // 可选字段，用于更高级的配置
    private Integer minIdle;
    private Integer maxPoolSize;
    private Long connectionTimeout;
    private Long idleTimeout;
    private Long maxLifetime;

    // 可选的连接测试查询
    private String validationQuery;

    // 是否自动提交
    private Boolean autoCommit;

    // 是否只读连接
    private Boolean readOnly;

    // 连接池名称
    @Size(max = 50, message = "连接池名称长度不能超过50个字符")
    private String poolName;

    // 初始化大小
    private Integer initialSize;

    // 是否在连接池启动时测试连接
    private Boolean testOnCreate;

    // 其他可能需要的字段...

    // 你可以根据需要添加自定义的getter和setter方法
    // 例如，如果你需要对某些字段进行特殊处理

    public void setUrl(String url) {
        this.url = url.trim(); // 移除可能的前后空格
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName.trim(); // 移除可能的前后空格
    }

    // 可以添加一个方法来验证URL格式
    public boolean isValidUrl() {
        // 这里可以添加更复杂的URL验证逻辑
        return this.url != null && this.url.startsWith("jdbc:");
    }
}