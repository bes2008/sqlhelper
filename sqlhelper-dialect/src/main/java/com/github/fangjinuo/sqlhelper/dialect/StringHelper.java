
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

package com.github.fangjinuo.sqlhelper.dialect;

import java.util.*;

public class StringHelper {
    public static final String[] EMPTY_STRINGS;

    private StringHelper() {
    }
    

    public static String join(final String seperator, final Iterator objects) {
        final StringBuilder buf = new StringBuilder();
        if (objects.hasNext()) {
            buf.append(objects.next());
        }
        while (objects.hasNext()) {
            buf.append(seperator).append(objects.next());
        }
        return buf.toString();
    }
    public static String[] add(final String[] x, final String sep, final String[] y) {
        final String[] result = new String[x.length];
        for (int i = 0; i < x.length; ++i) {
            result[i] = x[i] + sep + y[i];
        }
        return result;
    }
    

    public static String replace(final String template, final String placeholder, final String replacement, final boolean wholeWords, final boolean encloseInParensIfNecessary) {
        if (template == null) {
            return null;
        }
        final int loc = template.indexOf(placeholder);
        if (loc < 0) {
            return template;
        }
        final String beforePlaceholder = template.substring(0, loc);
        final String afterPlaceholder = template.substring(loc + placeholder.length());
        return replace(beforePlaceholder, afterPlaceholder, placeholder, replacement, wholeWords, encloseInParensIfNecessary);
    }
    
    public static String replace(final String beforePlaceholder, final String afterPlaceholder, final String placeholder, final String replacement, final boolean wholeWords, final boolean encloseInParensIfNecessary) {
        final boolean actuallyReplace = !wholeWords || afterPlaceholder.length() == 0 || !Character.isJavaIdentifierPart(afterPlaceholder.charAt(0));
        final boolean encloseInParens = actuallyReplace && encloseInParensIfNecessary && getLastNonWhitespaceCharacter(beforePlaceholder) != '(' && (getLastNonWhitespaceCharacter(beforePlaceholder) != ',' || getFirstNonWhitespaceCharacter(afterPlaceholder) != ')');
        final StringBuilder buf = new StringBuilder(beforePlaceholder);
        if (encloseInParens) {
            buf.append('(');
        }
        buf.append(actuallyReplace ? replacement : placeholder);
        if (encloseInParens) {
            buf.append(')');
        }
        buf.append(replace(afterPlaceholder, placeholder, replacement, wholeWords, encloseInParensIfNecessary));
        return buf.toString();
    }
    
    public static char getLastNonWhitespaceCharacter(final String str) {
        if (str != null && str.length() > 0) {
            for (int i = str.length() - 1; i >= 0; --i) {
                final char ch = str.charAt(i);
                if (!Character.isWhitespace(ch)) {
                    return ch;
                }
            }
        }
        return '\0';
    }
    
    public static char getFirstNonWhitespaceCharacter(final String str) {
        if (str != null && str.length() > 0) {
            for (int i = 0; i < str.length(); ++i) {
                final char ch = str.charAt(i);
                if (!Character.isWhitespace(ch)) {
                    return ch;
                }
            }
        }
        return '\0';
    }
    
    public static String replaceOnce(final String template, final String placeholder, final String replacement) {
        if (template == null) {
            return null;
        }
        final int loc = template.indexOf(placeholder);
        if (loc < 0) {
            return template;
        }
        return template.substring(0, loc) + replacement + template.substring(loc + placeholder.length());
    }


    public static String unqualify(final String qualifiedName) {
        final int loc = qualifiedName.lastIndexOf(46);
        return (loc < 0) ? qualifiedName : qualifiedName.substring(loc + 1);
    }


    public static String root(final String qualifiedName) {
        final int loc = qualifiedName.indexOf(".");
        return (loc < 0) ? qualifiedName : qualifiedName.substring(0, loc);
    }
    

    public static String toString(final Object[] array) {
        final int len = array.length;
        if (len == 0) {
            return "";
        }
        final StringBuilder buf = new StringBuilder(len * 12);
        for (int i = 0; i < len - 1; ++i) {
            buf.append(array[i]).append(", ");
        }
        return buf.append(array[len - 1]).toString();
    }
    


    public static String truncate(final String string, final int length) {
        if (string.length() <= length) {
            return string;
        }
        return string.substring(0, length);
    }
    

    public static String generateAlias(final String description, final int unique) {
        return generateAliasRoot(description) + Integer.toString(unique) + '_';
    }
    
    private static String generateAliasRoot(final String description) {
        String result = truncate(unqualifyEntityName(description), 10).toLowerCase(Locale.ROOT).replace('/', '_').replace('$', '_');
        result = cleanAlias(result);
        if (Character.isDigit(result.charAt(result.length() - 1))) {
            return result + "x";
        }
        return result;
    }
    
    private static String cleanAlias(final String alias) {
        final char[] chars = alias.toCharArray();
        if (!Character.isLetter(chars[0])) {
            for (int i = 1; i < chars.length; ++i) {
                if (Character.isLetter(chars[i])) {
                    return alias.substring(i);
                }
            }
        }
        return alias;
    }
    
    public static String unqualifyEntityName(final String entityName) {
        String result = unqualify(entityName);
        final int slashPos = result.indexOf(47);
        if (slashPos > 0) {
            result = result.substring(0, slashPos - 1);
        }
        return result;
    }
    

    static {
        EMPTY_STRINGS = new String[0];
    }
}
