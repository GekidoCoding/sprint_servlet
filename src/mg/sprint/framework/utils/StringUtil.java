package mg.sprint.framework.utils;

public class StringUtil {
    public static String removeLastAt(String str , String removing ){
        int lastIndex = str.lastIndexOf(removing);
        if(lastIndex!=-1){
            return str.substring(lastIndex+1);
        }
        return str;
    }    
}
