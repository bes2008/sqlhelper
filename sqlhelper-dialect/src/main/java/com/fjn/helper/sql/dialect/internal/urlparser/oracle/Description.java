/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 2.1 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fjn.helper.sql.dialect.internal.urlparser.oracle;

import java.util.ArrayList;
import java.util.List;

public class Description {
    private String serviceName;
    private String sid;
    private ArrayList<Address> addressList;

    public Description() {
        this.addressList = new ArrayList<Address>();
    }

    public Description(final KeyValue keyValue) {
        this.addressList = new ArrayList<Address>();
        if (keyValue == null) {
            throw new NullPointerException("keyValue");
        }
        this.mapping(keyValue);
    }

    private void mapping(final KeyValue keyValue) {
        if (!this.compare("description", keyValue)) {
            throw new RuntimeException("description node not found");
        }
        for (final KeyValue kv : keyValue.getKeyValueList()) {
            if (this.compare("address", kv)) {
                String host = null;
                String port = null;
                String protocol = null;
                for (final KeyValue address : kv.getKeyValueList()) {
                    if (this.compare("host", address)) {
                        host = address.getValue();
                    } else if (this.compare("port", address)) {
                        port = address.getValue();
                    } else {
                        if (!this.compare("protocol", address)) {
                            continue;
                        }
                        protocol = address.getValue();
                    }
                }
                this.addAddress(protocol, host, port);
            } else {
                if (!this.compare("connect_data", kv)) {
                    continue;
                }
                for (final KeyValue connectData : kv.getKeyValueList()) {
                    if (this.compare("service_name", connectData)) {
                        this.serviceName = connectData.getValue();
                    } else {
                        if (!this.compare("sid", connectData)) {
                            continue;
                        }
                        this.sid = connectData.getValue();
                    }
                }
            }
        }
    }

    private boolean compare(final String value, final KeyValue kv) {
        if (kv == null) {
            return false;
        }
        return value.equals(kv.getKey());
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
    }

    public String getSid() {
        return this.sid;
    }

    public void setSid(final String sid) {
        this.sid = sid;
    }

    public List<String> getJdbcHost() {
        final List<String> hostList = new ArrayList<String>();
        for (final Address address : this.addressList) {
            final String host = address.getHost();
            String port = address.getPort();
            if (port == null) {
                port = "1521";
            }
            hostList.add(host + ":" + port);
        }
        return hostList;
    }

    public String getDatabaseId() {
        final String serviceName = this.getServiceName();
        if (serviceName != null) {
            return serviceName;
        }
        final String sid = this.getSid();
        if (sid != null) {
            return sid;
        }
        return "oracleDatabaseId not found";
    }

    public void addAddress(final String protocol, final String host, final String port) {
        this.addressList.add(new Address(protocol, host, port));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Description that = (Description) o;
        if (addressList != null ? !addressList.equals(that.addressList) : that.addressList != null) {
            return false;
        }
        if (serviceName != null ? !serviceName.equals(that.serviceName) : that.serviceName != null) {
            return false;
        }
        if (sid != null ? !sid.equals(that.sid) : that.sid != null) {
            return false;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = (this.serviceName != null) ? this.serviceName.hashCode() : 0;
        result = 31 * result + ((this.sid != null) ? this.sid.hashCode() : 0);
        result = 31 * result + ((this.addressList != null) ? this.addressList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Description");
        sb.append("{serviceName='").append(this.serviceName).append('\'');
        sb.append(", sid='").append(this.sid).append('\'');
        sb.append(", addressList=").append(this.addressList);
        sb.append('}');
        return sb.toString();
    }
}
