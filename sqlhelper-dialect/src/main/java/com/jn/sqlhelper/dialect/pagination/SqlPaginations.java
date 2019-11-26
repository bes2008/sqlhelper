/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.dialect.pagination;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objects;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.sqlhelper.dialect.SQLStatementInstrumentor;
import com.jn.sqlhelper.dialect.orderby.OrderByBuilder;
import com.jn.sqlhelper.dialect.orderby.SqlStyleOrderByBuilder;

public class SqlPaginations {
    public static <C, E> PagingRequest<C, E> preparePagination(int pageNo, int pageSize) {
        return preparePagination(pageNo, pageSize, null);
    }

    public static <C, E> PagingRequest<C, E> preparePagination(int pageNo, int pageSize, String sort) {
        return preparePagination(pageNo, pageSize, sort, null);
    }

    public static <C, E> PagingRequest<C, E> preparePagination(int pageNo, int pageSize, String sort, OrderByBuilder<String> orderByBuilder) {
        return preparePagination(pageNo, pageSize, sort, orderByBuilder, true);
    }

    public static <C, E> PagingRequest<C, E> preparePagination(int pageNo, int pageSize, String sort, OrderByBuilder<String> orderByBuilder, String dialect) {
        return preparePagination(pageNo, pageSize, sort, orderByBuilder, dialect, true, null);
    }

    public static <C, E> PagingRequest<C, E> preparePagination(int pageNo, int pageSize, String sort, OrderByBuilder<String> orderByBuilder, boolean count) {
        return preparePagination(pageNo, pageSize, sort, orderByBuilder, null, count, null);
    }

    public static <C, E> PagingRequest<C, E> preparePagination(int pageNo, int pageSize, String sort, OrderByBuilder<String> orderByBuilder, String dialect, boolean count, String countColumn) {
        PagingRequest<C, E> pagingRequest = new PagingRequest<C, E>().limit(pageNo, pageSize);
        if (Strings.isNotEmpty(sort)) {
            if (orderByBuilder == null) {
                orderByBuilder = SqlStyleOrderByBuilder.DEFAULT;
            }
            pagingRequest.setOrderBy(orderByBuilder.build(sort));
        }
        if (Strings.isNotEmpty(dialect)) {
            pagingRequest.setDialect(dialect);
        }
        pagingRequest.setCount(count);
        if (Strings.isNotEmpty(countColumn)) {
            pagingRequest.setCountColumn(countColumn);
        }

        PagingRequestContextHolder.getContext().setPagingRequest(pagingRequest);
        return pagingRequest;
    }

    public static int findPlaceholderParameterCount(String sqlsegment){
        if(Strings.isNotEmpty(sqlsegment)) {
            sqlsegment = sqlsegment.replaceAll("([\\\\][?])", "");
            sqlsegment = sqlsegment.replaceAll("[^?]", "");
            sqlsegment = sqlsegment.replaceAll("'\\?'", "");
            return sqlsegment.length();
        }
        return 0;
    }

    public static String extractBeforeSubqueryPartition(@NonNull String sql, @NonNull String startFlag) {
        Preconditions.checkNotNull(startFlag, StringTemplates.formatWithPlaceholder("The start flag of the subquery paging request is invalid: {}", startFlag));
        int index = sql.indexOf(startFlag);
        if (index != -1) {
            return sql.substring(0,index);
        }
        return null;
    }

    public static String extractSubqueryPartition(@NonNull String sql, @NonNull String startFlag, @NonNull String endFlag) {
        Preconditions.checkNotNull(startFlag, StringTemplates.formatWithPlaceholder("The start flag of the subquery paging request is invalid: {}", startFlag));
        Preconditions.checkNotNull(endFlag, StringTemplates.formatWithPlaceholder("The   end flag of the subquery paging request is invalid: {}", endFlag));
        String subquery = null;
        int index = sql.indexOf(startFlag);
        if (index != -1) {
            subquery = sql.substring(index + startFlag.length());
        }
        if(Objects.nonNull(subquery)) {
            index = subquery.lastIndexOf(endFlag);
            if (index != -1) {
                subquery = subquery.substring(0, index);
            }
        }
        return subquery;
    }

    public static String extractAfterSubqueryPartition(@NonNull String sql, @NonNull String endFlag) {
        Preconditions.checkNotNull(endFlag, StringTemplates.formatWithPlaceholder("The   end flag of the subquery paging request is invalid: {}", endFlag));
        int index = sql.lastIndexOf(endFlag);
        if (index != -1) {
            return sql.substring(index+endFlag.length());
        }
        return null;
    }


    public static boolean isSubqueryPagingRequest(@Nullable PagingRequest request) {
        if (Objects.isNull(request)) {
            return false;
        }
        return request.isSubqueryPaging();
    }

    public static boolean isValidSubQueryPagination(@Nullable PagingRequest request, @NonNull SQLStatementInstrumentor instrumentor) {
        if (!isSubqueryPagingRequest(request)) {
            return false;
        }
        return Strings.isNotBlank(getSubqueryPaginationStartFlag(request, instrumentor)) && Strings.isNotBlank(getSubqueryPaginationEndFlag(request, instrumentor));
    }

    public static String getSubqueryPaginationStartFlag(@Nullable PagingRequest request) {
        if (Objects.isNull(request)) {
            return null;
        }
        String flag = request.getSubqueryPagingStartFlag();
        if (Strings.isNotBlank(flag)) {
            return flag;
        }
        return null;
    }

    public static String getSubqueryPaginationStartFlag(@Nullable PagingRequest request, @NonNull SQLStatementInstrumentor instrumentor) {
        String flag = getSubqueryPaginationStartFlag(request);
        if (Strings.isNotBlank(flag)) {
            return flag;
        }
        return instrumentor.getConfig().getSubqueryPagingStartFlag();
    }

    public static String getSubqueryPaginationEndFlag(@Nullable PagingRequest request) {
        if (Objects.isNull(request)) {
            return null;
        }
        String flag = request.getSubqueryPagingEndFlag();
        if (Strings.isNotBlank(flag)) {
            return flag;
        }
        return null;
    }

    public static String getSubqueryPaginationEndFlag(@Nullable PagingRequest request, @NonNull SQLStatementInstrumentor instrumentor) {
        String flag = getSubqueryPaginationEndFlag(request);
        if (Strings.isNotBlank(flag)) {
            return flag;
        }
        return instrumentor.getConfig().getSubqueryPagingEndFlag();
    }
}
