package com.mysterin.sakura.datasource;

import com.mysterin.sakura.exception.SakuraException;
import com.mysterin.sakura.model.DatabaseModel;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author linxb
 */
@Component
public class JdbcTemplates {

    Map<Long, JdbcTemplate> jtMap = new HashMap<>();

    /**
     * 连接池最小连接数
     */
    @Value("${sakura.dataSource.minIdle}")
    private int minIdle;

    /**
     * 连接池最大连接数
     */
    @Value("${sakura.dataSource.maxPoolSize}")
    private int maxPoolSize;

    /**
     * 连接超时时间
     */
    @Value("${sakura.dataSource.connectTimeout}")
    private long connectTimeout;

    /**
     * 新增 jdbc 模板
     * @param databaseModel
     * @throws SakuraException
     */
    public void addJdbcTemplate(DatabaseModel databaseModel) throws SakuraException {
        Long id = databaseModel.getId();
        if (jtMap.containsKey(id)) {
            return;
        }
        DataSource dataSource = newDataSource(databaseModel);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jtMap.put(id, jdbcTemplate);
    }

    /**
     * 获取模板
     * @param databaseModel
     * @return
     * @throws SakuraException
     */
    public JdbcTemplate getJdbcTemplate(DatabaseModel databaseModel) throws SakuraException {
        Long id = databaseModel.getId();
        if (!jtMap.containsKey(id)) {
            addJdbcTemplate(databaseModel);
        }
        return jtMap.get(id);
    }

    public JdbcTemplate getJdbcTemplate(Long id) {
        return jtMap.get(id);
    }

    /**
     * 删除模板
     * @param id
     */
    public void removeJdbcTemplate(Long id) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate(id);
        if (jdbcTemplate != null) {
            HikariDataSource dataSource = (HikariDataSource) jdbcTemplate.getDataSource();
            dataSource.close();
        }
        jtMap.remove(id);
    }

    /**
     * 构建新的数据源
     * @param databaseModel
     * @return
     * @throws SakuraException
     */
    public DataSource newDataSource(DatabaseModel databaseModel) throws SakuraException {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(DataSourceUtils.driverName(databaseModel));
        dataSource.setJdbcUrl(DataSourceUtils.linkUrl(databaseModel));
        dataSource.setUsername(databaseModel.getUsername());
        dataSource.setPassword(databaseModel.getPassword());
        dataSource.setConnectionTestQuery("select 2");
        dataSource.setMinimumIdle(minIdle);
        dataSource.setMaximumPoolSize(maxPoolSize);
        dataSource.setConnectionTimeout(connectTimeout);
        return dataSource;
    }
}
