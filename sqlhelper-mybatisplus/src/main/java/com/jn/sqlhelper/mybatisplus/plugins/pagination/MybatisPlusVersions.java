package com.jn.sqlhelper.mybatisplus.plugins.pagination;

import com.baomidou.mybatisplus.core.MybatisPlusVersion;

public class MybatisPlusVersions {
    static final String UNKNOWN = "unknown";
    private static String version = extractMybatisPlusVersion();

    private static String extractMybatisPlusVersion() {
        try {
            return MybatisPlusVersion.getVersion();
        } catch (Throwable ex) {
            return UNKNOWN;
        }
    }

    public static final String getMyBatisPlusVersion() {
        return version;
    }
}
