package com.lateautumn4lin.headwolf.utils;

import java.io.InputStream;
import java.util.Properties;

public class ProperTies {
    public static Properties getProperties() {
        Properties props = new Properties();
        InputStream in = ProperTies.class.getResourceAsStream("/assets/config");
        try {
            props.load(in);
        } catch (Exception e) {
            Logger.loge(e.toString());
        }
        return props;
    }
}
