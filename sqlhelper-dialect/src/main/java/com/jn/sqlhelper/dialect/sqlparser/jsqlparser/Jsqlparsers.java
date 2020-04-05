package com.jn.sqlhelper.dialect.sqlparser.jsqlparser;

import com.jn.langx.util.collection.Collects;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.comment.Comment;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.statement.values.ValuesStatement;

import java.util.List;

public class Jsqlparsers {

    private static final List<Class<? extends Statement>> DDL_STATEMENTS = Collects.newArrayList(
            Comment.class,
            Drop.class,
            AlterView.class,
            Alter.class,
            CreateTable.class,
            CreateView.class,
            CreateIndex.class
    );

    private static final List<Class<? extends Statement>> DML_STATEMENTS = Collects.newArrayList(
            Select.class,
            Update.class,
            Insert.class,
            Delete.class,
            Upsert.class,
            SetStatement.class,
            Truncate.class,
            Merge.class,
            Replace.class,
            ValuesStatement.class
    );

    private static final List<Class<? extends Statement>> OTHER_STATEMENTS = Collects.newArrayList(
            Commit.class,
            ShowStatement.class,
            ShowColumnsStatement.class,
            ExplainStatement.class,
            Block.class,
            Execute.class,
            DescribeStatement.class,
            UseStatement.class
    );

    public static boolean isDDL(Statement statement) {
        return DDL_STATEMENTS.contains(statement.getClass());
    }

    public static boolean isDML(Statement statement) {
        return DML_STATEMENTS.contains(statement.getClass());
    }
}
