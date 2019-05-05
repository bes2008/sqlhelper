package com.fjn.helper.sql.dialect;

public interface QueryParameters<P>
{
    RowSelection getRowSelection();
    
    boolean isCallable();
    
    P getParameterValues();
    
    int getParameterValuesSize();
}
