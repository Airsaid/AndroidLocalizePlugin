package task;

import org.jetbrains.annotations.NotNull;
/**
 * Remove escape sequences before translation and add them afterwards.
 * @see
 * https://developer.android.com/guide/topics/resources/string-resource#FormattingAndStyling
 * @Author: Hans Joachim Herbertz <herberlin@nexgo.de>.
 * @since 2021-02-22
 */
public class EscapeUtil {
    static String removeEscapeSequences(@NotNull String source) {
        String result = source.replaceAll("\\\\'", "'");
        result = result.replaceAll("\\\\\"", "\"");
        result = result.replaceAll("\\\\\\?", "?");
        result = result.replaceAll("\\\\@", "@");

        result = result.replaceAll("&lt;", "<");
        result = result.replaceAll("&amp;", "&");
        result = result.replaceAll("&apos;", "'");
        result = result.replaceAll("&quot;", "\"");

        return result;
    }

    static String addEscapeSequences(@NotNull String source) {
        String result = source.replaceAll("'", "\\\\'");
        result = result.replaceAll("\"", "\\\\\"");
        result = result.replaceAll("\\?", "\\\\?");
        result = result.replaceAll("@", "\\\\@");

        result = result.replaceAll("\\<", "&lt;");
        result = result.replaceAll("&[^lt;]", "&amp; ");
        return result;
    }
}
