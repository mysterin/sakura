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
    var tableListUrl = '/getTableList';
    // 获取表字段名
    var tableFieldListUrl = '/getFieldList';
    // 获取数据表数据
    var dataListUrl = '/getData';
    // 插入表数据
    var insertTableDataUrl = '/insert';
    // 更新表数据
    var updateTableListUrl = '/update';
    // 删除表数据
    var deleteTableListUrl = '/delete';

    app = new Vue({
        el: '#main',
        data: {
            dbs: [],
            searchDatabase: '',
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
            searchTable: '',
            connectInfo: initConnectInfo(),
            insertDataList: [],
            insertDataErrmsg: []
        },
        computed: {
            selectTables: function() {
                var searchTable = this.searchTable.toLowerCase();
                if ("" == searchTable) {
                    return this.tables;
                }
                var tbs = [];
                $.each(this.tables, function(k, v) {
                    var name = v.name.toLowerCase();
                    if (name.indexOf(searchTable) > -1) {
                        tbs.push(v);
                    }
                });
                return tbs;
            },
            selectDatabases: function() {
                var searchDatabase = this.searchDatabase.toLowerCase();
                if ("" == searchDatabase) {
                    return this.dbs;
                }
                var dbs = [];
                $.each(this.dbs, function(k, v) {
                    var name = v.name.toLowerCase();
                    if (name.indexOf(searchDatabase) > -1) {
                        dbs.push(v);
                    }
                });
                return dbs;
            }
        },
        watch: {
            'insertDataList': {
                handler: function(value) {
                    var fields = this.table.fields;
                    $.each(fields, function(k, vv) {
                        var nullable = vv.nullable;
                        var dataType = vv.dataType;
                        var maxLength = vv.characterMaximumLength;
                        var v = app.insertDataList[k];
                        var msg;
                        if (!nullable) {
                            msg = validateNull(v);
                        }
                        if (!msg && maxLength) {
                            msg = validateLength(v, maxLength);
                        }
                        if (!msg && isIntegerType(dataType)) {
                            msg = validateInteger(v);
                        }
                        if (!msg && isNumberType(dataType)) {
                            msg = validateNumber(v);
                        }
                        if (!msg && isDateType(dataType)) {
                            msg = validateDate(v);
                        }
                        if (!msg && isTimeType(dataType)) {
                            msg = validateTime(v);
                        }
                        if (!msg && isDateTimeType(dataType)) {
                            msg = validateDateTime(v);
                        }
                        if (!msg && isYearType(dataType)) {
                            msg = validateYear(v);
                        }
                        Vue.set(app.insertDataErrmsg, k, msg);
                    });
                }
            }
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
                $.each(this.table.fields, function(k, v) {
                    Vue.set(app.insertDataList, k, '');
                });
                $('#insertData').modal();
            },
            insertData: function() {
                var validation = true;
                $.each(this.insertDataErrmsg, function(k, v) {
                    if (v) {
                        validation = false;
                        return false;
                    }
                });
                if (validation) {
                    var data = $('#insertForm').serialize();
                    insertDataInfo(this.database.id, this.table.name, data);
                }
            },
            // 保存数据
            saveData: function() {
                var data = $('#tableData').bootstrapTable('getSelections');
                if (data.length == 0) {
                    this.table.errorCode = 1;
                    this.table.errorMsg = '请选择数据行';
                } else {
                    updateTableList(this.database.id, this.table.name, data);
                }
                setTimeout(function() {app.table.errorMsg = '';}, 3000);
            },
            // 删除数据
            deleteData: function() {
                var data = $('#tableData').bootstrapTable('getSelections');
                if (data.length == 0) {
                    this.table.errorCode = 1;
                    this.table.errorMsg = '请选择数据行';
                } else {
                    deleteTableList(this.database.id, this.table.name, data);
                }
                setTimeout(function() {app.table.errorMsg = '';}, 3000);
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
            url: id + tableListUrl,
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

    /**
     * 获取数据表字段信息
     * @param dbId
     * @param tableName
     */
    function getTableFieldList(dbId, tableName) {
        $.ajax({
            type: 'GET',
            url: dbId + '/' + tableName + tableFieldListUrl,
            dataType: 'json',
            success: function(response) {
                app.table.fields = response;
                initTable();
            }

        });
    }

    /**
     * 插入表数据
     * @param dbId
     * @param tableName
     * @param data
     */
    function insertDataInfo(dbId, tableName, data) {
        $.ajax({
            type: 'POST',
            url: dbId + '/' + tableName + insertTableDataUrl,
            data: data,
            dataType: 'json',
            success: function(response) {
                $('#insertData').modal('hide');
                var msg = error(response);
                app.table.errorCode = response.code;
                app.table.errorMsg = msg;
                if (response.code == 0) {
                    $('#tableData').bootstrapTable('refresh');
                }
            }
        })
    }

    /**
     * 更新数据表数据
     * @param data
     */
    function updateTableList(dbId, tableName, data) {
        $.ajax({
            type: 'POST',
            url: dbId + '/' + tableName + updateTableListUrl,
            contentType: 'application/json',
            data: JSON.stringify(data),
            dataType: 'json',
            success: function(response) {
                var msg = error(response);
                app.table.errorCode = response.code;
                app.table.errorMsg = msg;
                if (response.code == 0) {
                    $('#tableData').bootstrapTable('refresh');
                }
            }
        })
    }

    /**
     * 删除表数据
     * @param data
     */
    function deleteTableList(dbId, tableName, data) {
        $.ajax({
            type: 'POST',
            url: dbId + '/' + tableName + deleteTableListUrl,
            contentType: 'application/json',
            data: JSON.stringify(data),
            dataType: 'json',
            success: function(response) {
                var msg = error(response);
                app.table.errorCode = response.code;
                app.table.errorMsg = msg;
                if (response.code == 0) {
                    $('#tableData').bootstrapTable('refresh');
                }
            }
        })
    }

    /**
     * 初始化表格
     */
    function initTable() {
        $('#tableData').bootstrapTable('destroy');
        $('#tableData').bootstrapTable({
            url: app.database.id + '/' + app.table.name + dataListUrl,
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
            clickToSelect: true,
            queryParams: function(params) {
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
            responseHandler: function(res) {
                app.table.data = res;
                return res;
            }
        });
    }

    /**
     * 初始化表格标题
     */
    function initColumns() {
        var columns = [{checkbox: true, align: 'center'}];
        $.each(app.table.fields, function(k, v) {
            var column = {
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
                dataType != 'longblob' &&
                v.columnKey != 'PRI') {
                column.editable = {
                    type: 'text',
                    title: v.columnName,
                    display: function(value) {
                        if (typeof value == 'object') {
                            value = JSON.stringify(value);
                        }
                        $(this).text(value);
                    },
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
                    validateList.push(validateNull);
                }
            }
            // 长度判断
            if (v.characterMaximumLength) {
                var f = function(vv) {
                    return validateLength(vv, v.characterMaximumLength);
                }
                validateList.push(f);
            }
            // 整型判断
            if (isIntegerType(dataType)) {
                validateList.push(validateInteger);
            }
            // 浮点型判断
            if (isNumberType(dataType)) {
                validateList.push(validateNumber);
            }
            // 时间判断
            if (isDateType(dataType)) {
                column.formatter = function(vv) {
                    return !vv ? vv : formatDate(new Date(vv), 'yyyy-MM-dd');
                }
                validateList.push(validateDate);
            }
            if (isTimeType(dataType)) {
                column.formatter = function(vv) {
                    return !vv ? vv : formatDate(new Date(vv), 'hh:mm:ss');
                }
                validateList.push(validateTime);
            }
            if (isDateTimeType(dataType)) {
                column.formatter = function(vv) {
                    return !vv ? vv : formatDate(new Date(vv), 'yyyy-MM-dd hh:mm:ss');
                }
                validateList.push(validateDateTime);
            }
            if (isYearType(dataType)) {
                column.formatter = function(vv) {
                    return !vv ? vv : formatDate(new Date(vv), 'yyyy');
                }
                validateList.push(validateYear);
            }
            columns.push(column);
        });
        return columns;
    }

    /**
     * 整型类型
     * @param dataType
     * @returns {boolean}
     */
    function isIntegerType(dataType) {
        return dataType == 'tinyint' ||
            dataType == 'smallint' ||
            dataType == 'mediumint' ||
            dataType == 'int' ||
            dataType == 'bigint';
    }

    /**
     * 浮点型类型
     * @param dataType
     * @returns {boolean}
     */
    function isNumberType(dataType) {
        return dataType == 'float' ||
            dataType == 'double' ||
            dataType ==  'real' ||
            dataType == 'decimal';
    }

    /**
     * 日期类型
     * @param dataType
     * @returns {boolean}
     */
    function isDateType(dataType) {
        return dataType == 'date';
    }

    /**
     * 时间类型
     * @param dataType
     * @returns {boolean}
     */
    function isTimeType(dataType) {
        return dataType == 'time';
    }

    /**
     * 日期时间类型
     * @param dataType
     * @returns {boolean}
     */
    function isDateTimeType(dataType) {
        return dataType == 'datetime' || dataType == 'timestamp';
    }

    /**
     * 年份类型
     * @param dataType
     * @returns {boolean}
     */
    function isYearType(dataType) {
        return dataType == 'year';
    }

    /**
     * 验证是否空
     * @param vv
     * @returns {string}
     */
    function validateNull(vv) {
        if (!vv) {
            return '不允许为空';
        }
    }

    /**
     * 验证长度
     * @param vv
     * @returns {string}
     */
    function validateLength(vv, maxLength) {
        if (vv && vv.length > maxLength) {
            return '长度不能大于' + maxLength;
        }
    }

    /**
     * 验证整数
     * @param vv
     * @returns {string}
     */
    function validateInteger(vv) {
        if (vv && !isInteger(vv)) {
            return '只能输入整数';
        }
    }

    /**
     * 验证数字
     * @param vv
     * @returns {string}
     */
    function validateNumber(vv) {
        if (vv && !typeof vv == 'number') {
            return '只能输入整数或浮点数';
        }
    }

    /**
     * 验证日期
     * @param vv
     * @returns {string}
     */
    function validateDate(vv) {
        var reg = /^\d{4}-\d{2}-\d{2}$/;
        if (vv && (!reg.test(vv) || !isDate(vv))) {
            return '只能输入格式 yyyy-MM-dd 的日期';
        }
    }

    /**
     * 验证时间
     * @param vv
     * @returns {string}
     */
    function validateTime(vv) {
        var reg = /^\d{2}:\d{2}:\d{2}$/;
        if (vv && (!reg.test(vv) || !isDate('2018-09-06 ' + vv))) {
            return '只能输入格式 hh:mm:ss 的时间';
        }
    }

    /**
     * 验证日期时间
     * @param vv
     * @returns {string}
     */
    function validateDateTime(vv) {
        var reg = /^\d{4}-\d{2}-\d{2}\s{1}\d{2}:\d{2}:\d{2}$/;
        if (vv && (!reg.test(vv) || !isDate(vv))) {
            return '只能输入格式 yyyy-MM-dd hh:mm:ss 的时间';
        }
    }

    /**
     * 验证年份
     * @param vv
     * @returns {string}
     */
    function validateYear(vv) {
        var reg = /^\d{4}$/;
        if (vv && !reg.test(vv)) {
            return '只能输入格式 yyyy 的年份';
        }
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
                return '操作成功';
            case 400:
                return '连接失败';
            case 401:
                return '不支持数据库类型';
            case 402:
                return '不支持没有主键的表更新数据';
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

    /**
     * 格式化日期
     * @param date
     * @param fmt
     * @returns {*}
     */
    function formatDate(date, fmt) {
        if (/(y+)/.test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
        }
        let o = {
            'M+': date.getMonth() + 1,
            'd+': date.getDate(),
            'h+': date.getHours(),
            'm+': date.getMinutes(),
            's+': date.getSeconds()
        };
        for (let k in o) {
            if (new RegExp(`(${k})`).test(fmt)) {
                let str = o[k] + '';
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? str : ('00' + str).substr(str.length));
            }
        }
        return fmt;
    }
});
