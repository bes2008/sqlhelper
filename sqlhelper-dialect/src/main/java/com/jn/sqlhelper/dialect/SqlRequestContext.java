package com.jn.sqlhelper.dialect;

import com.jn.langx.util.BasedStringAccessor;
import com.jn.langx.util.Objects;

import java.util.HashMap;
import java.util.Map;

public class SqlRequestContext<R extends SqlRequest> extends BasedStringAccessor<String, Map<String, Object>> {
    private R request;

    public SqlRequestContext() {
        setTarget(new HashMap<String, Object>());
    }

    public R getRequest() {
        return request;
    }

    public void setRequest(R sqlRequest) {
        this.request = sqlRequest;
        this.request.setContext(this);
    }

    @Override
    public Object get(String key) {
        return getTarget().get(key);
    }

    @Override
    public String getString(String key, String defaultValue) {
        Object value = getTarget().get(key);
        return value == null ? defaultValue : value.toString();
    }

    @Override
    public void set(String key, Object value) {
        getTarget().put(key, value);
    }

    public boolean isPagingRequest() {
        return false;
    }

    public boolean isOrderByRequest() {
        if (!(this.getRequest() instanceof SelectRequest)) {
            return false;
        }
        SelectRequest request = (SelectRequest) this.getRequest();
        if (!request.needOrderBy()) {
            return false;
        }
        if (request.getOrderByAsString().contains("?")) {
            return false;
        }
        return true;
    }

    public void clear(){
        if(Objects.isNotNull(request)) {
            request.clear();
        }
        request = null;
    }

}
