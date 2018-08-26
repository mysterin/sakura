package com.mysterin.sakura.service.impl;

import com.mysterin.sakura.dao.DatabaseDao;
import com.mysterin.sakura.datasource.DataSourceUtils;
import com.mysterin.sakura.datasource.JdbcTemplates;
import com.mysterin.sakura.exception.SakuraException;
import com.mysterin.sakura.model.DatabaseModel;
import com.mysterin.sakura.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class DatabaseServiceImpl implements DatabaseService {

    @Autowired
    private DatabaseDao databaseDao;

    @Autowired
    private JdbcTemplates jdbcTemplates;

    @Override
    public List<DatabaseModel> getDatavaseList() {
        List<DatabaseModel> list = databaseDao.findAll();
        return list;
    }

    @Override
    public Optional<DatabaseModel> getDatabaseModel(Long id) {
        return databaseDao.findById(id);
    }

    @Override
    public void saveDatabase(DatabaseModel databaseModel) {
        databaseDao.save(databaseModel);
    }

    @Override
    public void testDatabase(DatabaseModel databaseModel) throws ClassNotFoundException, SQLException, SakuraException {
        Class.forName(DataSourceUtils.driverName(databaseModel));
        String url = DataSourceUtils.linkUrl(databaseModel);
        String username = databaseModel.getUsername();
        String password = databaseModel.getPassword();
        Connection connection = DriverManager.getConnection(url, username, password);
        connection.close();
    }

    @Override
    public void deleteDatabase(Long id) {
        jdbcTemplates.removeJdbcTemplate(id);
        databaseDao.deleteById(id);
    }


}
