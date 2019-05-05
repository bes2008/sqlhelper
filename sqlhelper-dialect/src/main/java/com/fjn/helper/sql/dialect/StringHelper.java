package com.fjn.helper.sql.dialect;

import com.fjn.helper.sql.util.StringUtil;

import java.util.*;

public class StringHelper extends StringUtil
{
    private static final int ALIAS_TRUNCATE_LENGTH = 10;
    public static final String WHITESPACE = " \n\r\f\t";
    public static final String[] EMPTY_STRINGS;
    public static final String BATCH_ID_PLACEHOLDER = "$$BATCH_ID_PLACEHOLDER$$";
    
    private StringHelper() {
    }
    
    public static int lastIndexOfLetter(final String string) {
        for (int i = 0; i < string.length(); ++i) {
            final char character = string.charAt(i);
            if (!Character.isLetter(character) && '_' != character) {
                return i - 1;
            }
        }
        return string.length() - 1;
    }
    
    public static String join(final String seperator, final String[] strings) {
        final int length = /*EL:28*/strings.length;
        if (length == 0) {
            return "";
        }
        final int firstStringLength = /*EL:35*/(strings[0] != null) ? strings[0].length() : 4;
        final StringBuilder buf = /*EL:36*/new StringBuilder(length * firstStringLength).append(strings[0]);
        for (int i = 1; i < length; ++i) {
            buf.append(seperator).append(strings[i]);
        }
        return buf.toString();
    }
    
    public static String joinWithQualifierAndSuffix(final String[] values, final String qualifier, final String suffix, final String deliminator) {
        final int length = /*EL:49*/values.length;
        if (length == 0) {
            return "";
        }
        final StringBuilder buf = /*EL:53*/new StringBuilder(length * (values[0].length() + suffix.length())).append(/*EL:54*/qualify(qualifier, values[0])).append(suffix);
        for (int i = 1; i < length; ++i) {
            buf.append(deliminator).append(qualify(qualifier, values[i])).append(suffix);
        }
        return buf.toString();
    }
    
    public static String join(final String seperator, final Iterator objects) {
        final StringBuilder buf = /*EL:62*/new StringBuilder();
        if (objects.hasNext()) {
            buf.append(objects.next());
        }
        while (objects.hasNext()) {
            buf.append(seperator).append(objects.next());
        }
        return buf.toString();
    }
    
    public static String join(final String separator, final Iterable objects) {
        return join(separator, objects.iterator());
    }
    
    public static String[] add(final String[] x, final String sep, final String[] y) {
        final String[] result = /*EL:77*/new String[x.length];
        for (int i = 0; i < x.length; ++i) {
            result[i] = x[i] + sep + y[i];
        }
        return result;
    }
    
    public static String repeat(final String string, final int times) {
        final StringBuilder buf = /*EL:85*/new StringBuilder(string.length() * times);
        for (int i = 0; i < times; ++i) {
            buf.append(string);
        }
        return buf.toString();
    }
    
    public static String repeat(final String string, final int times, final String deliminator) {
        final StringBuilder buf = /*EL:93*/new StringBuilder(string.length() * times + deliminator.length() * (times - 1)).append(string);
        for (int i = 1; i < times; ++i) {
            buf.append(deliminator).append(string);
        }
        return buf.toString();
    }
    
    public static String repeat(final char character, final int times) {
        final char[] buffer = /*EL:102*/new char[times];
        Arrays.fill(buffer, character);
        return new String(buffer);
    }
    
    public static String replace(final String template, final String placeholder, final String replacement) {
        return replace(template, placeholder, replacement, false);
    }
    
    public static String[] replace(final String[] templates, final String placeholder, final String replacement) {
        final String[] result = /*EL:113*/new String[templates.length];
        for (int i = 0; i < templates.length; ++i) {
            result[i] = replace(templates[i], placeholder, replacement);
        }
        return result;
    }
    
    public static String replace(final String template, final String placeholder, final String replacement, final boolean wholeWords) {
        return replace(template, placeholder, replacement, wholeWords, false);
    }
    
    public static String replace(final String template, final String placeholder, final String replacement, final boolean wholeWords, final boolean encloseInParensIfNecessary) {
        if (template == null) {
            return null;
        }
        final int loc = /*EL:133*/template.indexOf(placeholder);
        if (loc < 0) {
            return template;
        }
        final String beforePlaceholder = /*EL:137*/template.substring(0, loc);
        final String afterPlaceholder = /*EL:138*/template.substring(loc + placeholder.length());
        return replace(beforePlaceholder, afterPlaceholder, placeholder, replacement, wholeWords, encloseInParensIfNecessary);
    }
    
    public static String replace(final String beforePlaceholder, final String afterPlaceholder, final String placeholder, final String replacement, final boolean wholeWords, final boolean encloseInParensIfNecessary) {
        final boolean actuallyReplace = /*EL:158*/!wholeWords || afterPlaceholder.length() == /*EL:160*/0 || /*EL:161*/!Character.isJavaIdentifierPart(afterPlaceholder.charAt(0));
        final boolean encloseInParens = /*EL:173*/actuallyReplace && encloseInParensIfNecessary && /*EL:176*/getLastNonWhitespaceCharacter(beforePlaceholder) != '(' && /*EL:177*/(getLastNonWhitespaceCharacter(beforePlaceholder) != ',' || getFirstNonWhitespaceCharacter(afterPlaceholder) != ')');
        final StringBuilder buf = /*EL:179*/new StringBuilder(beforePlaceholder);
        if (encloseInParens) {
            buf.append('(');
        }
        buf.append(actuallyReplace ? replacement : placeholder);
        if (encloseInParens) {
            buf.append(')');
        }
        buf.append(/*EL:188*/replace(afterPlaceholder, placeholder, replacement, wholeWords, encloseInParensIfNecessary));
        return buf.toString();
    }
    
    public static char getLastNonWhitespaceCharacter(final String str) {
        if (str != null && str.length() > 0) {
            for (int i = str.length() - 1; i >= 0; --i) {
                final char ch = /*EL:202*/str.charAt(i);
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
                final char ch = /*EL:214*/str.charAt(i);
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
        final int loc = /*EL:227*/template.indexOf(placeholder);
        if (loc < 0) {
            return template;
        }
        return template.substring(0, loc) + replacement + template.substring(loc + placeholder.length());
    }
    
    public static String[] split(final String separators, final String list) {
        return split(separators, list, false);
    }
    
    public static String[] split(final String separators, final String list, final boolean include) {
        final StringTokenizer tokens = /*EL:241*/new StringTokenizer(list, separators, include);
        final String[] result = /*EL:242*/new String[tokens.countTokens()];
        int i = /*EL:243*/0;
        while (tokens.hasMoreTokens()) {
            result[i++] = tokens.nextToken();
        }
        return result;
    }
    
    public static String[] splitTrimmingTokens(final String separators, final String list, final boolean include) {
        final StringTokenizer tokens = /*EL:251*/new StringTokenizer(list, separators, include);
        final String[] result = /*EL:252*/new String[tokens.countTokens()];
        int i = /*EL:253*/0;
        while (tokens.hasMoreTokens()) {
            result[i++] = tokens.nextToken().trim();
        }
        return result;
    }
    
    public static String unqualify(final String qualifiedName) {
        final int loc = /*EL:261*/qualifiedName.lastIndexOf(46);
        return (loc < 0) ? qualifiedName : qualifiedName.substring(loc + 1);
    }
    
    public static String qualifier(final String qualifiedName) {
        final int loc = /*EL:266*/qualifiedName.lastIndexOf(46);
        return (loc < 0) ? "" : qualifiedName.substring(0, loc);
    }
    
    public static String collapse(final String name) {
        if (name == null) {
            return null;
        }
        final int breakPoint = /*EL:282*/name.lastIndexOf(46);
        if (breakPoint < 0) {
            return name;
        }
        return collapseQualifier(name.substring(0, breakPoint), true) + name.substring(breakPoint);
    }
    
    public static String collapseQualifier(final String qualifier, final boolean includeDots) {
        final StringTokenizer tokenizer = /*EL:300*/new StringTokenizer(qualifier, ".");
        String collapsed = /*EL:301*/Character.toString(tokenizer.nextToken().charAt(0));
        while (tokenizer.hasMoreTokens()) {
            if (includeDots) {
                collapsed += '.';
            }
            collapsed += tokenizer.nextToken().charAt(0);
        }
        return collapsed;
    }
    
    public static String partiallyUnqualify(final String name, final String qualifierBase) {
        if (name == null || !name.startsWith(qualifierBase)) {
            return name;
        }
        return name.substring(qualifierBase.length() + 1);
    }
    
    public static String collapseQualifierBase(final String name, final String qualifierBase) {
        if (name == null || !name.startsWith(qualifierBase)) {
            return collapse(name);
        }
        return collapseQualifier(qualifierBase, true) + name.substring(qualifierBase.length());
    }
    
    public static String[] suffix(final String[] columns, final String suffix) {
        if (suffix == null) {
            return columns;
        }
        final String[] qualified = /*EL:346*/new String[columns.length];
        for (int i = 0; i < columns.length; ++i) {
            qualified[i] = suffix(columns[i], suffix);
        }
        return qualified;
    }
    
    private static String suffix(final String name, final String suffix) {
        return (suffix == null) ? name : (name + suffix);
    }
    
    public static String root(final String qualifiedName) {
        final int loc = /*EL:358*/qualifiedName.indexOf(".");
        return (loc < 0) ? qualifiedName : qualifiedName.substring(0, loc);
    }
    
    public static String unroot(final String qualifiedName) {
        final int loc = /*EL:363*/qualifiedName.indexOf(".");
        return (loc < 0) ? qualifiedName : qualifiedName.substring(loc + 1, qualifiedName.length());
    }
    
    public static boolean booleanValue(final String tfString) {
        final String trimmed = /*EL:368*/tfString.trim().toLowerCase(Locale.ROOT);
        return trimmed.equals("true") || trimmed.equals("t");
    }
    
    public static String toString(final Object[] array) {
        final int len = /*EL:373*/array.length;
        if (len == 0) {
            return "";
        }
        final StringBuilder buf = /*EL:377*/new StringBuilder(len * 12);
        for (int i = 0; i < len - 1; ++i) {
            buf.append(array[i]).append(", ");
        }
        return buf.append(array[len - 1]).toString();
    }
    
    
    private static String[] multiply(final String[] strings, final String placeholder, final String[] replacements) {
        final String[] results = /*EL:393*/new String[replacements.length * strings.length];
        int n = /*EL:394*/0;
        for (final String replacement : replacements) {
            for (final String string : strings) {
                results[n++] = replaceOnce(string, placeholder, replacement);
            }
        }
        return results;
    }
    
    public static int countUnquoted(final String string, final char character) {
        if ('\'' == character) {
            throw new IllegalArgumentException("Unquoted count of quotes is invalid");
        }
        if (string == null) {
            return 0;
        }
        int count = /*EL:413*/0;
        final int stringLength = /*EL:414*/string.length();
        boolean inQuote = /*EL:415*/false;
        for (int indx = 0; indx < stringLength; ++indx) {
            final char c = /*EL:417*/string.charAt(indx);
            if (inQuote) {
                if ('\'' == c) {
                    inQuote = false;
                }
            }
            else if ('\'' == c) {
                inQuote = true;
            }
            else if (c == character) {
                ++count;
            }
        }
        return count;
    }
    
    public static boolean isNotEmpty(final String string) {
        return string != null && string.length() > 0;
    }
    
    public static boolean isEmpty(final String string) {
        return string == null || string.length() == 0;
    }
    
    public static boolean isEmptyOrWhiteSpace(final String string) {
        return isEmpty(string) || isEmpty(string.trim());
    }
    
    public static String qualify(final String prefix, final String name) {
        if (name == null || prefix == null) {
            throw new NullPointerException("prefix or name were null attempting to build qualified name");
        }
        return prefix + '.' + name;
    }
    
    public static String qualifyConditionally(final String prefix, final String name) {
        if (name == null) {
            throw new NullPointerException("name was null attempting to build qualified name");
        }
        return isEmpty(prefix) ? name : (prefix + '.' + name);
    }
    
    public static String[] qualify(final String prefix, final String[] names) {
        if (prefix == null) {
            return names;
        }
        final int len = /*EL:462*/names.length;
        final String[] qualified = /*EL:463*/new String[len];
        for (int i = 0; i < len; ++i) {
            qualified[i] = qualify(prefix, names[i]);
        }
        return qualified;
    }
    
    public static String[] qualifyIfNot(final String prefix, final String[] names) {
        if (prefix == null) {
            return names;
        }
        final int len = /*EL:474*/names.length;
        final String[] qualified = /*EL:475*/new String[len];
        for (int i = 0; i < len; ++i) {
            if (names[i].indexOf(46) < 0) {
                qualified[i] = qualify(prefix, names[i]);
            }
            else {
                qualified[i] = names[i];
            }
        }
        return qualified;
    }
    
    public static int firstIndexOfChar(final String sqlString, final BitSet keys, final int startindex) {
        for (int i = startindex, size = sqlString.length(); i < size; ++i) {
            if (keys.get(sqlString.charAt(i))) {
                return i;
            }
        }
        return -1;
    }
    
    public static int firstIndexOfChar(final String sqlString, final String string, final int startindex) {
        final BitSet keys = /*EL:497*/new BitSet();
        for (int i = 0, size = string.length(); i < size; ++i) {
            keys.set(string.charAt(i));
        }
        return firstIndexOfChar(sqlString, keys, startindex);
    }
    
    public static String truncate(final String string, final int length) {
        if (string.length() <= length) {
            return string;
        }
        return string.substring(0, length);
    }
    
    public static String generateAlias(final String description) {
        return generateAliasRoot(description) + '_';
    }
    
    public static String generateAlias(final String description, final int unique) {
        return generateAliasRoot(description) + Integer.toString(unique) + '_';
    }
    
    private static String generateAliasRoot(final String description) {
        String result = /*EL:540*/truncate(unqualifyEntityName(description), 10).toLowerCase(Locale.ROOT).replace(/*EL:541*/'/', '_').replace(/*EL:542*/'$', '_');
        result = cleanAlias(result);
        if (Character.isDigit(result.charAt(result.length() - 1))) {
            return result + "x";
        }
        return result;
    }
    
    private static String cleanAlias(final String alias) {
        final char[] chars = /*EL:560*/alias.toCharArray();
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
        final int slashPos = /*EL:576*/result.indexOf(47);
        if (slashPos > 0) {
            result = result.substring(0, slashPos - 1);
        }
        return result;
    }
    
    public static String moveAndToBeginning(String filter) {
        if (filter.trim().length() > 0) {
            filter += " and ";
            if (filter.startsWith(" and ")) {
                filter = filter.substring(4);
            }
        }
        return filter;
    }
    
    public static boolean isQuoted(final String name) {
        return name != null && name.length() != 0 && ((name.charAt(0) == '`' && name.charAt(name.length() - 1) == '`') || (name.charAt(0) == '\"' && name.charAt(name.length() - 1) == '\"'));
    }
    
    public static String quote(String name) {
        if (isEmpty(name) || isQuoted(name)) {
            return name;
        }
        if (name.startsWith("\"") && name.endsWith("\"")) {
            name = name.substring(1, name.length() - 1);
        }
        return "`" + name + '`';
    }
    
    public static String unquote(final String name) {
        return isQuoted(name) ? name.substring(1, name.length() - 1) : name;
    }
    
    public static String[] toArrayElement(final String s) {
        return (s == null || s.length() == 0) ? new String[0] : new String[] { s };
    }
    
    public static String nullIfEmpty(final String value) {
        return isEmpty(value) ? null : value;
    }
    
    public static List<String> parseCommaSeparatedString(final String incomingString) {
        return Arrays.asList(incomingString.split("\\s*,\\s*"));
    }
    
    public static <T> String join(final Collection<T> values, final Renderer<T> renderer) {
        final StringBuilder buffer = /*EL:658*/new StringBuilder();
        boolean firstPass = /*EL:659*/true;
        for (final T value : values) {
            if (firstPass) {
                firstPass = false;
            }
            else {
                buffer.append(", ");
            }
            buffer.append(renderer.render(value));
        }
        return buffer.toString();
    }
    
    public static <T> String join(final T[] values, final Renderer<T> renderer) {
        return join(Arrays.asList(values), renderer);
    }
    
    static {
        EMPTY_STRINGS = new String[0];
    }
    
    public interface Renderer<T>
    {
        String render(final T p0);
    }
}
