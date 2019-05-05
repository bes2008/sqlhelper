package com.fjn.helper.sql.dialect.internal.urlparser.oracle;

public class Token {
    private final String token;
    private final int type;

    public Token(final String token, final int type) {
        this.token = token;
        this.type = type;
    }

    public String getToken() {
        return this.token;
    }

    public int getType() {
        return this.type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Token");
        sb.append("{token='").append(this.token).append('\'');
        sb.append(", type=").append(this.type);
        sb.append('}');
        return sb.toString();
    }
}
