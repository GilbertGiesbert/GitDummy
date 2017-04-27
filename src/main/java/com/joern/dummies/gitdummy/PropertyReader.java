package com.joern.dummies.gitdummy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by jsattler on 27.04.2017.
 */
public class PropertyReader {

    private static final Logger l = LoggerFactory.getLogger(PropertyReader.class);

    private static Properties properties;


    public static String readProperty(String key){

        if(properties == null){
            initProperties();
        }

        String value = null;
        if(properties.containsKey(key)){
            value = properties.get(key).toString();
        }
        l.debug("read property: "+key+"="+value);
        return value;
    }

    private static void initProperties(){

        Properties properties = new Properties();
        InputStream input = null;

        try {

            String propertiesPath = "config.properties";
            input = PropertyReader.class.getClassLoader().getResourceAsStream(propertiesPath);
            if (input == null) {
                l.error("Failed to init properties, check properties path " + propertiesPath);
            }else{
                properties.load(input);
            }

        } catch (IOException ex) {
            l.error("Failed to init properties", ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    l.error("Failed to init properties", e);
                }
            }
        }
        PropertyReader.properties = properties;
    }
}