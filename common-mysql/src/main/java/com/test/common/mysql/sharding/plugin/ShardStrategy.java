package com.test.common.mysql.sharding.plugin;


import java.util.Map;

public interface ShardStrategy {
    String LOGIC_DB_NAME = "_LOGIC_DB_NAME";
    String LOGIC_TABLE_NAME = "_LOGIC_TABLE_NAME";
    String HINT = "HINT";
    ShardType shardType = ShardType.BY_SUFFIX;

    ShardCondition parse(Map<String, Object> var1);
}
