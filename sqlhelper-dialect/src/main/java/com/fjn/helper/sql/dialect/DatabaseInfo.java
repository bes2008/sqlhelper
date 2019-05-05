package com.fjn.helper.sql.dialect;

import java.util.List;

public interface DatabaseInfo {
    List<String> getHost();

    String getMultipleHost();

    String getDatabaseId();

    String getRealUrl();

    String getUrl();

    String getVendor();

    boolean isParsingComplete();
}
