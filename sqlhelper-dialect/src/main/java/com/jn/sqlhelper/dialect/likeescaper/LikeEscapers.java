package com.jn.sqlhelper.dialect.likeescaper;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.struct.Entry;
import com.jn.langx.util.struct.Holder;
import com.jn.langx.util.struct.Pair;
import com.jn.sqlhelper.common.utils.SQLs;

import java.util.*;

public class LikeEscapers {

    private static String insert(@NonNull final String string, @Nullable final List<Integer> slotIndexes, @NonNull String insertment) {
        Preconditions.checkNotNull(string);
        if (Emptys.isEmpty(slotIndexes)) {
            return string;
        }
        // step 1: sort indexes:
        final Set<Integer> sortedSlotIndexes = Collects.emptyTreeSet();
        final Holder<Boolean> insertFirst = new Holder<Boolean>(false);
        final Holder<Boolean> insertLast = new Holder<Boolean>(false);
        Collects.forEach(slotIndexes, new Consumer<Integer>() {
            @Override
            public void accept(Integer slotIndex) {
                if (slotIndex <= 0) {
                    insertFirst.set(true);
                } else if (slotIndex >= string.length()) {
                    insertLast.set(true);
                } else {
                    sortedSlotIndexes.add(slotIndex);
                }

            }
        });
        final List<Integer> sortedSlotIndexList = Pipeline.of(sortedSlotIndexes).asList();

        // step 2: split segments
        final List<Pair<Integer, Integer>> segmentOffsetPairs = new LinkedList<Pair<Integer, Integer>>();

        Collects.forEach(sortedSlotIndexList, new Consumer2<Integer, Integer>() {
            @Override
            public void accept(Integer index, Integer slotIndex) {
                if (index == 0) {
                    segmentOffsetPairs.add(new Entry<Integer, Integer>(0, slotIndex));
                } else {
                    segmentOffsetPairs.add(new Entry<Integer, Integer>(sortedSlotIndexList.get(index - 1), slotIndex));
                }
            }
        });

        if (Emptys.isEmpty(sortedSlotIndexList)) {
            segmentOffsetPairs.add(new Entry<Integer, Integer>(0, string.length()));
        } else {
            int lastSlotIndex = sortedSlotIndexList.get(sortedSlotIndexList.size() - 1);
            int stringLength = string.length();
            if (lastSlotIndex < stringLength) {
                segmentOffsetPairs.add(new Entry<Integer, Integer>(lastSlotIndex, stringLength));
            }
        }


        final Collection<String> segments = Collects.map(segmentOffsetPairs, new Function<Pair<Integer, Integer>, String>() {
            @Override
            public String apply(Pair<Integer, Integer> pair) {
                return string.substring(pair.getKey(), pair.getValue());
            }
        });

        // step 3: concat string
        StringBuilder newString = new StringBuilder(string.length() + 20);
        if (insertFirst.get()) {
            newString.append(insertment);
        }
        Iterator<String> segmentIter = segments.iterator();
        while (segmentIter.hasNext()) {
            String segment = segmentIter.next();
            newString.append(segment);
            if (segmentIter.hasNext()) {
                newString.append(insertment);
            }
        }
        if (insertLast.get()) {
            newString.append(insertment);
        }
        return newString.toString();
    }

    public static String insertLikeEscapeDeclares(@NonNull final String sql, @Nullable final List<Integer> slotIndexes, @NonNull LikeEscaper escaper) {
        return insert(sql, slotIndexes, escaper.appendmentAfterLikeClause());
    }

    private static final List<String> keywordsAfterLikeClause = Collects.asList(
            "and", "or",
            "group", "order",
            "limit", "fetch", "offset",
            "window", "union",
            "into",
            "using",
            "plan", "for", "with");

    /**
     * @return key: the parameters placeholder index: all will be escaped ? indexes
     * value: all slots will be insert appentmentOfLikeClause
     *
     * 这个方法有两个作用，
     * 1）对数据进行分段，返回值的key 用来表示分段的起始索引，结束索引
     * 2）计算每一段的参数个数，value用来表示该段中涉及到的参数个数
     */
    public static Pair<List<Integer>, List<Integer>> findEscapedSlots(String sql) {
        String normalDelimiter = " \t\n\r\f',()";
        StringTokenizer tokenizer = new StringTokenizer(sql.toLowerCase(), normalDelimiter, true);
        int singleQuoteCount = 0;
        List<Integer> parameterPlaceholderIndexes = Collects.emptyArrayList();
        List<Integer> escapeDeclareSlotIndexes = Collects.emptyArrayList();

        int readedLength = 0;

        // segment 两个slot之前的字符串，为一个 segment

        int segmentStartIndex = 0; // split to segments by 'like' keyword
        int readedParameterCount = 0;
        int braceCountAfterLike = 0; // 遇到 ( 则 + 1，遇到）则 -1
        boolean inLikeClause = false;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (singleQuoteCount % 2 == 1) {
                // skip any token if the token is between left single quote and right single quote
                if (!"'".equals(token)) {
                    readedLength = readedLength + token.length();
                    continue;
                }
            }
            if (Strings.isBlank(token) || ",".equals(token)) {
                readedLength = readedLength + token.length();
                continue;
            } else if ("'".equals(token)) {
                singleQuoteCount++;
                readedLength = readedLength + token.length();
                continue;
            } else if ("like".equals(token) && singleQuoteCount % 2 == 0) {
                inLikeClause = true;
                String segment = sql.substring(segmentStartIndex, readedLength);
                readedParameterCount = readedParameterCount + SQLs.findPlaceholderParameterCount(segment);
                readedLength = readedLength + token.length();
                segmentStartIndex = readedLength;
                continue;
            }

            if (inLikeClause && singleQuoteCount % 2 == 0) {

                boolean needCompute = false;
                boolean addTokenLengthToReaded = false;
                if ("(".equals(token)) {
                    braceCountAfterLike++;
                    readedLength = readedLength + token.length();
                    addTokenLengthToReaded = true;
                    continue;
                } else if (")".equals(token)) {
                    if (braceCountAfterLike == 0) {
                        needCompute = true;
                        addTokenLengthToReaded = false;
                    }
                    braceCountAfterLike--;
                    if (braceCountAfterLike == 0) {
                        needCompute = true;
                        readedLength = readedLength + token.length();
                        addTokenLengthToReaded = true;
                    }
                } else if (keywordsAfterLikeClause.contains(token)) {
                    // 当 在 like 语句中，找到了一些关键字
                    needCompute = true;
                }

                if (needCompute) {
                    inLikeClause = false;
                    braceCountAfterLike = 0;
                    String segment = sql.substring(segmentStartIndex, readedLength);
                    int parameterCountInLikeClause = SQLs.findPlaceholderParameterCount(segment);
                    if (parameterCountInLikeClause > 0) {
                        escapeDeclareSlotIndexes.add(readedLength);
                        for (int i = 0; i < parameterCountInLikeClause; i++) {
                            parameterPlaceholderIndexes.add(readedParameterCount + i);
                        }
                        readedParameterCount = readedParameterCount + parameterCountInLikeClause;
                    }
                    segmentStartIndex = readedLength;
                }
                if (!addTokenLengthToReaded) {
                    readedLength = readedLength + token.length();
                }
            } else {
                readedLength = readedLength + token.length();
            }
        }

        if (inLikeClause && singleQuoteCount % 2 == 0) {
            inLikeClause = false;

            String segment = sql.substring(segmentStartIndex);
            int parameterCountInLikeClause = SQLs.findPlaceholderParameterCount(segment);
            if (parameterCountInLikeClause > 0) {
                escapeDeclareSlotIndexes.add(readedLength);
                for (int i = 0; i < parameterCountInLikeClause; i++) {
                    parameterPlaceholderIndexes.add(readedParameterCount + i);
                }
                readedParameterCount = readedParameterCount + parameterCountInLikeClause;
            }
            segmentStartIndex = readedLength;
        }

        return new Entry<List<Integer>, List<Integer>>(parameterPlaceholderIndexes, escapeDeclareSlotIndexes);
    }
}
