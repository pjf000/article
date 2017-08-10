package com.test.common.mysql.sharding.parse;


import net.sf.jsqlparser.schema.Table;

import java.util.List;

public interface SqlParser {
    List<Table> getTables();

    String toSQL();
}

