package com.test.article.dao.sharding;


import com.test.common.mysql.sharding.plugin.ShardCondition;
import com.test.common.mysql.sharding.plugin.ShardStrategy;

import java.util.Map;

/**
 * user表分库分表中间件
 * Created by jeffpeng on 2017/7/26.
 */
public class UserShardStrategy implements ShardStrategy {

    public ShardCondition parse(Map<String, Object> map) {
        Integer userId = (Integer) map.get("id");
        ShardCondition condition = new ShardCondition();
        int sharding_num = userId%1000;
        String db_prefix = String.format("%02d",sharding_num/10);
        String tb_prefix = String.valueOf(sharding_num%10);
        condition = new ShardCondition();
        condition.setDatabaseSuffix(db_prefix);
        condition.setTableSuffix(tb_prefix);
        return condition;
    }
}
