package com.mysterin.sakura.service.impl;

import com.google.common.base.Joiner;
import com.mysterin.sakura.datasource.JdbcTemplates;
import com.mysterin.sakura.exception.SakuraException;
import com.mysterin.sakura.model.*;
import com.mysterin.sakura.response.Code;
import com.mysterin.sakura.response.Page;
import com.mysterin.sakura.service.DatabaseService;
import com.mysterin.sakura.service.TableService;
import org.hibernate.mapping.Join;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * @author linxb
 */
@Service
public class TableServiceImpl implements TableService {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private JdbcTemplates jdbcTemplates;

    /**
     * 获取表名字
     * @param id
     * @return
     * @throws SakuraException
     */
    @Override
    public List<TableModel> getTableList(Long id) throws SakuraException {
        DatabaseModel databaseModel = getDatabaseModel(id);
        JdbcTemplate jdbcTemplate = jdbcTemplates.getJdbcTemplate(databaseModel);
        String sql = selectTables();
        List<TableModel> tables = jdbcTemplate.query(sql, new Object[]{databaseModel.getName()}, new TableRowMapper());
        return tables;
    }

    /**
     * 获取表字段
     * @param id
     * @param tableName
     * @return
     * @throws SakuraException
     */
    @Override
    public List<FieldModel> getTableFieldList(Long id, String tableName) throws SakuraException {
        DatabaseModel databaseModel = getDatabaseModel(id);
        String dbName = databaseModel.getName();
        String sql = selectFields();
        JdbcTemplate jdbcTemplate = jdbcTemplates.getJdbcTemplate(databaseModel);
        List<FieldModel> fields = jdbcTemplate.query(sql, new Object[]{dbName, tableName}, new FieldRowMapper());
        return fields;
    }

    /**
     * 读取表数据
     * @param page
     * @param dbId
     * @param tableName
     * @return
     * @throws SakuraException
     */
    @Override
    public Page<Map<String, Object>> getData(Page page, Long dbId, String tableName) throws SakuraException {
        List<FieldModel> fields = getTableFieldList(dbId, tableName);
        String searcheCondition = searchCondition(page.getSearch(), fields);

        DatabaseModel databaseModel = getDatabaseModel(dbId);
        String sql = selectPage(tableName, fields, searcheCondition);
        String countSql = selectCount(tableName, searcheCondition);

        JdbcTemplate jdbcTemplate = jdbcTemplates.getJdbcTemplate(databaseModel);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[]{page.getOffset(), page.getLimit()});
        int total = jdbcTemplate.queryForObject(countSql, Integer.class);

        page.setRows(rows);
        page.setTotal(total);
        return page;
    }

    @Override
    public void insertTableData(Long id, String tableName, Map<String, String> data) throws SakuraException {
        List<FieldModel> fields = getTableFieldList(id, tableName);
        String sql = insertTableSql(tableName, fields);
        JdbcTemplate jdbcTemplate = getJdbcTemplate(id);
        jdbcTemplate.update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                for (int i = 0; i < fields.size(); i++) {
                    FieldModel field = fields.get(i);
                    String columnName = field.getColumnName();
                    boolean nullable = field.isNullable();
                    String value = data.get(columnName);
                    if (nullable && StringUtils.isEmpty(value)) {
                        value = null;
                    }
                    ps.setString(i+1, value);
                }
            }
        });
    }

    @Override
    public void updateTableList(Long id, String tableName, List<Map<String, String>> data) throws SakuraException {
        List<FieldModel> fields = getTableFieldList(id, tableName);
        String sql = updateTableSql(tableName, fields);
        JdbcTemplate jdbcTemplate = getJdbcTemplate(id);
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Map<String, String> map = data.get(i);
                String primaryKey = null;
                int j = 0;
                for (; j < fields.size(); j++) {
                    FieldModel field = fields.get(j);
                    if ("PRI".equals(field.getColumnKey())) {
                        primaryKey = field.getColumnName();
                    }
                    ps.setString(j + 1, map.get(field.getColumnName()));
                }
                if (primaryKey != null) {
                    ps.setString(j + 1, map.get(primaryKey));
                }
            }

            @Override
            public int getBatchSize() {
                return data.size();
            }
        });
    }

    @Override
    public void deleteTableList(Long id, String tableName, List<Map<String, String>> data) throws SakuraException {
        List<FieldModel> fields = getTableFieldList(id, tableName);
        String primaryKey = primaryKey(fields);
        if (primaryKey == null) {
            throw new SakuraException(Code.UNSUPPORT_NO_PRIMARY_KEY);
        }
        String sql = deleteTableSql(tableName, fields);
        JdbcTemplate jdbcTemplate = getJdbcTemplate(id);
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Map<String, String> map = data.get(i);
                ps.setString(1, map.get(primaryKey));
            }

            @Override
            public int getBatchSize() {
                return data.size();
            }
        });
    }

    /**
     * 根据 id 获取数据库连接
     * @param id
     * @return
     * @throws SakuraException
     */
    public JdbcTemplate getJdbcTemplate(Long id) throws SakuraException {
        DatabaseModel databaseModel = getDatabaseModel(id);
        return jdbcTemplates.getJdbcTemplate(databaseModel);
    }

    /**
     * 根据 id 读取数据库连接
     * @param id
     * @return
     */
    public DatabaseModel getDatabaseModel(Long id) {
        Optional<DatabaseModel> optional = databaseService.getDatabaseModel(id);
        return optional.get();
    }

    /**
     * 查询表名字 sql
     * @return
     */
    public String selectTables() {
        return "select table_name, table_collation from information_schema.tables " +
                "where table_schema=? and table_type='base table'";
    }

    /**
     * 查询表字段 sql
     * @return
     */
    public String selectFields() {
        return "select column_name, data_type, character_maximum_length, is_nullable, column_key " +
                "from information_schema.columns where table_schema=? and table_name=?";
    }

    /**
     * 模糊查询条件, 只对 varchar 类型字段查询
     * @param search
     * @param fields
     * @return
     */
    public String searchCondition(String search, List<FieldModel> fields) {
        if (StringUtils.isEmpty(search) || fields.size() == 0) {
            return "0=0";
        }
        String sql = "";
        for (Iterator<FieldModel> it = fields.iterator(); it.hasNext(); ) {
            FieldModel field = it.next();
            if (!"varchar".equals(field.getDataType().toLowerCase())) {
                continue;
            }
            sql += field.getColumnName() + " like '%" + search + "%' ";
            if (it.hasNext()) {
                sql += "or ";
            }
        }
        return sql;
    }

    /**
     * 分页查询表数据 sql
     * @param tableName
     * @return
     */
    public String selectPage(String tableName, List<FieldModel> fields, String condition) {
        List<String> fieldList = new ArrayList<>();
        for (FieldModel field : fields) {
            String type = field.getDataType();
            String tableField;
            String columnName = "`" + field.getColumnName() + "`";
            switch (type) {
                case "datetime":
                    tableField = "DATE_FORMAT(" + columnName + ", '%Y-%m-%d %H:%i:%s') " + columnName;
                    break;
                case "date":
                    tableField = "DATE_FORMAT(" + columnName + ", '%Y-%m-%d) " + columnName;
                    break;
                case "time":
                    tableField = "DATE_FORMAT(" + columnName + ", '%H:%i:%s) " + columnName;
                    break;
                default:
                    tableField = columnName;
                    break;
            }
            fieldList.add(tableField);
        }
        return "select " + Joiner.on(",").join(fieldList.toArray()) + " from " + tableName + " where " + condition + " limit ?, ?";
    }

    /**
     * 查询表数据总数 sql
     * @param tableName
     * @return
     */
    public String selectCount(String tableName, String condition) {
        return "select count(*) from " + tableName + " where " + condition;
    }

    /**
     * 插入表数据 sql
     * @param tableName
     * @param fields
     * @return
     */
    public String insertTableSql(String tableName, List<FieldModel> fields) {
        List<String> fieldList = new ArrayList<>();
        List<String> values = new ArrayList<>();
        for (FieldModel field : fields) {
            fieldList.add("`" + field.getColumnName() + "`");
            values.add("?");
        }
        String sql = "insert into " + tableName + " (" + Joiner.on(",").join(fieldList.toArray()) +
                ") values (" + Joiner.on(",").join(values.toArray()) + ")";
        return sql;
    }

    /**
     * 更新表数据 sql
     * @param tableName
     * @param fields
     * @return
     * @throws SakuraException
     */
    public String updateTableSql(String tableName, List<FieldModel> fields) throws SakuraException {
        String primaryKey = null;
        List<String> fieldList = new ArrayList<>();
        for (int i = 0; i < fields.size(); i++) {
            FieldModel field = fields.get(i);
            if ("PRI".equals(field.getColumnKey())) {
                primaryKey = field.getColumnName();
            }
            fieldList.add("`" + field.getColumnName() + "`=?");
        }
        if (primaryKey == null) {
            throw new SakuraException(Code.UNSUPPORT_NO_PRIMARY_KEY);
        }
        String sql = "update " + tableName + " set " + Joiner.on(",").join(fieldList.toArray()) + " where `" + primaryKey + "`=?";
        return sql;
    }

    /**
     * 删除表数据 sql
     * @param tableName
     * @param fields
     * @return
     * @throws SakuraException
     */
    public String deleteTableSql(String tableName, List<FieldModel> fields) throws SakuraException {
        String primaryKey = primaryKey(fields);
        String sql = "delete from " + tableName + " where `" + primaryKey + "`=?";
        return sql;
    }

    /**
     * 获取表主键
     * @param fields
     * @return
     */
    public String primaryKey(List<FieldModel> fields) {
        String primaryKey = null;
        for (FieldModel field : fields) {
            if ("PRI".equals(field.getColumnKey())) {
                primaryKey = field.getColumnName();
            }
        }
        return primaryKey;
    }
}
