package com.mysterin.sakura.datasource;

import com.mysterin.sakura.exception.SakuraException;
import com.mysterin.sakura.model.DatabaseModel;
import com.mysterin.sakura.response.Code;

/**
 * @author linxb
 */
public class DataSourceUtils {

    /**
     * 返回数据库连接 URL
     * @param databaseModel
     * @return
     */
    public static String linkUrl(DatabaseModel databaseModel) throws SakuraException {
        String ip = databaseModel.getIp();
        int port = databaseModel.getPort();
        String name = databaseModel.getName();

        switch (databaseModel.getType()) {
            case "mysql":
                return "jdbc:mysql://" + databaseModel.getIp() + ":" + port + "/" + name + "?" + databaseModel.getParams();
            default:
                throw new SakuraException(Code.UNSUPPORT_TYPE);
        }
    }

    /**
     * 获取驱动类
     * @param databaseModel
     * @return
     * @throws SakuraException
     */
    public static String driverName(DatabaseModel databaseModel) throws SakuraException {
        switch (databaseModel.getType()) {
            case "mysql":
                return "com.mysql.cj.jdbc.Driver";
            default:
                throw new SakuraException(Code.UNSUPPORT_TYPE);
        }
    }
}
