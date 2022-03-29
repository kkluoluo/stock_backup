package com.icheer.stock.framework.jwt;

import org.apache.shiro.authc.AuthenticationToken;
/**
 * com.eiker.framework.jwt
 *
 * @author eiker
 * @create 2020-06-19 17:44
 */
public class JwtToken implements AuthenticationToken {

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return getCredentials();
    }

    @Override
    public Object getCredentials() {
        return token;
    }

}