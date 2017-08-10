package com.test.article.dao.util;

import com.test.common.mysql.sharding.plugin.ShardPlugin;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 分库分表插件
 * Created by  on 2017/3/21.
 */
@Intercepts({@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class}
)})
public class ShardPluginInterceptor implements Interceptor {
    private ShardPlugin proxy = new ShardPlugin();

    public void setTable2Db(Resource table2DbResource) {
        Properties table2DbProperties = new Properties();
        try {
            table2DbProperties.load(table2DbResource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("读取分库分表配置文件失败", e);
        }
        Map<String, String> table2DbMap = convertToMap(table2DbProperties);
        proxy.setTable2DB(table2DbMap);
    }

    public void setConfigLocation(String configLocation) throws Exception {
        final String CONFIG_LOCATION = "configsLocation";
        Properties shardProperties = new Properties();
        shardProperties.setProperty(CONFIG_LOCATION, configLocation);
        proxy.setProperties(shardProperties);
    }

    public Object intercept(Invocation invocation) throws Throwable {
        return proxy.intercept(invocation);
    }

    public Object plugin(Object target) {
        return proxy.plugin(target);
    }

    public void setProperties(Properties properties) {
        proxy.setProperties(properties);
    }

    private  Map<String, String> convertToMap(Properties properties){
        Map<String, String> map = new HashMap();
        for (Object key : properties.keySet()) {
            String keyStr = key.toString();
            String value = properties.getProperty(keyStr);
            map.put(keyStr, value);
        }
        return map;
    }
}
