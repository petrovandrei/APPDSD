package com.appd.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import java.util.Properties;

public class Util {

    /*
    Mettre les méthodes statiques dont j'ai besoin dans tout le projet
     */

    private static final Logger log = LoggerFactory.getLogger(Util.class);

    /**
     * retourne la valeur de ce qui est passé en paramètres dans le fichier properties
     *
     * @param propertyName :
     * 		Nom de la prop dans le fichier properties
     * @return
     * 		valeur de la prop
     */
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
            log.error("Erreur lors de l'appel de la property " + propertyName, e);
        }
        return propertyValue;
    }

    public static String getPropertyValueFromApplicationProperties(String propertyName) {
        return getPropertyValueFromApplicationProperties(propertyName, "application.properties");
    }

    //Méthode pour récupérer la version du client (ou du server) dans le fichier properties
    public static String getVersionSplitter() {
        return getPropertyValueFromApplicationProperties("version_split");
    }


}
