
/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.dialect.urlparser.oracle;

public class Address {
    private String protocol;
    private String host;
    private String port;

    public Address(final String protocol, final String host, final String port) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(final String port) {
        this.port = port;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Address address = (Address) o;
        Label_0062:
        {
            if (this.host != null) {
                if (this.host.equals(address.host)) {
                    break Label_0062;
                }
            } else if (address.host == null) {
                break Label_0062;
            }
            return false;
        }
        Label_0095:
        {
            if (this.port != null) {
                if (this.port.equals(address.port)) {
                    break Label_0095;
                }
            } else if (address.port == null) {
                break Label_0095;
            }
            return false;
        }
        if (this.protocol != null) {
            if (this.protocol.equals(address.protocol)) {
                return true;
            }
        } else if (address.protocol == null) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = (this.protocol != null) ? this.protocol.hashCode() : 0;
        result = 31 * result + ((this.host != null) ? this.host.hashCode() : 0);
        result = 31 * result + ((this.port != null) ? this.port.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Address");
        sb.append("{protocol='").append(this.protocol).append('\'');
        sb.append(", host='").append(this.host).append('\'');
        sb.append(", port='").append(this.port).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
