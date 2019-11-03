package com.appd.gui;

import com.appd.listener.PersonListener;
import com.appd.panel.Persons;

import javax.swing.*;

public class IHM extends JFrame {
    Persons personstab = new Persons();

    public IHM(){
        this.setSize(900, 900);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(personstab);
        this.setVisible(true);
    }

}
