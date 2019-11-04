package com.appd.main;

import com.appd.gui.IHM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class GuiMain {

    private static  final Logger log = LoggerFactory.getLogger(GuiMain.class);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @SuppressWarnings("unused")
            public void run() {
                log.info("Lancement de APPD");
                IHM appd = new IHM();


            }
        });

    }
}
