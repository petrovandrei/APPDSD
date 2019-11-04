package com.appd.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import com.appd.entity.Person;
import com.appd.enumeration.RequestTypes;
import com.appd.exception.BadVersionException;
import com.appd.exception.NoConnectionException;
import com.appd.panel.Persons;
import com.appd.util.GuiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appd.enumeration.RequestSender;
import com.appd.enumeration.UserProfile;

import com.appd.util.JsonUtil;


public class PersonListener implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(PersonListener.class);
    private Persons personsTab;
    private List<Person> persons;
    private List<String> fields;
    private List<String> values;
    private String personText = "";

    public PersonListener(Persons personsTab) {
        this.personsTab = personsTab;
        fields = new ArrayList<String>();
        values = new ArrayList<String>();
    }

    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton)e.getSource();
        if(clickedButton == personsTab.getCreateButton())
        {
        String firstName = personsTab.getNewFirstNameTextField().getText().trim();
        String lastName = personsTab.getNewLastNameTextField().getText().trim();
        String password = String.valueOf(personsTab.getNewPasswordField().getPassword()).trim();
        log.info(firstName);

        if (firstName.length() <= 0 || firstName.length() <= 0 || password.length() <= 0) {
            JOptionPane.showMessageDialog(personsTab, "Un ou plusieurs champs sont vides", "Champ(s) invalide(s)", JOptionPane.ERROR_MESSAGE);
        }
        // else if(password.length() < 4 || NumberUtils.toInt(password, -9) == -9 || password.charAt(0) == '0') {
        // 	JOptionPane.showMessageDialog(personsTab, "Le mot de passe doit uniquement �tre compos� de chiffre"
        // 			+ " et ne doit pas commencer par 0 " + password, "Champ(s) invalide(s)", JOptionPane.ERROR_MESSAGE);
        // }
        else {
            Person newPerson = new Person(0, lastName, firstName, UserProfile.RESIDENT, password);
            log.info(newPerson.toString());

            String serializedObject = JsonUtil.serializeObject(newPerson, Person.class, "");
            String jsonRequest = JsonUtil.serializeRequest(RequestTypes.INSERT, Person.class, serializedObject, null, null, null, RequestSender.CLIENT);
            String response = null;
            try {
                response = GuiUtil.sendRequest(jsonRequest);
            } catch (NoConnectionException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (BadVersionException ex) {
                ex.printStackTrace();
            }
            newPerson = (Person) JsonUtil.deserializeObject(response);
            JOptionPane.showMessageDialog(personsTab, "Nouvelle personne cr��e avec l'id n�" + newPerson.getIdPerson(), "Succes", JOptionPane.INFORMATION_MESSAGE);

        }
        }
    }}