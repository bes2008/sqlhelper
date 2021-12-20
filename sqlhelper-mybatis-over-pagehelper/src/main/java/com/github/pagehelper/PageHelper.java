/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.pagehelper;

import com.github.pagehelper.util.PageObjectUtil;
import com.jn.langx.util.Objs;
import com.jn.sqlhelper.dialect.orderby.SqlStyleOrderByBuilder;
import com.jn.sqlhelper.dialect.pagination.PagingRequest;
import com.jn.sqlhelper.dialect.pagination.PagingRequestContextHolder;
import com.jn.sqlhelper.dialect.pagination.PagingResult;
import com.jn.sqlhelper.mybatis.plugins.PageHelperCompibles;

import java.util.Properties;

public class PageHelper {
    private static final ThreadLocal<Page> LOCAL_PAGE = new ThreadLocal<Page>();
    private static boolean DEFAULT_COUNT = true;

    /**
     * 设置 Page 参数
     *
     * @param page
     */
    protected static void setLocalPage(Page page) {
        setLocalPage0(page);
    }

    protected static PagingRequest setLocalPage0(Page page) {
        LOCAL_PAGE.set(page);
        PagingRequest pagingRequest = new PagingRequestAdapter().setPage(page);
        PagingRequestContextHolder.getContext().setPagingRequest(pagingRequest);
        PagingRequestContextHolder.getContext().get().setBoolean(PageHelperCompibles.pageHelperRequestFlag, true);
        return pagingRequest;
    }

    static PagingRequestAdapter getLocalPagingRequest() {
        return (PagingRequestAdapter) PagingRequestContextHolder.getContext().getPagingRequest();
    }

    public static class PagingRequestAdapter extends PagingRequest {
        Page page;

        PagingRequestAdapter() {

        }

        void setOrderBy(String orderBy) {
            page.setOrderBy(orderBy);
            setOrderBy();
        }

        void setOrderBy() {
            this.setOrderBy(SqlStyleOrderByBuilder.DEFAULT.build(page.getOrderBy()));
        }

        PagingRequestAdapter setPage(Page page) {
            this.page = page;
            this.setCount(page.isCount());
            setOrderBy();
            this.setPageNo(page.getPageNum());
            int pageSize = page.getPageSize();
            // 不需要关心 page.pageSizeZero 是否为 true, 因为 pagehelper的这个配置项只是为了在最后设置 total时 用的，也就是仍然会执行分页查询
            if (pageSize == 0) {
                pageSize = -1;
            }
            this.setPageSize(pageSize);
            this.setUseLastPageIfPageOut(page.getReasonable());
            this.setCountColumn(page.getCountColumn());
            return this;
        }

        @Override
        public void clear(boolean clearResult) {
            PagingResult result = this.getResult();
            if (Objs.isNull(result)) {
                this.page.setPageSize(this.getPageSize());
                this.page.setPageNum(this.getPageNo());
            } else {
                this.page.setPageSize(result.getPageSize());
                this.page.setPageNum(result.getPageNo());
                this.page.setPages(result.getMaxPage());
                this.page.setTotal(result.getTotal());
                this.page.addAll(result.getItems());
            }

            //  move it to pageHelperHandler
            //  page.close();

            super.clear(clearResult);
        }
    }

    /**
     * 获取 Page 参数
     *
     * @return
     */
    public static <T> Page<T> getLocalPage() {
        return LOCAL_PAGE.get();
    }

    /**
     * 移除本地变量
     */
    public static void clearPage() {
        LOCAL_PAGE.remove();
    }

    /**
     * 获取任意查询方法的count总数
     *
     * @param select
     * @return
     */
    public static long count(ISelect select) {
        Page<?> page = startPage(1, -1, true);
        select.doSelect();
        return page.getTotal();
    }

    /**
     * 开始分页
     *
     * @param params
     */
    public static <E> Page<E> startPage(Object params) {
        Page<E> page = PageObjectUtil.getPageFromObject(params, true);
        //当已经执行过orderBy的时候
        Page<E> oldPage = getLocalPage();
        if (oldPage != null && oldPage.isOrderByOnly()) {
            page.setOrderBy(oldPage.getOrderBy());
        }
        setLocalPage(page);
        return page;
    }

    /**
     * 开始分页
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数量
     */
    public static <E> Page<E> startPage(int pageNum, int pageSize) {
        return startPage(pageNum, pageSize, DEFAULT_COUNT);
    }

    /**
     * 开始分页
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数量
     * @param count    是否进行count查询
     */
    public static <E> Page<E> startPage(int pageNum, int pageSize, boolean count) {
        return startPage(pageNum, pageSize, count, null, null);
    }

    /**
     * 开始分页
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数量
     * @param orderBy  排序
     */
    public static <E> Page<E> startPage(int pageNum, int pageSize, String orderBy) {
        Page<E> page = startPage(pageNum, pageSize);
        page.setOrderBy(orderBy);
        return page;
    }

    /**
     * 开始分页
     *
     * @param pageNum      页码
     * @param pageSize     每页显示数量
     * @param count        是否进行count查询
     * @param reasonable   分页合理化,null时用默认配置
     * @param pageSizeZero true且pageSize=0时返回全部结果，false时分页,null时用默认配置
     */
    public static <E> Page<E> startPage(int pageNum, int pageSize, boolean count, Boolean reasonable, Boolean pageSizeZero) {
        Page<E> page = new Page<E>(pageNum, pageSize, count);
        page.setReasonable(reasonable);
        page.setPageSizeZero(pageSizeZero);
        //当已经执行过orderBy的时候
        Page<E> oldPage = getLocalPage();
        if (oldPage != null && oldPage.isOrderByOnly()) {
            page.setOrderBy(oldPage.getOrderBy());
        }
        setLocalPage(page);
        return page;
    }

    /**
     * 开始分页
     *
     * @param offset 起始位置，偏移位置
     * @param limit  每页显示数量
     */
    public static <E> Page<E> offsetPage(int offset, int limit) {
        return offsetPage(offset, limit, DEFAULT_COUNT);
    }

    /**
     * 开始分页
     *
     * @param offset 起始位置，偏移位置
     * @param limit  每页显示数量
     * @param count  是否进行count查询
     */
    public static <E> Page<E> offsetPage(int offset, int limit, boolean count) {
        Page<E> page = new Page<E>(new int[]{offset, limit}, count);
        //当已经执行过orderBy的时候
        Page<E> oldPage = getLocalPage();
        if (oldPage != null && oldPage.isOrderByOnly()) {
            page.setOrderBy(oldPage.getOrderBy());
        }
        setLocalPage(page);
        return page;
    }

    /**
     * 排序
     *
     * @param orderBy
     */
    public static void orderBy(String orderBy) {
        Page<?> page = getLocalPage();
        if (page != null) {
            page.setOrderBy(orderBy);
        } else {
            page = new Page();
            page.setOrderBy(orderBy);
            page.setOrderByOnly(true);
            setLocalPage(page);
        }
    }

    /**
     * 设置参数
     *
     * @param properties 插件属性
     */
    protected static void setStaticProperties(Properties properties) {
        //defaultCount，这是一个全局生效的参数，多数据源时也是统一的行为
        if (properties != null) {
            DEFAULT_COUNT = Boolean.valueOf(properties.getProperty("defaultCount", "true"));
        }
    }
}
