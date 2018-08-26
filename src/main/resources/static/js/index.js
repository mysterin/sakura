$(function() {
    // 获取数据库列表
    var databaseListUrl = 'database/getList';
    // 测试数据库连接
    var testDatabaseUrl = 'database/test';
    // 新增数据库连接
    var saveDatabaseUrl = 'database/save';
    // 删除数据库连接
    var deleteDatabaseUrl = 'database/delete';
    // 获取数据表列表
    var tableListUrl = 'table/getList';
    // 获取表字段名
    var tableFieldListUrl = 'table/getFieldList';
    // 获取数据表数据
    var dataListUrl = 'table/getData';

    var app = new Vue({
        el: '#main',
        data: {
            dbs: [],
            // 当前数据库
            database: {},
            tables: [],
            // 当前 table
            table: {},
            connectInfo: '',
            // 当前表字段
            fields: {}
        },
        methods: {
            // 点击数据库
            selectDatabase: function(event) {
                var id = event.target.id;
                this.database = getDatabaseInfo(id);
                getTableList(id);
            },
            deleteDatabase: function(event) {
                var id = $(event.target).parent().attr('id');
                deleteDatabase(id);
            },
            // 点击表
            selectTable: function(event) {
                var tableName = event.target.innerText;
                this.table = getTableInfo(tableName);
                getTableFieldList(this.database.id, tableName);
            }
        }
    });

    /**
     * 读取数据库列表
     */
    function getDatabaseList() {
        $.ajax({
            type: 'GET',
            url: databaseListUrl,
            dataType: 'json',
            success: function(response) {
                app.dbs = response;
            }
        });
    }
    getDatabaseList();

    /**
     * 获取数据表列表
     * @param id
     */
    function getTableList(id) {
        $.ajax({
            type: 'GET',
            url: tableListUrl,
            data: {id: id},
            dataType: 'json',
            success: function(response) {
                app.tables = response;
            }
        })
    }

    /**
     * 根据数据库名称获取数据库信息
     * @param id
     */
    function getDatabaseInfo(id) {
        var db;
        $.each(app.dbs, function(k, v) {
            if (v.id == id) {
                db = v;
                return;
            }
        });
        return db;
    }

    /**
     * 根据数据表名字获取表信息
     * @param name
     */
    function getTableInfo(name) {
        var t;
        $.each(app.tables, function(k, v) {
            if (v.name == name) {
                t = v;
                return;
            }
        });
        return t;
    }

    function getTableFieldList(dbId, tableName) {
        $.ajax({
            type: 'GET',
            url: tableFieldListUrl,
            data: {
                dbId: dbId,
                tableName: tableName
            },
            dataType: 'json',
            success: function(response) {
                app.fields = response;
                initTable();
            }

        })
    }

    /**
     * 初始化表格
     */
    function initTable() {
        var columns = [];
        $.each(app.fields, function(k, v) {
            var obj = {title: v.columnName, field: v.columnName, align: 'center'};
            columns.push(obj);
        });
        $('#tableData').bootstrapTable('destroy');
        $('#tableData').bootstrapTable({
            url: dataListUrl,
            method: 'POST',
            contentType: "application/x-www-form-urlencoded",
            dataType: 'json',
            queryParamsType: 'limit',
            sidePagination: 'server',
            pagination: true,
            pageList: [10, 25, 50, 100],
            pageNumber: 1,
            pageSize: 10,
            queryParams: function(params) {
                params.dbId = app.database.id;
                params.tableName = app.table.name;
                return params;
            },
            columns: columns
        });
    }

    /**
     * 测试连接
     */
    $('#testDatabase').on('click', function() {
        $.ajax({
            type: 'POST',
            url: testDatabaseUrl,
            data: $('#databaseInfo').serialize(),
            dataType: 'json',
            success: function(response) {
                app.connectInfo = error(response);
            }
        })
    });

    /**
     * 保存数据库配置
     */
    $('#saveDatabase').on('click', function() {
        $.ajax({
            type: 'POST',
            url: saveDatabaseUrl,
            data: $('#databaseInfo').serialize(),
            dataType: 'json',
            success: function(response) {
                if (response && response.code == 0) {
                    getDatabaseList();
                    $('#dbInfo').modal('toggle');
                }
            }
        })
    });

    /**
     * 删除数据库连接
     */
    function deleteDatabase(id) {
        $.ajax({
            type: 'GET',
            url: deleteDatabaseUrl,
            data: {
                id: id
            },
            dataType: 'json',
            success: function(response) {
                if (response && response.code == 0) {
                    getDatabaseList();
                }
            }
        })
    };

    /**
     * 错误码处理
     * @param response
     * @returns {string}
     */
    function error(response) {
        if (!response) {
            return '空返回';
        }
        var msg = response.msg;
        switch (response.code) {
            case 0:
                return '成功';
            case 400:
                return '连接失败';
            case 401:
                return '不支持数据库类型';
            default:
                return msg && msg != '' ? msg : '未知错误';
        }
    }
});
