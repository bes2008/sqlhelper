package com.jn.sqlhelper.dialect.pagination;

import com.jn.sqlhelper.dialect.orderby.OrderBy;
import com.jn.sqlhelper.dialect.orderby.OrderByItem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PseudoPaginations {

    public static interface Filter<E> {
        boolean doFilter(E e);
    }

    public <C, E> List<E> pseudoPaging(List<E> list, PagingRequest<C, E> pagingRequest, Filter<E> filter) {
        if (pagingRequest == null) {
            throw new IllegalArgumentException("paging request is illegal");
        }

        if (list == null) {
            list = Collections.emptyList();
        }

        List<E> filtered = filter != null ? doFilter(list, filter) : list;

        PagingResult<E> result = new PagingResult<E>();
        pagingRequest.setResult(result);
        result.setPageNo(pagingRequest.getPageNo());
        result.setPageSize(pagingRequest.getPageSize());
        result.setTotal(filtered.size());
        if (list.isEmpty() || pagingRequest.isEmptyRequest()) {
            List<E> rs = new ArrayList<E>();
            result.setItems(rs);
            return rs;
        }

        List<E> sorted = pagingRequest.needOrderBy() ? doSort(filtered, pagingRequest.getOrderBy()) : filtered;

        if (pagingRequest.isGetAllRequest()) {
            result.setItems(sorted);
            return sorted;
        }

        if (pagingRequest.isGetAllFromNonZeroOffsetRequest()) {
            int pageSize = 10;
            int offset = (pagingRequest.getPageNo() - 1) * pageSize;
            if (offset >= sorted.size()) {
                List<E> rs = new ArrayList<E>();
                result.setItems(rs);
                return rs;
            } else {
                List<E> rs = sorted.subList(offset, sorted.size());
                result.setItems(rs);
                return rs;
            }
        }

        int maxPageCount = pagingRequest.getResult().getMaxPageCount();
        if (pagingRequest.getPageNo() > maxPageCount) {
            List<E> rs = new ArrayList<E>();
            result.setItems(rs);
            return rs;
        } else {
            int offset = (pagingRequest.getPageNo() - 1) * pagingRequest.getPageSize();
            int toOffset = maxPageCount == pagingRequest.getPageNo() ? sorted.size() : (offset + pagingRequest.getPageSize());
            List<E> rs = sorted.subList(offset, toOffset);
            result.setItems(rs);
            return rs;
        }

    }

    private <E> List<E> doFilter(List<E> list, Filter<E> filter) {
        List<E> rs = new ArrayList<E>();
        for (E e : list) {
            if (e != null) {
                if (filter.doFilter(e)) {
                    rs.add(e);
                }
            }
        }
        return rs;
    }

    private <E> List<E> doSort(List<E> list, OrderBy orderBy) {
        if (list.isEmpty()) {
            return list;
        }
        Class elementClass = list.get(0).getClass();
        List<E> rs = new ArrayList<E>();
        for (OrderByItem orderByItem : orderBy) {
            String expression = orderByItem.getExpression();
            Field field = null;
            try {
                elementClass.getDeclaredField(expression);
            } catch (NoSuchFieldException ex) {
                // ignore it
            }
            if (field != null) {

            }
        }
        return rs;
    }

    private class FieldComparator<E> implements Comparator<E> {
        private Field field;
        private boolean asc;

        FieldComparator(Field field) {
            this.field = field;
        }

        @Override
        public int compare(E o1, E o2) {
            if(o1 == o2) {
                return 0;
            }

            Class fieldType = field.getType();
            // TODO build-in comparator
            return 0;
        }

    }

    private static boolean isComparable(Class clazz){
        if(Comparable.class.isAssignableFrom(clazz)){
            return true;
        }
        if(Number.class.isAssignableFrom(clazz)){
            return true;
        }
        if(String.class==clazz || StringBuffer.class == clazz || StringBuilder.class == clazz){
            return true;
        }

        // TODO judge primitive type

        return false;

    }
}
