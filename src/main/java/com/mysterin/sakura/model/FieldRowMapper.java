package com.mysterin.sakura.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author linxb
 */
public class FieldRowMapper implements RowMapper<FieldModel> {

    @Override
    public FieldModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        FieldModel field = new FieldModel();
        field.setColumnName(rs.getString("column_name"));
        field.setDataType(rs.getString("data_type"));
        field.setCharacterMaximumLength(rs.getString("character_maximum_length"));
        boolean nullable = "YES".equals(rs.getString("is_nullable")) ? true : false;
        field.setNullable(nullable);
        field.setColumnKey(rs.getString("column_key"));
        return field;
    }
}
