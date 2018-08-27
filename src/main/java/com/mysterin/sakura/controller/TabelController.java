package com.mysterin.sakura.controller;

import com.mysterin.sakura.exception.SakuraException;
import com.mysterin.sakura.model.FieldModel;
import com.mysterin.sakura.model.TableModel;
import com.mysterin.sakura.response.Page;
import com.mysterin.sakura.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author linxb
 */
@RestController
@RequestMapping(value = "table")
public class TabelController {

    @Autowired
    private TableService tableService;

    /**
     * 读取表名字
     * @param id
     * @return
     * @throws SakuraException
     */
    @RequestMapping(value = "getList")
    public List<TableModel> getList(Long id) throws SakuraException {
        return tableService.getTableList(id);
    }

    /**
     * 获取表字段
     * @param dbId
     * @param tableName
     * @return
     * @throws SakuraException
     */
    @RequestMapping(value = "getFieldList")
    public List<FieldModel> getFieldList(Long dbId, String tableName) throws SakuraException {
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
    @RequestMapping(value = "getData")
    public Page<Map<String, Object>> getData(Page page, Long dbId, String tableName) throws SakuraException {
        return tableService.getData(page, dbId, tableName);
    }
}
