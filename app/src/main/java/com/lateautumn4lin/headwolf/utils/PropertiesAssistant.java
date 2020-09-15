package com.lateautumn4lin.headwolf.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.lateautumn4lin.headwolf.commons.Logger;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * The type Properties assistant.
 */
public class PropertiesAssistant {
    private static final Pattern floatPattern = Pattern.compile("^-?[0-9]+(\\.[0-9]+)?$");
    private static final Pattern intPattern = Pattern.compile("^-?[0-9]+$");

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

    /**
     * Gets json properties.
     *
     * @return the json properties
     */
    public static HashMap getJsonProperties() {
        Properties properties = new Properties();
        try {
            InputStream in = PropertiesAssistant.class.getResourceAsStream("/assets/config");
            properties.load(in);
        } catch (Exception e) {
            Logger.loge(e.toString());
        }
        JsonObject json = new JsonObject();
        for (Object key : properties.keySet()) {

            String baseKey = key.toString();
            String[] splittedKey = baseKey.split("\\.");

            JsonObject nestedObject = json;

            for (int i = 0; i < splittedKey.length - 1; ++i) {
                if (!json.has(splittedKey[i])) {
                    json.add(splittedKey[i], new JsonObject());
                }
                nestedObject = json.getAsJsonObject(splittedKey[i]);
            }

            String finalKeyElement = splittedKey[splittedKey.length - 1];

            String value = properties.getProperty(baseKey);

            String[] splitedValue = value.split("\\,");

            if (splitedValue.length == 1) {

                if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
                    nestedObject.addProperty(finalKeyElement, Boolean.parseBoolean(value));
                } else if (intPattern.matcher(value).matches()) {
                    nestedObject.addProperty(finalKeyElement, Integer.parseInt(value));
                } else if (floatPattern.matcher(value).matches()) {
                    nestedObject.addProperty(finalKeyElement, Float.parseFloat(value));
                } else {
                    nestedObject.addProperty(finalKeyElement, value);
                }

            } else {
                JsonArray elementArray = new JsonArray();
                for (String val : splitedValue) {

                    if (val.toLowerCase().equals("true") || val.toLowerCase().equals("false")) {
                        elementArray.add(new JsonPrimitive(Boolean.parseBoolean(val)));
                    } else if (intPattern.matcher(val).matches()) {
                        elementArray.add(new JsonPrimitive(Integer.parseInt(val)));
                    } else if (floatPattern.matcher(val).matches()) {
                        elementArray.add(new JsonPrimitive(Float.parseFloat(val)));
                    } else {
                        elementArray.add(new JsonPrimitive(val));
                    }
                }
                nestedObject.add(finalKeyElement, elementArray);
            }

        }
        return new Gson().fromJson(json, HashMap.class);
    }
}
