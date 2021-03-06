package com.appd.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

public class Util {

    private static final Logger log = LoggerFactory.getLogger(Util.class);

    private static String getPropertyValueFromApplicationProperties(String propertyName, String fileName)
    {
        String propertyValue = null;
        Properties properties = new Properties();
        try
        {
            InputStream propertiesFile = Util.class.getClassLoader().getResourceAsStream(fileName);
            properties.load(propertiesFile);
            propertyValue = properties.getProperty(propertyName);
            propertiesFile.close();
        }
        catch (Exception e)
        {
            log.error("Error requesting property :  " + propertyName, e);
        }
        return propertyValue;
    }

    public static String getPropertyValueFromApplicationProperties(String propertyName) {
        return getPropertyValueFromApplicationProperties(propertyName, "application.properties");
    }

    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static String getVersionSplitter() {
        return getPropertyValueFromApplicationProperties("version_split");
    }

    public static <T> void displayListElements(List<T> elements, String newLineIndicator)
    {
        if(elements != null && elements.size() > 0)
        {
            for(T element : elements)
            {
                System.out.println(newLineIndicator + element);
            }
        }
        else
        {
            log.error("The list is empty !");
        }
    }


    public static Timestamp getCurrentTimeUTC() {
        return new Timestamp(System.currentTimeMillis());

    }
}
