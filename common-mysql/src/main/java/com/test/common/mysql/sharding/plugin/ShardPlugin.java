package com.test.common.mysql.sharding.plugin;


import com.google.common.collect.Maps;
import com.test.common.mysql.sharding.parse.SqlParser;
import com.test.common.mysql.sharding.parse.SqlParserFactory;
import net.sf.jsqlparser.schema.Table;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.*;

@Intercepts({@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class}
)})
public class ShardPlugin implements Interceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShardPlugin.class);
    private Map<String, String> table2DB = Maps.newHashMap();
    private final Map<String, ShardStrategy> strategies = Maps.newHashMap();
    private final Field boundSqlField;

    public ShardPlugin() {
        try {
            this.boundSqlField = BoundSql.class.getDeclaredField("sql");
            this.boundSqlField.setAccessible(true);
        } catch (Exception var2) {
            throw new RuntimeException(var2);
        }
    }

    public Object intercept(Invocation invocation) throws Throwable {
        if (CollectionUtils.isEmpty(this.table2DB)) {
            throw new IllegalArgumentException("请初始化逻辑表 与数据库映射");
        } else {
            BoundSql boundSql = this.getBoundSql(invocation);
            LOGGER.debug("Shard Original SQL:{}", boundSql.getSql());
            SqlParser sqlParser = SqlParserFactory.getInstance().createParser(boundSql.getSql());
            List<Table> tables = sqlParser.getTables();
            if (tables.isEmpty()) {
                return invocation.proceed();
            } else {
                this.doSharding(tables, boundSql, sqlParser);
                return invocation.proceed();
            }
        }
    }

    private void doSharding(List<Table> tables, BoundSql boundSql, SqlParser sqlParser) throws Throwable {
        boolean noneStrateFlag = true;

        Table table;
        boolean strict;
        for(Iterator i$ = tables.iterator(); i$.hasNext(); this.dealTableStrict(strict, table)) {
            table = (Table)i$.next();
            String rowTableName = table.getName();
            strict = this.isStrict(rowTableName);
            String logicTableName = this.getLogicTableName(rowTableName, strict);
            ShardStrategy strategy = (ShardStrategy)this.strategies.get(logicTableName);
            String logicDBName = (String)this.table2DB.get(logicTableName);
            if (StringUtils.isEmpty(logicDBName)) {
                throw new IllegalArgumentException(logicTableName + "找不到对应的db");
            }

            if (strategy == null) {
                this.shardingByNoneStrategy(table, logicDBName, logicTableName);
            } else {
                noneStrateFlag = false;
                Map<String, Object> params = this.paresParams(boundSql);
                params.put("_LOGIC_DB_NAME", logicDBName);
                params.put("_LOGIC_TABLE_NAME", logicTableName);
                params.put("HINT", ShardHint.getHint());
                ShardHint.clear();
                ShardCondition condition = strategy.parse(params);
                if (ShardType.isByFull(condition.getShardType())) {
                    this.shardingByFull(table, condition);
                } else {
                    this.shardingByPre(table, condition, logicDBName, logicTableName);
                }
            }
        }

        String targetSQL = sqlParser.toSQL();
        LOGGER.debug("Shard Convert SQL:{}", targetSQL);
        this.boundSqlField.set(boundSql, targetSQL);
    }

    private void dealTableStrict(boolean strict, Table table) {
        if (strict) {
            table.setName("`" + table.getName() + "`");
            table.setSchemaName("`" + table.getSchemaName() + "`");
        }

    }

    private void shardingByFull(Table table, ShardCondition condition) {
        table.setName(condition.getRealTable());
        table.setSchemaName(condition.getRealDb());
    }

    private void shardingByPre(Table table, ShardCondition condition, String logicDBName, String logicTableName) {
        String dbPrix = logicDBName.substring(0, logicDBName.length() - 2);
        String dbSuffix = logicDBName.substring(logicDBName.length() - 3, logicDBName.length());
        String realDbName = dbPrix + condition.getDatabaseSuffix() + dbSuffix;
        table.setName(logicTableName + "_" + condition.getTableSuffix());
        table.setSchemaName(realDbName);
    }

    private void shardingByNoneStrategy(Table table, String logicDBName, String logicTableName) {
        table.setName(logicTableName);
        table.setSchemaName(logicDBName);
    }

    private boolean isStrict(String rowTableName) {
        return rowTableName.startsWith("`") && rowTableName.endsWith("`");
    }

    private String getLogicTableName(String rowTableName, boolean strict) {
        String logicTableName = null;
        if (strict) {
            logicTableName = rowTableName.substring(1, rowTableName.length() - 1);
        } else {
            logicTableName = rowTableName;
        }

        return logicTableName;
    }

    private BoundSql getBoundSql(Invocation invocation) {
        StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        return boundSql;
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    private Map<String, Object> paresParams(BoundSql boundSql) throws Throwable {
        Object parameterObject = boundSql.getParameterObject();
        Map<String, Object> params = null;
        if (PluginCommon.SINGLE_PARAM_CLASSES.contains(parameterObject.getClass())) {
            List<ParameterMapping> mapping = boundSql.getParameterMappings();
            if (mapping != null && !mapping.isEmpty()) {
                ParameterMapping m = (ParameterMapping)mapping.get(0);
                params = new HashMap();
                ((Map)params).put(m.getProperty(), parameterObject);
            } else {
                params = Collections.emptyMap();
            }
        } else if (parameterObject instanceof Map) {
            params = (Map)parameterObject;
        } else {
            params = new HashMap();
            BeanInfo beanInfo = Introspector.getBeanInfo(parameterObject.getClass());
            PropertyDescriptor[] proDescrtptors = beanInfo.getPropertyDescriptors();
            if (proDescrtptors != null && proDescrtptors.length > 0) {
                PropertyDescriptor[] arr$ = proDescrtptors;
                int len$ = proDescrtptors.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    PropertyDescriptor propDesc = arr$[i$];
                    ((Map)params).put(propDesc.getName(), propDesc.getReadMethod().invoke(parameterObject));
                }
            }
        }

        return (Map)params;
    }

    private void parseStrategies(Element root) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        List<?> strategies = root.elements("strategy");
        if (strategies != null) {
            Iterator i$ = strategies.iterator();

            while(i$.hasNext()) {
                Object o = i$.next();
                Element strategy = (Element)o;
                String logicTable = strategy.attribute("logicTable").getStringValue();
                String strategyClass = strategy.attribute("class").getStringValue();
                Class<?> clazz = Class.forName(strategyClass);
                ShardStrategy shardStrategy = (ShardStrategy)clazz.newInstance();
                if (this.strategies.containsKey(logicTable)) {
                    throw new IllegalArgumentException("LogicTable[" + logicTable + "] Duplicate");
                }

                this.strategies.put(logicTable, shardStrategy);
            }
        }

    }

    public void setTable2DB(Map<String, String> table2DB) {
        this.table2DB = table2DB;
    }

    public void setProperties(Properties properties) {
        String configsLocation = properties.getProperty("configsLocation");
        if (configsLocation == null) {
            throw new IllegalArgumentException("ShardPlugin[" + this.getClass().getName() + "] Property[configsLocation] Cannot Empty");
        } else {
            ClassLoader classLoader = this.getClass().getClassLoader();
            InputStream configInputStream = null;
            InputStream validateInputStream = null;
            InputStream xsdInputStream = null;

            try {
                String clazzName = this.getClass().getName();
                String xsdPath = clazzName.substring(0, clazzName.lastIndexOf(46) + 1).replace('.', '/') + "mybatis-sharding-config.xsd";
                xsdInputStream = classLoader.getResourceAsStream(xsdPath);
                configInputStream = classLoader.getResourceAsStream(configsLocation);
                validateInputStream = classLoader.getResourceAsStream(configsLocation);
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                SAXReader reader = new SAXReader(parser.getXMLReader());
                Document document = reader.read(configInputStream);
                Element root = document.getRootElement();
                this.parseStrategies(root);
            } catch (Exception var27) {
                throw new RuntimeException(var27);
            } finally {
                try {
                    if (validateInputStream != null) {
                        validateInputStream.close();
                    }
                } catch (IOException var26) {
                    ;
                }

                try {
                    if (xsdInputStream != null) {
                        xsdInputStream.close();
                    }
                } catch (IOException var25) {
                    ;
                }

                try {
                    if (configInputStream != null) {
                        configInputStream.close();
                    }
                } catch (IOException var24) {
                    ;
                }

            }

        }
    }
}

