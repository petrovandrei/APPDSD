package com.appd.alert;

import com.appd.connection.pool.implementation.DataSource;
import com.appd.dao.entityDao.DAOFactory;
import com.appd.entity.Message;
import com.appd.entity.Person;
import com.appd.entity.SensorConfiguration;
import com.appd.enumeration.RequestTypes;
import com.appd.enumeration.SensorActivity;
import com.appd.enumeration.SensorState;
import com.appd.util.Util;
import org.joda.time.DateTimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.*;

public class AlertHandler {

    private static final Logger log = LoggerFactory.getLogger(AlertHandler.class);
    private final List<String> fieldsForActiveSensors = Arrays.asList("ACTIVITY", "START_ACTIVITY_TIME", "END_ACTIVITY_TIME");
    private final List<String> testsForActiveSensors = Arrays.asList("=", "<=", ">=");
    private List<String> valuesForActiveSensors;
    private Map<Integer, Cache> cacheInfoBySensor;
    private List<SensorConfiguration> activeSensors;
    private final long updateListFrequency = 3000;
    private long sensorActivityCheckerSleepTime= 3000;

    private Thread listUpdaterThread;
    private long counter;
    private int maxWarningMessage;

    public AlertHandler(){
        cacheInfoBySensor = Collections.synchronizedMap(new HashMap<Integer, Cache>());
        activeSensors = Collections.synchronizedList(new ArrayList<SensorConfiguration>());
        List<Person> persons = Collections.synchronizedList(new ArrayList<Person>());
        counter = 0;
        //complementarySensorDictionnary = new ComplementarySensorDictionnary();

    }

    public void startThreads() {
        activeSensorListUpdater();
        sensorActivityChecker();
        //codeRetriver();
    }

    public synchronized void activeSensorListUpdater() {
        listUpdaterThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while(true) {
                    if(counter <= 0) {
                        updateSensorsList();
                    }else {
                        try {
                            Thread.sleep(DateTimeConstants.MILLIS_PER_SECOND);
                            long sleepTime = 0;
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
        for(SensorConfiguration sensor : activeSensors) {
            sensorId = sensor.getSensorConfigurationId();
            info = cacheInfoBySensor.get(sensorId);
            if(info != null && info.getSensorState() == sensorState) {
                results.add(sensor);
            }
        }

        return results;
    }

    public void updateSensorsList() {
        try {
            Connection connection = DataSource.getConnection();
            if(connection != null) {
                updateSensorsSearchValues();
                activeSensors.clear();
                activeSensors.addAll((List<SensorConfiguration>) DAOFactory.execute(connection, SensorConfiguration.class, RequestTypes.SELECT, null, fieldsForActiveSensors, valuesForActiveSensors, testsForActiveSensors));
                log.info("Active sensors list updated");
                //Util.displayListElements(activeSensors, "====> ");
                DataSource.putConnection(connection);
                //clearCacheSensorStateBySensor();
            }
        } catch (Exception e) {
            log.error("An error occured during the update of the active sensors list : " + e.getMessage());
        }
        counter = updateListFrequency;
    }

    private void updateSensorsSearchValues() {
        String nowTimeCasted = "cast('" + Util.getCurrentTimeUTC() +  "' as time)";
        valuesForActiveSensors = Arrays.asList(SensorActivity.ENABLED.toString(), nowTimeCasted, nowTimeCasted);
    }


    private void sensorActivityChecker() {
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
    }

    public synchronized void processMessage(Message receivedMessage) {
        //TODO
    }


}
