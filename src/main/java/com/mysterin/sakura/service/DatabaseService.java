package com.mysterin.sakura.service;

import com.mysterin.sakura.exception.SakuraException;
import com.mysterin.sakura.model.DatabaseModel;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DatabaseService {
    List<DatabaseModel> getDatavaseList();
    Optional<DatabaseModel> getDatabaseModel(Long id);
    void saveDatabase(DatabaseModel databaseModel);
    void testDatabase(DatabaseModel databaseModel) throws ClassNotFoundException, SQLException, SakuraException;
    void deleteDatabase(Long id);
}
