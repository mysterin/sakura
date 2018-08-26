package com.mysterin.sakura.controller;

import com.mysterin.sakura.exception.SakuraException;
import com.mysterin.sakura.model.DatabaseModel;
import com.mysterin.sakura.response.Response;
import com.mysterin.sakura.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(value = "database")
public class DatabaseController {

    @Autowired
    private DatabaseService databaseService;

    @RequestMapping(value = "getList")
    public List<DatabaseModel> getList() {
        return databaseService.getDatavaseList();
    }

    @RequestMapping(value = "save")
    public Response saveDatabase(DatabaseModel databaseModel) {
        databaseService.saveDatabase(databaseModel);
        return Response.success();
    }

    @RequestMapping(value = "test")
    public Response testDatabase(DatabaseModel databaseModel) throws SakuraException, SQLException, ClassNotFoundException {
        databaseService.testDatabase(databaseModel);
        return Response.success();
    }

    @RequestMapping(value = "delete")
    public Response deleteDatabase(Long id) {
        databaseService.deleteDatabase(id);
        return Response.success();
    }

}
