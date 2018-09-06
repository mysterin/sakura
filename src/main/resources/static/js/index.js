var app;
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

    app = new Vue({
        el: '#main',
        data: {
            dbs: [],
            // 当前数据库
            database: {},
            tables: [],
            // 当前 table
            table: {
                name: '',
                collation: '',
                fields: [],
                data: {},
                errorCode: 0,
                errorMsg: ''
            },
            connectInfo: initConnectInfo(),
        },
        methods: {
            // 点击数据库
            selectDatabase: function(event) {
                var id = event.currentTarget.id;
                this.database = getDatabaseInfo(id);
                getTableList(id);
            },
            deleteDatabase: function(event) {
                var id = $(event.currentTarget).parent().attr('id');
                deleteDatabase(id);
            },
            // 点击表
            selectTable: function(event) {
                var tableName = event.currentTarget.textContent;
                this.table = getTableInfo(tableName);
                getTableFieldList(this.database.id, tableName);
            },
            // 测试数据库连接
            testDatabase: function() {
                testDatabase();
            },
            // 保存数据库连接
            saveDatabase: function() {
                saveDatabase();
            },
            // 新增数据
            addData: function() {

            },
            // 保存数据
            saveData: function() {
                var data = $('#tableData').bootstrapTable('getSelections');
                if (data.length == 0) {
                    this.table.errorCode = 1;
                    this.table.errorMsg = '请选择数据行';
                } else {
                    this.table.errorCode = 0;
                    this.table.errorMsg = '保存成功';
                }
                setTimeout(function() {app.table.errorMsg = '';}, 3000);
            },
            // 删除数据
            deleteData: function() {

            }
        }
    });

    /**
     * 初始化连接信息
     * @returns {{ip: string, port: string, dbName: string, username: string, password: string, params: string, code: number, msg: string, show: boolean}}
     */
    function initConnectInfo() {
        var connectInfo = {
            ip: '',
            port: '',
            dbName: '',
            username: '',
            password: '',
            params: '',
            code: 0,
            msg: '',
            show: false
        }
        return connectInfo;
    }

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
     * 删除数据库连接
     * @param id
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
        });
    }

    /**
     * 测试数据库连接
     */
    function testDatabase() {
        $.ajax({
            type: 'POST',
            url: testDatabaseUrl,
            data: $('#databaseInfo').serialize(),
            dataType: 'json',
            success: function(response) {
                app.connectInfo.show = true;
                app.connectInfo.code = response.code;
                app.connectInfo.msg = error(response);
            }
        });
    }

    /**
     * 保存数据库连接
     */
    function saveDatabase() {
        $.ajax({
            type: 'POST',
            url: saveDatabaseUrl,
            data: $('#databaseInfo').serialize(),
            dataType: 'json',
            success: function(response) {
                if (response && response.code == 0) {
                    app.connectInfo = initConnectInfo();
                    getDatabaseList();
                    $('#dbInfo').modal('toggle');
                }
            }
        });
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
                return false;
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
                return false;
            }
        });
        return {
            name: t.name,
            collation: t.collation,
            fields: [],
            data: {},
            errorCode: 0,
            errorMsg: ''
        };
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
                app.table.fields = response;
                initTable();
            }

        })
    }

    /**
     * 初始化表格
     */
    function initTable() {
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
            search: true,
            showRefresh: true,
            showColumns: true,
            toolbar: '#toolbar',
            queryParams: function(params) {
                params.dbId = app.database.id;
                params.tableName = app.table.name;
                return params;
            },
            columns: initColumns(),
            detailView: true,
            detailFormatter: function(index, row) {
                var s = '';
                $.each(row, function(k, v) {
                    s += '<strong>' + k + '</strong>: ' + v + '<br>';
                });
                return s;
            },
            onLoadSuccess: function(data) {
                app.table.data = data;
            }
        });
    }

    /**
     * 初始化表格标题
     */
    function initColumns() {
        var columns = [{checkbox: true, align: 'center'}];
        $.each(app.table.fields, function(k, v) {
            var obj = {
                title: v.columnName,
                field: v.columnName,
                align: 'center'
            };
            var dataType = v.dataType.toLowerCase();
            var validateList = [];
            // 二进制不需要编辑
            if (dataType != 'tinyblob' &&
                dataType != 'blob' &&
                dataType != 'mediumblob' &&
                dataType != 'longblob') {
                obj.editable = {
                    type: 'text',
                    title: v.columnName,
                    validate: function(vv) {
                        var msg;
                        $.each(validateList, function(kk, validate) {
                            msg = validate(vv);
                            if (msg) {
                                return false;
                            }
                        });
                        return msg;
                    }
                }
                // 空判断
                if (!v.nullable) {
                    var f = function(vv) {
                        if (!vv) {
                            return '不允许为空';
                        }
                    }
                    validateList.push(f);
                }
            }
            // 长度判断
            if (v.characterMaximumLength) {
                var f = function(vv) {
                    if (vv && vv.length > v.characterMaximumLength) {
                        return '长度不能大于' + v.characterMaximumLength;
                    }
                }
                validateList.push(f);
            }
            // 整型判断
            if (dataType == 'tinyint' ||
                dataType == 'smallint' ||
                dataType == 'mediumint' ||
                dataType == 'int' ||
                dataType == 'bigint') {
                var f = function(vv) {
                    if (vv && !isInteger(vv)) {
                        return '只能输入整数';
                    }
                }
                validateList.push(f);
            }
            // 浮点型判断
            if (dataType == 'float' ||
                dataType == 'double' ||
                dataType ==  'real' ||
                dataType == 'decimal') {
                var f = function(vv) {
                    if (vv && !typeof vv == 'number') {
                        return '只能输入整数或浮点数';
                    }
                }
                validateList.push(f);
            }
            // 时间判断
            if (dataType == 'date') {
                var f = function(vv) {
                    var reg = /^\d{4}-\d{2}-\d{2}$/;
                    if (vv && (!reg.test(vv) || !isDate(vv))) {
                        return '只能输入格式 yyyy-MM-dd 的日期';
                    }
                }
                validateList.push(f);
            }
            if (dataType == 'time') {
                var f = function(vv) {
                    var reg = /^\d{2}:\d{2}:\d{2}$/;
                    if (vv && (!reg.test(vv) || !isDate('2018-09-06 ' + vv))) {
                        return '只能输入格式 hh:mm:ss 的时间';
                    }
                }
                validateList.push(f);
            }
            if (dataType == 'datetime' || dataType == 'timestamp') {
                var f = function(vv) {
                    var reg = /^\d{4}-\d{2}-\d{2}\s{1}\d{2}:\d{2}:\d{2}$/;
                    if (vv && (!reg.test(vv) || !isDate(vv))) {
                        return '只能输入格式 yyyy-MM-dd hh:mm:ss 的时间';
                    }
                }
                validateList.push(f);
            }
            if (dataType == 'year') {
                var f = function(vv) {
                    var reg = /^\d{4}$/;
                    if (vv && !reg.test(vv)) {
                        return '只能输入格式 yyyy 的年份';
                    }
                }
                validateList.push(f);
            }
            columns.push(obj);
        });
        return columns;
    }

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

    /**
     * 判断是否整数
     * @param obj
     * @returns {boolean}
     */
    function isInteger(obj) {
        return !isNaN(obj) && obj%1 === 0;
    }

    /**
     * 判断是否时间
     * @param obj
     * @returns {boolean}
     */
    function isDate(obj) {
        var date = new Date(obj);
        if (isNaN(date.getDate())) {
            return false;
        }
        return true;
    }
});
