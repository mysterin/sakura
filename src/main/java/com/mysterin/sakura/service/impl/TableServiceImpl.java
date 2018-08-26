package com.mysterin.sakura.service.impl;

import com.mysterin.sakura.datasource.JdbcTemplates;
import com.mysterin.sakura.exception.SakuraException;
import com.mysterin.sakura.model.*;
import com.mysterin.sakura.response.Page;
import com.mysterin.sakura.service.DatabaseService;
import com.mysterin.sakura.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TableServiceImpl implements TableService {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private JdbcTemplates jdbcTemplates;

    @Override
    public List<TableModel> getTableList(Long id) throws SakuraException {
        DatabaseModel databaseModel = getDatabaseModel(id);
        JdbcTemplate jdbcTemplate = jdbcTemplates.getJdbcTemplate(databaseModel);
        String sql = selectTables();
        List<TableModel> tables = jdbcTemplate.query(sql, new Object[]{databaseModel.getName()}, new TableRowMapper());
        return tables;
    }

    @Override
    public List<FieldModel> getTableFieldList(Long id, String tableName) throws SakuraException {
        DatabaseModel databaseModel = getDatabaseModel(id);
        String dbName = databaseModel.getName();
        String sql = selectFields();
        JdbcTemplate jdbcTemplate = jdbcTemplates.getJdbcTemplate(databaseModel);
        List<FieldModel> fields = jdbcTemplate.query(sql, new Object[]{dbName, tableName}, new FieldRowMapper());
        return fields;
    }

    @Override
    public Page<Map<String, Object>> getData(Page page, Long dbId, String tableName) throws SakuraException {
        DatabaseModel databaseModel = getDatabaseModel(dbId);
        String sql = selectPage(tableName);
        String countSql = selectCount(tableName);
        JdbcTemplate jdbcTemplate = jdbcTemplates.getJdbcTemplate(databaseModel);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[]{page.getOffset(), page.getLimit()});
        int total = jdbcTemplate.queryForObject(countSql, Integer.class);
        page.setRows(rows);
        page.setTotal(total);
        return page;
    }

    public DatabaseModel getDatabaseModel(Long id) {
        Optional<DatabaseModel> optional = databaseService.getDatabaseModel(id);
        return optional.get();
    }

    public String selectTables() {
        return "select table_name name, table_collation collation from information_schema.tables " +
                "where table_schema=? and table_type='base table'";
    }

    public String selectFields() {
        return "select column_name columnName, data_type dataType from information_schema.columns " +
                "where table_schema=? and table_name=?";
    }

    public String selectPage(String tableName) {
        return "select * from " + tableName + " limit ?, ?";
    }

    public String selectCount(String tableName) {
        return "select count(*) from " + tableName;
    }
}
