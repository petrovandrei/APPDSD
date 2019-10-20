package com.appd.util;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Classe pour mettre les méthodes communes aux classes PDSSERVER
 */
public class UtilServer {

    private static final Logger log = LoggerFactory.getLogger(UtilServer.class);

    private static final String APPLICATION_VERSION = Util.getPropertyValueFromApplicationProperties("version");
    private static final Long dataPoolLoopSleep = NumberUtils.toLong(Util.getPropertyValueFromApplicationProperties("data_pool_loop_sleep"));

    //Récupére l'ascii
    //pour titre app par exmeple
    public static String getASCII(String name)
    {
        //initialisé à vide
        String ascii = "";

        try
        {
            InputStream inputStream = UtilServer.class.getClassLoader().getResourceAsStream("ascii/" + name);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            /*
            *Seules les 1ères lignse contiennes des noms
             */
            String line = null;
            while((line = bufferedReader.readLine()) != null)
            {
                ascii += line + "\n";
            }

            inputStream.close();
        }
        catch (Exception e)
        {
            log.error("Erreur en récupérant l'ASCII de '" + name + "'");
        }
        return ascii;
    }

    //Getters & setters
    /**
     * @return the applicationVersion
     */
    public static String getApplicationVersion() {
        return APPLICATION_VERSION;
    }

}
