package com.icheer.stock.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServerVersion {
    @Value("${server.version}")
    private  String version;

    public String getVersion(){
        return version;
    }
}
