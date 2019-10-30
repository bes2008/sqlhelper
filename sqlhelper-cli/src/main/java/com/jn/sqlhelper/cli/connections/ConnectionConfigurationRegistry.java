package com.jn.sqlhelper.cli.connections;

import com.jn.langx.util.collection.Collects;
import com.jn.sqlhelper.common.connection.NamedConnectionConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class ConnectionConfigurationRegistry implements InitializingBean {
    /**
     * key: configuration name
     * value: configuration
     */
    private Map<String, NamedConnectionConfiguration> registry = Collects.emptyTreeMap();

    public Set<String> getConnectionNames() {
        return registry.keySet();
    }

    public void addConnectionConfiguration(NamedConnectionConfiguration configuration) {
        registry.put(configuration.getName(), configuration);
    }

    public void removeConnectionConfiguration(String name) {
        registry.remove(name);
    }

    public Map<String, NamedConnectionConfiguration> getConfigurations(){
        return registry;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }


}
