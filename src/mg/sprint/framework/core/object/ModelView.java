package mg.sprint.framework.core.object;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelView {
    private static final Logger logger = LoggerFactory.getLogger(ModelView.class);
    private String url;
    private Map<String, Object> data = new HashMap<>();

    public ModelView() {
        logger.debug("Initialized new ModelView instance");
    }

    public ModelView(String url) {
        this.url = url;
        logger.debug("Initialized ModelView with url: {}", url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        logger.debug("Set url to: {}", url);
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void addData(String key, Object value) {
        this.data.put(key, value);
        logger.trace("Added data: key={}, value={}", key, value);
    }
}