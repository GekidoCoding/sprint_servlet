package mg.sprint.framework.utils;

public class StringUtil {
    public static String removeLastAt(String str , String removing ){
        int lastIndex = str.lastIndexOf(removing);
        if(lastIndex!=-1){
            return str.substring(lastIndex+1);
        }
        return str;
    }    
    
    public static String removeLastAtIfDont(String str , String removing ){
        if(str.endsWith(removing)){
            return removeLastAt(str, removing);
        }
        return str;
    }    
    public static String addInBeginIfDont(String text , String adding){
      
        if (!text.startsWith(adding) ) {
            text=adding+text;
            System.out.println("final adding:"+text);
        }
        return text;
    }
  
}
