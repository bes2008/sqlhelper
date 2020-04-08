package com.jn.sqlhelper.jsqlparser;

import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
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
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.statement.values.ValuesStatement;

import java.util.List;

@SuppressWarnings("unchecked")
public class JSqlParsers {

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

    public static PlainSelect extractPlainSelect(SelectBody selectBody) {
        if (selectBody == null) {
            return null;
        }
        if (selectBody instanceof PlainSelect) {
            return (PlainSelect) selectBody;
        }

        if (selectBody instanceof WithItem) {
            SelectBody subSelectBody = ((WithItem) selectBody).getSelectBody();
            if (subSelectBody != null) {
                return extractPlainSelect(subSelectBody);
            } else {
                return null;
            }
        }

        if (selectBody instanceof ValuesStatement) {
            return null;
        }

        if (selectBody instanceof SetOperationList) {
            SetOperationList setOperationList = (SetOperationList) selectBody;
            List<SelectBody> selectBodyList = setOperationList.getSelects();
            if (Emptys.isNotEmpty(selectBodyList)) {
                return extractPlainSelect(selectBodyList.get(selectBodyList.size() - 1));
            }
        }
        return null;

    }

    public static boolean columnEquals(Column column1, Column column2) {
        if (column1 == null && column2 == null) {
            return true;
        }
        if (column1 == null || column2 == null) {
            return false;
        }
        return column1.getFullyQualifiedName().equalsIgnoreCase(column2.getFullyQualifiedName());
    }

    public static boolean expressionEquals(Expression expr1, Expression expr2) {
        if (expr1 == null && expr2 == null) {
            return true;
        }
        if (expr1 == null || expr2 == null) {
            return false;
        }

        if (expr1 instanceof Column && expr2 instanceof Column) {
            return columnEquals((Column) expr1, (Column) expr2);
        }
        return expr1.toString().equalsIgnoreCase(expr2.toString());
    }
}
