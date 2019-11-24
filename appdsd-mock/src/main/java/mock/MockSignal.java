package mock;

import com.appd.entity.Message;

import com.appd.entity.Location;
import com.appd.entity.Message;
import com.appd.entity.SensorConfiguration;
import com.appd.enumeration.ConnectionStates;
import com.appd.enumeration.RequestSender;
import com.appd.enumeration.RequestTypes;
import com.appd.enumeration.SensorActivity;
import com.appd.enumeration.SensorSensitivity;
import com.appd.enumeration.SensorType;
import com.sun.xml.internal.bind.v2.TODO;
import mock.SensorSignal;
import com.appd.util.GuiUtil;
import com.appd.socket.ClientSocket;
import com.appd.util.JsonUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class MockSignal {

    private static final Logger log = LoggerFactory.getLogger(MockSignal.class);
    private Map<Integer,SensorSignal> sensorSignalMap;
    private List<Location> locationList;
    private int currentLocationId;
    private int numberOfLocations;
    private Scanner sc;
    private final int defaultNumber = -912345566;
    private float choice = 0;

    public MockSignal() {
        currentLocationId = 0;
        numberOfLocations = 0;
        sensorSignalMap = new HashMap<Integer, SensorSignal>();
        sc = new Scanner(System.in);
        System.out.println("Welcome to signal mock");
        displayMainMenu();

    }

    private int chooseAction(float minIncluded, float maxIncluded) {
        int choice = defaultNumber;
        boolean isChoiceCorrect = true;do {
            isChoiceCorrect = true;
            System.out.print("Enter your choice : ");
            choice = NumberUtils.toInt(sc.nextLine(), defaultNumber);
            if(choice == defaultNumber || choice < minIncluded || choice > maxIncluded) {
                System.out.println("The action choosen is incorrect");
                isChoiceCorrect = false;
            }

        } while(!isChoiceCorrect);
        return choice;
    }

    private void displayMainMenu() {
        System.out.println("\nMenu - Choose an action :");
        System.out.println("2: Mock sensors");
        System.out.println("3: Indent Json");
        choice = chooseAction(1, 3);
        if(choice == 1) {
            sensorMockMenu();
        }
        else if(choice == 2) {
            System.out.println("Enter the text in Json format below :");
            String json = sc.nextLine();
            try {
                System.out.println("Result :\n" + JsonUtil.indentJsonOutput(json));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        displayMainMenu();
    }

    private void sensorMockMenu() {

        System.out.println("\nChoose ?");
        System.out.println("1. Generate only active sensors");
        System.out.println("2. Send message");
        System.out.println("3. Stop sending message");
        choice = chooseAction(1, 4);

        if(choice == 1) {
            System.out.println("This operation may take a long time...");
            generateActiveSensors(true);
        } else if(choice == 2) {
            sendAnonymously(true, false);
        } else if(choice == 3) {
            sendAnonymously(false, false);
        }
        displayMainMenu();
    }

    private void generateActiveSensors(boolean b) {
        //TODO écrire la méthode generateActiveSensors qui affiche les capteurs en marche de la bdd
        }

    private void sendAnonymously(Boolean sendMessage, Boolean isOneShot) {
        System.out.print("Enter the sensor's id : ");
        int sensorId = NumberUtils.toInt(sc.nextLine(), defaultNumber);
        if(sensorId != defaultNumber) {
            System.out.println("Enter the min threshold : ");
            float minThreshold = NumberUtils.toFloat(sc.nextLine(), defaultNumber);
            System.out.println("Enter the max threshold : ");
            float maxThreshold = NumberUtils.toFloat(sc.nextLine(), defaultNumber);
            System.out.println("Enter the check frequency in milliseconds : ");
            float frequency = NumberUtils.toFloat(sc.nextLine(), defaultNumber);
            if(minThreshold != defaultNumber && maxThreshold != defaultNumber && frequency != defaultNumber) {
                SensorSignal signal = new SensorSignal(sensorId,
                        minThreshold,
                        maxThreshold,
                        (long) frequency, true);
                signal.setSendMessage(sendMessage);
                setSignal(signal, null, null);
            }
            else {
                System.out.println("A value is incorrect");
            }
        }
    }

    private void setSignal(SensorSignal newSignal, SensorType type, Float code) {
        int id = newSignal.getSensorId();
        SensorSignal signal = null;
        if(type != null && type == SensorType.ACCESS_CONTROL && code != null) {
            sendMessage(new Message(id, code));
        }
        else if(sensorSignalMap.containsKey(id)){
            signal = sensorSignalMap.get(id);
            signal.clone(newSignal);
            sensorSignalMap.put(id, newSignal);
        }
        else {
            sensorSignalMap.put(id, newSignal);
            Thread thread = new Thread(newSignal);
            thread.start();
        }
    }



    public static boolean sendMessage(Message message) {
        try {
            ClientSocket clientSocket = new ClientSocket();
            ConnectionStates connectionState = clientSocket.start();
            if(connectionState == ConnectionStates.SUCCESS) {
                String serializedObject = JsonUtil.serializeObject(message, message.getClass(), "");
                String jsonRequest = JsonUtil.serializeRequest(RequestTypes.INSERT, message.getClass(), serializedObject, null, null,null, RequestSender.SENSOR);
                clientSocket.sendRequestToServer(jsonRequest);
                return true;
            }
            else {
                log.error("An error occurred during the connection with the server. Perhaps the server is off.");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;

    }
}
