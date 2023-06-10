package com.heartsuit.infrastructure.injector.methods;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import com.heartsuit.infrastructure.injector.enums.SqlMethodDiv;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * @author liuxiansong
 * @date 2021-05-20
 * @desc 通过MybatisPlus源码 扩展修改
 */
public class UpdateClickHouse extends AbstractMethod {

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlMethodDiv sqlMethod = SqlMethodDiv.UPDATE;
        String sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(),
                sqlSet(true, true, tableInfo, true, ENTITY, ENTITY_DOT),
                sqlWhereEntityWrapper(true, tableInfo), sqlComment());
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
    }


    /**
     * SQL 更新 set 语句
     *
     * @param logic  是否逻辑删除注入器
     * @param ew     是否存在 UpdateWrapper 条件
     * @param table  表信息
     * @param alias  别名
     * @param prefix 前缀
     * @return sql
     */
    @Override
    protected String sqlSet(boolean logic, boolean ew, TableInfo table, boolean judgeAliasNull, final String alias,
                            final String prefix) {
        String sqlScript = table.getAllSqlSet(logic, prefix);
        if (judgeAliasNull) {
            sqlScript = SqlScriptUtils.convertIf(sqlScript, String.format("%s != null", alias), true);
        }
        if (ew) {
            sqlScript += NEWLINE;
            sqlScript += SqlScriptUtils.convertIf(SqlScriptUtils.unSafeParam(U_WRAPPER_SQL_SET),
                    String.format("%s != null and %s != null", WRAPPER, U_WRAPPER_SQL_SET), false);
        }
        sqlScript = convertSet(sqlScript);
        return sqlScript;
    }

    /**
     * 去掉sest 和 suffixOverrides=","代表去掉第一个逗号
     *
     * @param sqlScript
     * @return
     */
    public static String convertSet(final String sqlScript) {
        return "<trim prefix=\"\" suffixOverrides=\",\"> " + sqlScript + "\n" + "</trim>";
    }
}
