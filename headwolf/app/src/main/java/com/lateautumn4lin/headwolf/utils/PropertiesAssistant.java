package com.lateautumn4lin.headwolf.utils;

import com.lateautumn4lin.headwolf.commons.Logger;

import java.io.InputStream;
import java.util.Properties;

/**
 * The type Properties assistant.
 */
public class PropertiesAssistant {
    /**
     * Gets properties.
     *
     * @return the properties
     */
    public static Properties getProperties() {
        Properties props = new Properties();
        try {
            InputStream in = PropertiesAssistant.class.getResourceAsStream("/assets/config");
            props.load(in);
        } catch (Exception e) {
            Logger.loge(e.toString());
        }
        return props;
    }
}
