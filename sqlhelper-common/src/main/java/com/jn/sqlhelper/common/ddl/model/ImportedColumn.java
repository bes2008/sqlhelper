package com.jn.sqlhelper.common.ddl.model;

import com.jn.langx.annotation.Nullable;
import com.jn.sqlhelper.common.annotation.Column;
import com.jn.sqlhelper.common.ddl.model.internal.FkInitiallyRule;
import com.jn.sqlhelper.common.ddl.model.internal.FkInitiallyRuleConverter;
import com.jn.sqlhelper.common.ddl.model.internal.FkMutatedRule;
import com.jn.sqlhelper.common.ddl.model.internal.FkMutatedRuleConverter;

public class ImportedColumn {
    @Column({"PKTABLE_CAT","PKTABLE_CATALOG"})
    private String pkTableCatalog;

    @Column({"PKTABLE_SCHEM","PKTABLE_SCHEMA"})
    private String pkTableSchema;

    private String pkTableName;

    private String pkColumnName;

    @Column({"FKTABLE_CAT","FKTABLE_CATALOG"})
    private String fkTableCatalog;

    @Column({"FKTABLE_SCHEM","FKTABLE_SCHEMA"})
    private String fkTableSchema;

    private String fkTableName;

    private String fkColumnName;

    private Integer keySeq;

    @Column(value = {"UPDATE_RULE"}, converter = FkMutatedRuleConverter.class)
    private FkMutatedRule updateRule;

    @Column(value = {"DELETE_RULE"}, converter = FkMutatedRuleConverter.class)
    private FkMutatedRule deleteRule;

    @Nullable
    private String fkName;

    @Nullable
    private String pkName;

    @Column(value = {"DEFERRABILITY"}, converter = FkInitiallyRuleConverter.class)
    private FkInitiallyRule deferrability;

    public String getPkTableCatalog() {
        return pkTableCatalog;
    }

    public void setPkTableCatalog(String pkTableCatalog) {
        this.pkTableCatalog = pkTableCatalog;
    }

    public String getPkTableSchema() {
        return pkTableSchema;
    }

    public void setPkTableSchema(String pkTableSchema) {
        this.pkTableSchema = pkTableSchema;
    }

    public String getPkTableName() {
        return pkTableName;
    }

    public void setPkTableName(String pkTableName) {
        this.pkTableName = pkTableName;
    }

    public String getPkColumnName() {
        return pkColumnName;
    }

    public void setPkColumnName(String pkColumnName) {
        this.pkColumnName = pkColumnName;
    }

    public String getFkTableCatalog() {
        return fkTableCatalog;
    }

    public void setFkTableCatalog(String fkTableCatalog) {
        this.fkTableCatalog = fkTableCatalog;
    }

    public String getFkTableSchema() {
        return fkTableSchema;
    }

    public void setFkTableSchema(String fkTableSchema) {
        this.fkTableSchema = fkTableSchema;
    }

    public String getFkTableName() {
        return fkTableName;
    }

    public void setFkTableName(String fkTableName) {
        this.fkTableName = fkTableName;
    }

    public String getFkColumnName() {
        return fkColumnName;
    }

    public void setFkColumnName(String fkColumnName) {
        this.fkColumnName = fkColumnName;
    }

    public Integer getKeySeq() {
        return keySeq;
    }

    public void setKeySeq(Integer keySeq) {
        this.keySeq = keySeq;
    }

    public FkMutatedRule getUpdateRule() {
        return updateRule;
    }

    public void setUpdateRule(FkMutatedRule updateRule) {
        this.updateRule = updateRule;
    }

    public FkMutatedRule getDeleteRule() {
        return deleteRule;
    }

    public void setDeleteRule(FkMutatedRule deleteRule) {
        this.deleteRule = deleteRule;
    }

    public String getFkName() {
        return fkName;
    }

    public void setFkName(String fkName) {
        this.fkName = fkName;
    }

    public String getPkName() {
        return pkName;
    }

    public void setPkName(String pkName) {
        this.pkName = pkName;
    }

    public FkInitiallyRule getDeferrability() {
        return deferrability;
    }

    public void setDeferrability(FkInitiallyRule deferrability) {
        this.deferrability = deferrability;
    }
}
