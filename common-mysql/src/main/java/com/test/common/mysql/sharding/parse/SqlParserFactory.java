package com.test.common.mysql.sharding.parse;


import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

import java.io.StringReader;
import java.sql.SQLException;

public class SqlParserFactory {
    private static SqlParserFactory instance = new SqlParserFactory();
    private final CCJSqlParserManager manager = new CCJSqlParserManager();

    public static SqlParserFactory getInstance() {
        return instance;
    }

    public SqlParserFactory() {
    }

    public SqlParser createParser(String originalSql) throws SQLException {
        try {
            Statement statement = this.manager.parse(new StringReader(originalSql));
            if (statement instanceof Select) {
                SelectSqlParser select = new SelectSqlParser((Select)statement);
                select.init();
                return select;
            } else if (statement instanceof Update) {
                UpdateSqlParser update = new UpdateSqlParser((Update)statement);
                update.init();
                return update;
            } else if (statement instanceof Insert) {
                InsertSqlParser insert = new InsertSqlParser((Insert)statement);
                insert.init();
                return insert;
            } else if (statement instanceof Delete) {
                DeleteSqlParser delete = new DeleteSqlParser((Delete)statement);
                delete.init();
                return delete;
            } else {
                throw new SQLException("Unsupported Parser[" + statement.getClass().getName() + "]");
            }
        } catch (JSQLParserException var4) {
            throw new SQLException("SQL Parse Failed", var4);
        }
    }
}

