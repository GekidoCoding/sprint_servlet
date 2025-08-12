package mg.sprint.framework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtil {
    private static final Logger logger = LoggerFactory.getLogger(StringUtil.class);

    public static String removeLastAt(String str, String removing) {
        if (str == null || removing == null) {
            logger.warn("Null input in removeLastAt: str={}, removing={}", str, removing);
            return str;
        }
        int lastIndex = str.lastIndexOf(removing);
        if (lastIndex >= 0) {
            String result = str.substring(0, lastIndex);
            logger.trace("Removed '{}' from '{}': result={}", removing, str, result);
            return result;
        }
        logger.trace("No '{}' found in '{}'", removing, str);
        return str;
    }    
    
    public static String removeLastAtIfDont(String str, String removing) {
        if (str == null || removing == null) {
            logger.warn("Null input in removeLastAtIfDont: str={}, removing={}", str, removing);
            return str;
        }
        if (str.endsWith(removing)) {
            String result = str.substring(0, str.length() - removing.length());
            logger.debug("Removed suffix '{}' from '{}': result={}", removing, str, result);
            return result;
        }
        logger.trace("String '{}' does not end with '{}'", str, removing);
        return str;
    }    
    
    public static String addInBeginIfDont(String text, String adding) {
        if (text == null || adding == null) {
            logger.warn("Null input in addInBeginIfDont: text={}, adding={}", text, adding);
            return text;
        }
        if (!text.startsWith(adding)) {
            String result = adding + text;
            logger.debug("Added prefix '{}' to '{}': result={}", adding, text, result);
            return result;
        }
        logger.trace("String '{}' already starts with '{}'", text, adding);
        return text;
    }
}