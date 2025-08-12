package mg.sprint.framework.core.object;

import java.util.HashMap;
import java.util.Map;

public class ModelView {
    private String url;
    private Map<String, Object> data = new HashMap<>();

    
    public ModelView() {
    }

    public ModelView(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void addData(String key, Object value) {
        this.data.put(key, value);
    }
} 
