package com.test.common.mysql.sharding.plugin;


public final class ShardCondition {
    private ShardType shardType;
    private String realDb;
    private String realTable;
    private String databaseSuffix;
    private String tableSuffix;

    public ShardCondition() {
        this.shardType = ShardType.BY_SUFFIX;
    }

    public String getDatabaseSuffix() {
        return this.databaseSuffix;
    }

    public void setDatabaseSuffix(String databaseSuffix) {
        this.databaseSuffix = databaseSuffix;
    }

    public String getTableSuffix() {
        return this.tableSuffix;
    }

    public void setTableSuffix(String tableSuffix) {
        this.tableSuffix = tableSuffix;
    }

    public String getRealDb() {
        return this.realDb;
    }

    public void setRealDb(String realDb) {
        this.realDb = realDb;
    }

    public String getRealTable() {
        return this.realTable;
    }

    public void setRealTable(String realTable) {
        this.realTable = realTable;
    }

    public ShardType getShardType() {
        return this.shardType;
    }

    public void setShardType(ShardType shardType) {
        this.shardType = shardType;
    }

    public String toString() {
        return "ShardCondition{shardType=" + this.shardType + ", realDb='" + this.realDb + '\'' + ", realTable='" + this.realTable + '\'' + ", databaseSuffix='" + this.databaseSuffix + '\'' + ", tableSuffix='" + this.tableSuffix + '\'' + '}';
    }
}

