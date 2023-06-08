package com.jn.sqlhelper.common.security;

import com.jn.langx.registry.GenericRegistry;
import com.jn.langx.util.SystemPropertys;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.spi.CommonServiceProvider;

public class JdbcSecuritys {

    static final String DEFAULT_DRIVER_PROPERTIES_CIPHER_NAME = SystemPropertys.get(Reflects.getFQNClassName(JdbcSecuritys.class) + ".DEFAULT_DRIVER_PROPERTIES_CIPHER_NAME", "RSA");
    private static final GenericRegistry<DriverPropertiesCipher> driverPropertiesCipherRegistry = new GenericRegistry<DriverPropertiesCipher>();

    static {
        Pipeline.<DriverPropertiesCipher>of(new CommonServiceProvider<DriverPropertiesCipher>().get(DriverPropertiesCipher.class))
                .forEach(new Consumer<DriverPropertiesCipher>() {
                    @Override
                    public void accept(DriverPropertiesCipher driverPropertiesCipher) {
                        driverPropertiesCipherRegistry.register(driverPropertiesCipher);
                    }
                });

    }

    public static DriverPropertiesCipher getDriverPropertiesCipher(String name) {
        return driverPropertiesCipherRegistry.get(name);
    }

    public static DriverPropertiesCipher getDefaultDriverPropertiesCipher() {
        return getDriverPropertiesCipher(DEFAULT_DRIVER_PROPERTIES_CIPHER_NAME);
    }

    private JdbcSecuritys() {
    }
}
