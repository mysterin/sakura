package com.mysterin.sakura.service.impl;

import com.mysterin.sakura.datasource.JdbcTemplates;
import com.mysterin.sakura.exception.SakuraException;
import com.mysterin.sakura.model.*;
import com.mysterin.sakura.response.Code;
import com.mysterin.sakura.response.Page;
import com.mysterin.sakura.service.DatabaseService;
import com.mysterin.sakura.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        String sql = selectPage(tableName, searcheCondition);
        String countSql = selectCount(tableName, searcheCondition);

        JdbcTemplate jdbcTemplate = jdbcTemplates.getJdbcTemplate(databaseModel);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[]{page.getOffset(), page.getLimit()});
        int total = jdbcTemplate.queryForObject(countSql, Integer.class);

        page.setRows(rows);
        page.setTotal(total);
        return page;
    }

    @Override
    public void updateTableList(Long id, String tableName, List<Map<String, String>> data) throws SakuraException {
        List<FieldModel> fields = getTableFieldList(id, tableName);
        String primaryKey = null;
        for (FieldModel field : fields) {
            if ("PRI".equals(field.getColumnKey())) {
                primaryKey = field.getColumnName();
            }
        }
        if (primaryKey == null) {
            throw new SakuraException(Code.UNSUPPORT_NO_PRIMARY_KEY);
        }
        JdbcTemplate jdbcTemplate = getJdbcTemplate(id);
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
    public String selectPage(String tableName, String condition) {
        return "select * from " + tableName + " where " + condition + " limit ?, ?";
    }

    /**
     * 查询表数据总数 sql
     * @param tableName
     * @return
     */
    public String selectCount(String tableName, String condition) {
        return "select count(*) from " + tableName + " where " + condition;
    }
}
