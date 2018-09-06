package com.mysterin.sakura.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author linxb
 */
public class TableRowMapper implements RowMapper<TableModel> {

    @Override
    public TableModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        TableModel table = new TableModel();
        table.setName(rs.getString("table_name"));
        table.setCollation(rs.getString("table_collation"));
        return table;
    }
}
