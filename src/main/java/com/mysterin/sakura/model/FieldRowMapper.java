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
        field.setColumnName(rs.getString("columnName"));
        field.setDataType(rs.getString("dataType"));
        return field;
    }
}
