package com.mysterin.sakura.service;

import com.mysterin.sakura.exception.SakuraException;
import com.mysterin.sakura.model.FieldModel;
import com.mysterin.sakura.model.TableModel;
import com.mysterin.sakura.response.Page;

import java.util.List;
import java.util.Map;

/**
 * @author linxb
 */
public interface TableService {
    List<TableModel> getTableList(Long id) throws SakuraException;
    List<FieldModel> getTableFieldList(Long id, String tableName) throws SakuraException;
    Page<Map<String, Object>> getData(Page page, Long dbId, String tableName) throws SakuraException;
    void updateTableList(Long id, String tableName, List<Map<String, String>> data) throws SakuraException;
    void deleteTableList(Long id, String tableName, List<Map<String, String>> data) throws SakuraException;
}
