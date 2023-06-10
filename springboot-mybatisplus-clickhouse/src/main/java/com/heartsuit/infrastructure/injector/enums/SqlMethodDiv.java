package com.heartsuit.infrastructure.injector.enums;

public enum SqlMethodDiv {

    /**
     * 删除
     */
    DELETE_BY_ID("deleteByIdClickHouse", "根据ID 删除一条数据", "<script>\nALTER TABLE %s DELETE WHERE %s=#{%s}\n</script>"),

    /**
     * 逻辑删除
     */
    LOGIC_DELETE_BY_ID("deleteByIdClickHouse", "根据ID 逻辑删除一条数据", "<script>\nALTER TABLE %s UPDATE %s where %s=#{%s} %s\n</script>"),

    /**
     * 修改 条件主键
     */
    UPDATE_BY_ID("updateByIdClickHouse", "根据ID 选择修改数据", "<script>\nALTER TABLE %s UPDATE %s where %s=#{%s} %s\n</script>"),
    /**
     * 修改 条件主键
     */
    UPDATE("updateClickHouse", "根据 whereEntity 条件，更新记录", "<script>\nALTER TABLE %s UPDATE  %s %s %s\n</script>");

    private final String method;
    private final String desc;
    private final String sql;

    SqlMethodDiv(String method, String desc, String sql) {
        this.method = method;
        this.desc = desc;
        this.sql = sql;
    }

    public String getMethod() {
        return method;
    }

    public String getDesc() {
        return desc;
    }

    public String getSql() {
        return sql;
    }
}
