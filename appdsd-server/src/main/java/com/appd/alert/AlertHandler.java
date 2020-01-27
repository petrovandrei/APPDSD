package com.appd.alert;

import com.appd.connection.pool.implementation.DataSource;
import com.appd.dao.entityDao.DAOFactory;
import com.appd.entity.Message;
import com.appd.entity.Person;
import com.appd.entity.SensorConfigHistory;
import com.appd.entity.SensorConfiguration;
import com.appd.enumeration.*;
import com.appd.util.Util;
import org.joda.time.DateTimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.*;

public class AlertHandler {

    private static final Logger log = LoggerFactory.getLogger(AlertHandler.class);
    private final List<String> fieldsForActiveSensors = Arrays.asList("ACTIVITY");
    private final List<String> testsForActiveSensors = Arrays.asList("=", "<=", ">=");
    private final int sleepTime = DateTimeConstants.MILLIS_PER_SECOND;
    private List<String> valuesForActiveSensors;
    private Map<Integer, Cache> cacheInfoBySensor;
    private List<SensorConfiguration> activeSensors;
    private final long updateListFrequency = DateTimeConstants.MILLIS_PER_MINUTE;
    private long sensorActivityCheckerSleepTime = 10000;
    Util util = new Util();

    private Thread listUpdaterThread;
    private long counter;
    private int warningMessagesNeeded;

    public AlertHandler() {
        cacheInfoBySensor = Collections.synchronizedMap(new HashMap<Integer, Cache>());
        activeSensors = Collections.synchronizedList(new ArrayList<SensorConfiguration>());
        List<Person> persons = Collections.synchronizedList(new ArrayList<Person>());
        counter = 0;
        //complementarySensorDictionnary = new ComplementarySensorDictionnary();

    }

    public void startThreads() {
        activeSensorListUpdater();
/*
        sensorActivityChecker();
*/
        //codeRetriver();
    }

    public synchronized void activeSensorListUpdater() {
        listUpdaterThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    if (counter <= 0) {
                        updateSensorsList();
                    } else {
                        try {
                            Thread.sleep(DateTimeConstants.MILLIS_PER_SECOND);
                            counter -= sleepTime;
                        } catch (InterruptedException e) {
                            log.error(e.getMessage());
                        }
                    }
                }
            }
        });
        listUpdaterThread.start();
    }

    public synchronized List<SensorConfiguration> getCacheSensorsByState(SensorState sensorState) {
        List<SensorConfiguration> results = new ArrayList<SensorConfiguration>();
        Integer sensorId = null;
        Cache info = null;
        for (SensorConfiguration sensor : activeSensors) {
            sensorId = sensor.getSensorConfigurationId();
            info = cacheInfoBySensor.get(sensorId);
            if (info != null && info.getSensorState() == sensorState) {
                results.add(sensor);
            }
        }

        return results;
    }
    private void updateSensorsSearchValues() {
        String nowTimeCasted = "cast('" + Util.getCurrentTimeUTC() + "' as time)";
        valuesForActiveSensors = Arrays.asList(SensorActivity.ENABLED.toString());
    }

    public void updateSensorsList() {
        try {
            Connection connection = DataSource.getConnection();
            //log.info(connection.toString());
            if (connection != null) {
                updateSensorsSearchValues();
                activeSensors.clear();
                activeSensors.addAll((List<SensorConfiguration>) DAOFactory.execute(connection, SensorConfiguration.class, RequestTypes.SELECT, null, fieldsForActiveSensors, valuesForActiveSensors, testsForActiveSensors));
                log.info("Active sensors list updated");

                DataSource.putConnection(connection);
            }
        } catch (Exception e) {
            log.error("An error occured during the update of the active sensors list : " + e.getMessage());
        }
        counter = updateListFrequency;
    }




  /*private void sensorActivityChecker() {
        Thread thread = new Thread(new Runnable() {


            @Override
            public void run() {
                Set<Integer> missingSensorsId = new HashSet<Integer>();
                try {
                    missingSensorsId.clear();
                    //Sleeps this thread so that the active sensors list can be filled
                    long sleepTime = 0;
                    Thread.sleep(sleepTime);
                    while (true) {
                        for (SensorConfiguration sensor : activeSensors) {
                            int id = sensor.getSensorConfigurationId();
                            Cache info = cacheInfoBySensor.get(id);
                            if (info == null) {
                                info = new Cache(null, null, SensorState.MISSING, 0);
                                cacheInfoBySensor.put(id, info);
                                missingSensorsId.add(id);
                            } else {
                                Timestamp lastMessageDate = info.getLastMessageDate();
                                if (lastMessageDate == null ||
                                        (Util.getCurrentTimestamp().getTime() - lastMessageDate.getTime()) > sensor.getCheckFrequency()) {
                                    info.reset();
                                    info.setLastMessageDate(null);
                                    info.setFirstDangerMessageDate(null);
                                    info.setSensorState(SensorState.MISSING);
                                    cacheInfoBySensor.put(id, info);
                                    missingSensorsId.add(id);
                                }
                            }
                        }
                        log.info("All active sensors have been checked");
                        String missingMessage = "";
                        if (missingSensorsId.size() > 0) {
                            missingMessage += "There are some missing sensors : ";
                            for (Integer i : missingSensorsId) {
                                missingMessage += i + "; ";
                            }
                            log.info(missingMessage);
                        }

                        Thread.sleep(sensorActivityCheckerSleepTime);
                    }

                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        });
        thread.start();
    }*/

    public synchronized Map<SensorState, List<SensorConfiguration>> getActiveSensorsByState() {
        Map<SensorState, List<SensorConfiguration>> map = new TreeMap<>();

        for (SensorConfiguration sensor : activeSensors) {
            Integer sensorID = sensor.getSensorConfigurationId();
            Cache info = cacheInfoBySensor.get(sensorID);
            SensorState state = (info != null) ? info.getSensorState() : SensorState.MISSING;
            List<SensorConfiguration> sensors = map.get(state);
            if (sensors != null) {
                sensors.add(sensor);
            } else {
                sensors = new ArrayList<>();
                sensors.add(sensor);
            }
            map.put(state, sensors);

        }
        return map;
    }

    private SensorState checkSensorState(SensorConfiguration sensorConfiguration, Float thresholdReached) {
        Float maxThreshold = sensorConfiguration.getMaxDangerThreshold();
        Float minThreshold = sensorConfiguration.getMinDangerThreshold();
        int sensorId = sensorConfiguration.getSensorConfigurationId();
        SensorType sensorType = sensorConfiguration.getSensorType();

        if (thresholdReached >= maxThreshold || thresholdReached <= minThreshold) {
            return SensorState.WARNING;
        } else if (thresholdReached.intValue() == minThreshold.intValue() || sensorType.isGapAcceptable() || sensorType.isBinary()) {
            return SensorState.DEFAULT;
        } else
            return SensorState.CAUTION;
    }

    /*private void saveSensorConfigurationHistory(SensorConfiguration sensor, SensorAction action, String message, Timestamp messageDate, boolean isAlertEnded) {
        int id = sensor.getSensorConfigurationId();
        try {
            Connection connection = DataSource.getConnection();
            if (connection != null) {
                SensorConfigHistory sensorHistory = null;
                // update alert history
                if (isAlertEnded) {
                    Integer wantedId = cacheInfoBySensor.get(sensor.getSensorConfigurationId()).getHistory();
                    sensorHistory = ((List<SensorConfigHistory>) DAOFactory.execute(connection, SensorConfigHistory.class, RequestTypes.SELECT, null,
                            Arrays.asList("ID_HISTORY"), Arrays.asList(wantedId.toString()), null)).get(0);
                    if (sensorHistory != null) {
                        sensorHistory.setEndAlertDate(messageDate);
                        sensorHistory.setActionDone(action);
                        DAOFactory.execute(connection, sensorHistory.getClass(), RequestTypes.UPDATE, sensorHistory, null, null, null);
                        log.info("The end date of the alert by the sensor n° " + id + " has been updated");
                    }
                    Cache info = cacheInfoBySensor.get(id);
                    info.reset();
                    cacheInfoBySensor.put(id, info);
                } else {
                    sensorHistory = new SensorConfigHistory(sensor, cacheInfoBySensor.get(id).getThresholdReached(), messageDate, message, null);
                    SensorConfigHistory result = (SensorConfigHistory) DAOFactory.execute(connection, sensorHistory.getClass(), RequestTypes.INSERT, sensorHistory, null, null, null);
                    cacheInfoBySensor.get(sensor.getSensorConfigurationId()).setHistory(result.getIdHistory());
                    log.info("The begin date of the alert by  sensor n° " + id + " has been registered");
                }

                DataSource.putConnection(connection);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }*/

    /*private void clearCacheSensorStateBySensor() {
        // on veut pouvoir modifier la map pendant la boucle while, d'où l'utilisation d'un iterateur
        Iterator<Map.Entry<Integer, Cache>> iterator = cacheInfoBySensor.entrySet().iterator();
        boolean isInActiveSensorList = false;
        while (iterator.hasNext()) {
            Map.Entry<Integer, Cache> entry = (Map.Entry<Integer, Cache>) iterator.next();
            isInActiveSensorList = false;
            int id = entry.getKey();
            for (SensorConfiguration sensor : activeSensors) {
                //Allows to remove from the cache the sensors whose start_time and end_time is not correct
                if (sensor.getSensorConfigurationId() == id) {
                    isInActiveSensorList = true;
                    break;
                }
            }*/

            /*if (!isInActiveSensorList) {
                iterator.remove();
            }
        }
        log.info("Cache empty");
    }

    public List<SensorConfiguration> getActiveSensors() {
        return activeSensors;
    }*/


    public synchronized void processMessage(Message receivedMessage) {
        try {
            updateSensorsList();
            log.info(receivedMessage.toString());
            int sensorId = receivedMessage.getSensorId();
            SensorState sensorState = null;
            log.info("Message reveived from the sensor n°" + sensorId);
            Timestamp messageDate = receivedMessage.getCreationDate();
            float thresholdReached = receivedMessage.getThresholdReached();
            SensorConfiguration sensorConfiguration = null;
            // Searchs the sensor which has sent a message among the active sensors
            for(SensorConfiguration sensor : activeSensors) {
                if(sensor.getSensorConfigurationId() == sensorId) {
                    sensorConfiguration = sensor;
                    break;
                }
            }

            if(sensorConfiguration != null) {
                SensorType type = sensorConfiguration.getSensorType();
                String message = "";
                warningMessagesNeeded = SensorSensitivity.getNumberOfMessages(sensorConfiguration.getSensorSensitivity());

                //Checks the state of the sensor just with the data received from the message
                sensorState = checkSensorState(sensorConfiguration, thresholdReached);

                Cache info = cacheInfoBySensor.get(sensorId);
                //If the info is in the cache
                if(info != null) {
                    info.setThresholdReached(thresholdReached);
                    Timestamp previousMessageTime = info.getLastMessageDate();
                    // Case : the interval between the two message is correct
                    if(previousMessageTime != null && (messageDate.getTime() - previousMessageTime.getTime()) <= sensorConfiguration.getCheckFrequency())
                    {
                        // Case : a warning is detected
                        if(sensorState == SensorState.WARNING) {
                            log.info("The sensor n°" + sensorId + " send another warning message");
                            sensorState = info.addWarning(warningMessagesNeeded, messageDate);
                            if(sensorState == SensorState.DANGER) {
                                log.info("=======> Sensor n° " + sensorConfiguration.getSensorConfigurationId() + " est en danger");
                                if(!info.isInDanger()) {

/*
                                    saveSensorConfigurationHistory(sensorConfiguration, null, "", info.getFirstDangerMessageDate(), false);
*/
                                    info.setInDanger(true);

                                }
                            }
                        }
                        else if(sensorState == SensorState.DEFAULT)
                        {
                            Integer numberOfWarning = info.getWarningCount();
                            // Case : Previously we had a warning
                            Timestamp dangerTime = info.getFirstDangerMessageDate();
                            // Case : The reparator have done their jobs
                            if(numberOfWarning >= warningMessagesNeeded) {
                                System.err.println(" /!\\ The maintainers repaired the sensor n°" + sensorId + " /!\\" );
/*
                                saveSensorConfigurationHistory(sensorConfiguration, SensorType.getActionAssociatedToStopDanger(sensorConfiguration.getSensorType()), "", dangerTime, true);
*/
                            }
                            else if(numberOfWarning > 0 && numberOfWarning < warningMessagesNeeded && !type.isBinary() && type != SensorType.ACCESS_CONTROL) {
                                log.info("A fake alert is detected for the sensor n°" + sensorId);
/*
                                saveSensorConfigurationHistory(sensorConfiguration, SensorAction.FAKE_ALERT, "", dangerTime, true);
*/
                            }
                            // Removes the warning and Removes the first danger alert date
                            info.reset();
                        }
                        else {
                            if(sensorState == SensorState.CAUTION)
                                info.reset();
                            info.setSensorState(sensorState);
                        }
                    }
                    // Case : the last alert in the cache was too old or there was not any message in the cache
                    else
                    {
                        if(sensorState == SensorState.WARNING) {
                            // Sets the numbers of warning message sent by this sensor
                            info.addWarning(warningMessagesNeeded);
                        }
                        else
                            info.setSensorState(sensorState);
                    }

                } else {
                    log.info(messageDate.toString());
                    info = new Cache(messageDate, null, sensorState, thresholdReached);
                    log.info("The sensor n°" + sensorId + " did not send any message or did it a long time ago");
                }
                //Sets the last warning sent by this sensor
                info.setLastMessageDate(messageDate);
                // Puts or updates the info in the cache
                cacheInfoBySensor.put(sensorId, info);
                System.out.println(cacheInfoBySensor.toString());
                //Displays the info on the console
                log.info("Info about location n°" + sensorConfiguration.getLocationId());

                if(type.isBinary()) {
                    String messageForBinary = type.getMessageAccordingToState(info.getSensorState());



                }
                else {
                    String unit = sensorConfiguration.getMeasurementUnit();
                    if(unit == null)
                        unit = "";

                }

                System.out.println(message);
            } else {
                log.error("The sensor n°" + sensorId + " sent a message but it should not ! Maybe this sensor does not exist");
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }



}
