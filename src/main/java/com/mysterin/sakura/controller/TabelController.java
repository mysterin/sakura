package com.mysterin.sakura.controller;

import com.mysterin.sakura.exception.SakuraException;
import com.mysterin.sakura.model.FieldModel;
import com.mysterin.sakura.model.TableModel;
import com.mysterin.sakura.response.Page;
import com.mysterin.sakura.response.Response;
import com.mysterin.sakura.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author linxb
 */
@RestController
@RequestMapping(value = "{dbId}")
public class TabelController {

    @Autowired
    private TableService tableService;

    /**
     * 读取表名字
     * @param dbId
     * @return
     * @throws SakuraException
     */
    @RequestMapping(value = "getTableList")
    public List<TableModel> getList(@PathVariable Long dbId) throws SakuraException {
        return tableService.getTableList(dbId);
    }

    /**
     * 获取表字段
     * @param dbId
     * @param tableName
     * @return
     * @throws SakuraException
     */
    @RequestMapping(value = "{tableName}/getFieldList")
    public List<FieldModel> getFieldList(@PathVariable Long dbId, @PathVariable String tableName) throws SakuraException {
        return tableService.getTableFieldList(dbId, tableName);
    }

    /**
     * 读取表数据
     * @param page
     * @param dbId
     * @param tableName
     * @return
     * @throws SakuraException
     */
    @RequestMapping(value = "{tableName}/getData")
    public Page<Map<String, Object>> getData(Page page, @PathVariable Long dbId, @PathVariable String tableName) throws SakuraException {
        return tableService.getData(page, dbId, tableName);
    }

    @RequestMapping(value = "{tableName}/insert")
    public Response insertTableData(@PathVariable Long dbId, @PathVariable String tableName, @RequestParam Map<String, String> data) throws SakuraException {
        tableService.insertTableData(dbId, tableName, data);
        return Response.success();
    }

    @RequestMapping(value = "{tableName}/update")
    public Response updateTableList(@PathVariable Long dbId, @PathVariable String tableName, @RequestBody List<Map<String, String>> list) throws SakuraException {
        tableService.updateTableList(dbId, tableName, list);
        return Response.success();
    }

    @RequestMapping(value = "{tableName}/delete")
    public Response deleteTableList(@PathVariable Long dbId, @PathVariable String tableName, @RequestBody List<Map<String, String>> list) throws SakuraException {
        tableService.deleteTableList(dbId, tableName, list);
        return Response.success();
    }
}
