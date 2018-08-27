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

/**
 * @author linxb
 */
@RestController
@RequestMapping(value = "database")
public class DatabaseController {

    @Autowired
    private DatabaseService databaseService;

    /**
     * 读取数据库连接
     * @return
     */
    @RequestMapping(value = "getList")
    public List<DatabaseModel> getList() {
        return databaseService.getDatavaseList();
    }

    /**
     * 保存数据库连接
     * @param databaseModel
     * @return
     */
    @RequestMapping(value = "save")
    public Response saveDatabase(DatabaseModel databaseModel) {
        databaseService.saveDatabase(databaseModel);
        return Response.success();
    }

    /**
     * 测试数据库连接
     * @param databaseModel
     * @return
     * @throws SakuraException
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @RequestMapping(value = "test")
    public Response testDatabase(DatabaseModel databaseModel) throws SakuraException, SQLException, ClassNotFoundException {
        databaseService.testDatabase(databaseModel);
        return Response.success();
    }

    /**
     * 删除数据库连接
     * @param id
     * @return
     */
    @RequestMapping(value = "delete")
    public Response deleteDatabase(Long id) {
        databaseService.deleteDatabase(id);
        return Response.success();
    }

}
