package com.test.common.mysql.sharding.plugin;

public enum ShardType {
    BY_SUFFIX,
    BY_FULL;

    private ShardType() {
    }

    public static boolean isBySuffix(ShardType shardType) {
        return BY_SUFFIX.equals(shardType);
    }

    public static boolean isByFull(ShardType shardType) {
        return BY_FULL.equals(shardType);
    }
}
