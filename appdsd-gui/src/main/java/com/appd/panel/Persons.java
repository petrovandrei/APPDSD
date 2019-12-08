package com.appd.panel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.appd.listener.PersonListener;

public class Persons extends JPanel {
    private JTextField newFirstNameTextField;
    private JTextField newLastNameTextField;
    private JPasswordField newPasswordField;
    private JTextArea textArea;
    private PersonListener listener;

    private JButton createButton;
    private JButton showButton;

    public Persons() {
        this.setLayout(new BorderLayout());
        JPanel northPanelForCreate = new JPanel();

        northPanelForCreate.setLayout(new FlowLayout());
        listener = new PersonListener(this);
        textArea = new JTextArea();
        createButton = new JButton("Créer");


        createButton.addActionListener(listener);
        newFirstNameTextField = new JTextField(15);
        newLastNameTextField = new JTextField(15);
        newPasswordField = new JPasswordField(15);
        northPanelForCreate.add(new JLabel("Prénom : "));
        northPanelForCreate.add(newFirstNameTextField);
        northPanelForCreate.add(new JLabel("Nom : "));
        northPanelForCreate.add(newLastNameTextField);
        northPanelForCreate.add(new JLabel("Mot de passe : "));
        northPanelForCreate.add(newPasswordField);
        northPanelForCreate.add(createButton);
        this.add(northPanelForCreate,BorderLayout.NORTH);


        // select dans la base

        showButton = new JButton("Afficher");
        JPanel showPanel = new JPanel();
        showPanel.setLayout(new FlowLayout());
        showButton.addActionListener(listener);
        showPanel.add(showButton);
        showPanel.add(textArea);
        this.add(showPanel,BorderLayout.CENTER );
    }



    public JTextField getNewFirstNameTextField() {
        return newFirstNameTextField;
    }

    public void setNewFirstNameTextField(JTextField newFirstNameTextField) {
        this.newFirstNameTextField = newFirstNameTextField;
    }

    public JTextField getNewLastNameTextField() {
        return newLastNameTextField;
    }

    public void setNewLastNameTextField(JTextField newLastNameTextField) {
        this.newLastNameTextField = newLastNameTextField;
    }

    public JPasswordField getNewPasswordField() {
        return newPasswordField;
    }

    public void setNewPasswordField(JPasswordField newPasswordField) {
        this.newPasswordField = newPasswordField;
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(JTextArea textArea) {
        this.textArea = textArea;
    }

    public JButton getCreateButton() {
        return createButton;
    }

    public JButton getShowButton() {
        return showButton;
    }
}

