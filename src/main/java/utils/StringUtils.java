package utils;

/**
 * @author coldilock
 */
public class StringUtils {
    public static boolean isValidMethodName(String str){
        return !str.isEmpty() && !str.startsWith(".") && !str.startsWith("UnsolvedType.");
    }
}
