package com.jn.sqlhelper.dialect.pagination;

import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.NonDistinctTreeSet;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.comparator.ComparableComparator;
import com.jn.langx.util.comparator.ParallelingComparator;
import com.jn.langx.util.comparator.ReverseComparator;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.FieldComparator;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.type.Primitives;
import com.jn.sqlhelper.dialect.orderby.OrderBy;
import com.jn.sqlhelper.dialect.orderby.OrderByItem;
import com.jn.sqlhelper.dialect.orderby.OrderByType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author jinuo.fang
 */
@SuppressWarnings({"unchecked"})
public class MemoryPaginations {
    private static final Logger logger = LoggerFactory.getLogger(MemoryPaginations.class);

    public static <C, E> List<E> paging(Collection<E> list, PagingRequest<C, E> pagingRequest, Predicate<E>... filters) {
        Preconditions.checkNotNull(list);
        Preconditions.checkNotNull(pagingRequest);

        // step 1: do filter
        Predicate<E> filter = Emptys.isNotEmpty(filters) ? Functions.allPredicate(filters) : null;
        List<E> filtered = Collects.asList(filter != null ? Collects.filter(list, filter) : list);

        // step 2: build paging result
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

        // step 3: sort
        List<E> sorted = pagingRequest.needOrderBy() ? doSort(filtered, pagingRequest.getOrderBy()) : filtered;


        // step 4: do paging
        if (pagingRequest.isGetAllRequest()) {
            result.setItems(sorted);
            return sorted;
        }

        if (pagingRequest.isGetAllFromNonZeroOffsetRequest()) {
            int pageSize = 10;
            int offset = (pagingRequest.getPageNo() - 1) * pageSize;
            List<E> rs = Collects.emptyArrayList();
            Pipeline.of(sorted).skip(offset).addTo(rs);
            result.setItems(rs);
            return rs;
        }

        int offset = (pagingRequest.getPageNo() - 1) * pagingRequest.getPageSize();
        int limit = pagingRequest.getPageSize();
        List<E> rs = Collects.emptyArrayList();
        Pipeline.of(sorted).skip(offset).limit(limit).addTo(rs);
        result.setItems(rs);
        return rs;
    }


    private static <E> List<E> doSort(Collection<E> collection, OrderBy orderBy) {
        if (collection.isEmpty() || !orderBy.isValid()) {
            return Collects.asList(collection);
        }
        List<E> list = Collects.asList(collection);
        Class modelClass = list.get(0).getClass();
        ParallelingComparator parallelingComparator = new ParallelingComparator();

        int i = 0;
        for (OrderByItem orderByItem : orderBy) {
            String fieldName = orderByItem.getExpression();
            Field field = Reflects.getDeclaredField(modelClass, fieldName);
            if (field != null) {
                Comparator comparator = orderByItem.getComparator();
                if (comparator == null) {
                    Class fieldClass = field.getType();
                    if (Comparable.class.isAssignableFrom(Primitives.wrap(fieldClass))) {
                        comparator = new ComparableComparator();
                    }
                }
                if (comparator != null) {
                    comparator = new FieldComparator(field, comparator);
                    if (orderByItem.getType() == OrderByType.DESC) {
                        comparator = new ReverseComparator(comparator);
                    }
                    parallelingComparator.addComparator(comparator);
                    i++;
                }
            } else {
                logger.warn("can't find a field [{}] in class [{}]", fieldName, Reflects.getFQNClassName(modelClass));
            }

        }
        if (i > 0) {
            Set<E> rs = new NonDistinctTreeSet<E>(parallelingComparator);
            rs.addAll(list);
            return Collects.asList(rs);
        }
        return list;
    }

}
