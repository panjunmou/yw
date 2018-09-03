package com.bootdo.common.utils;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

/**
 * Created with IntelliJ IDEA. User: Administrator Date: 13-10-16 Time: 上午11:04
 * To change this template use File | Settings | File Templates.
 */
public class StringHelper {

    public static StringBuilder formatMsg(CharSequence msgWithFormat, boolean autoQuote, Object[] args) {
        int argsLen = args.length;
        boolean markFound = false;

        StringBuilder sb = new StringBuilder(msgWithFormat);

        if (argsLen > 0) {
            for (int i = 0; i < argsLen; i++) {
                String flag = new StringBuilder().append("%").append(i + 1).toString();
                int idx = sb.indexOf(flag);

                while (idx >= 0) {
                    markFound = true;
                    sb.replace(idx, idx + 2, toString(args[i], autoQuote));
                    idx = sb.indexOf(flag);
                }
            }

            if ((args[(argsLen - 1)] instanceof Throwable)) {
                StringWriter sw = new StringWriter();
                ((Throwable) args[(argsLen - 1)]).printStackTrace(new PrintWriter(sw));
                sb.append("\n").append(sw.toString());
            } else if ((argsLen == 1) && (!markFound)) {
                sb.append(args[(argsLen - 1)].toString());
            }
        }
        return sb;
    }

    public static StringBuilder formatMsg(String msgWithFormat, Object[] args) {
        return formatMsg(new StringBuilder(msgWithFormat), true, args);
    }

    public static String toString(Object obj, boolean autoQuote) {
        StringBuilder sb = new StringBuilder();
        if (obj == null) {
            sb.append("NULL");
        } else if ((obj instanceof Object[])) {
            for (int i = 0; i < ((Object[]) obj).length; i++) {
                sb.append(((Object[]) (Object[]) obj)[i]).append(", ");
            }
            if (sb.length() > 0)
                sb.delete(sb.length() - 2, sb.length());
        } else {
            sb.append(obj.toString());
        }

        if ((autoQuote) && (sb.length() > 0) && ((sb.charAt(0) != '[') || (sb.charAt(sb.length() - 1) != ']'))
                && ((sb.charAt(0) != '{') || (sb.charAt(sb.length() - 1) != '}'))) {
            sb.insert(0, "[").append("]");
        }
        return sb.toString();
    }

    public static String getSetAsString(Set<Object> set) {
        return Joiner.on(",").skipNulls().join(set);
    }

    public static String trimPrefix(String toTrim, String trimStr) {
        while (toTrim.startsWith(trimStr)) {
            toTrim = toTrim.substring(trimStr.length());
        }
        return toTrim;
    }

    public static String trimSufffix(String toTrim, String trimStr) {
        while (toTrim.endsWith(trimStr)) {
            toTrim = toTrim.substring(0, toTrim.length() - trimStr.length());
        }
        return toTrim;
    }

    public static String trim(String toTrim, String trimStr) {
        return trimSufffix(trimPrefix(toTrim, trimStr), trimStr);
    }

    public static String escapeHtml(String content) {
        return StringEscapeUtils.escapeHtml4(content);
    }

    public static String unescapeHtml(String content) {
        return StringEscapeUtils.unescapeHtml4(content);
    }

    /**
     * @param newStr
     * @return
     */
    @Deprecated
    public static String makeFirstLetterUpperCase(String newStr) {
        if (newStr.length() == 0) {
            return newStr;
        }
        char[] oneChar = new char[1];
        oneChar[0] = newStr.charAt(0);
        String firstChar = new String(oneChar);
        return new StringBuilder().append(firstChar.toUpperCase()).append(newStr.substring(1)).toString();
    }
}
