package mock;

import com.appd.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.sleep;

public class SensorSignal implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(SensorSignal.class);
    private int sensorId;
    private float minTreshold;
    private float maxTreshold;
    private long interval;
    private boolean isDefaultMessage;
    private boolean sendMessage;

    public SensorSignal(int sensorId, float minTreshold, float maxTreshold, long interval, boolean isDefaultMessage) {
        this.sensorId = sensorId;
        this.minTreshold = minTreshold;
        this.maxTreshold = maxTreshold;
        this.interval = interval;
        this.isDefaultMessage = isDefaultMessage;
        sendMessage = true;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    public float getMinTreshold() {
        return minTreshold;
    }

    public void setMinTreshold(float minTreshold) {
        this.minTreshold = minTreshold;
    }

    public float getMaxTreshold() {
        return maxTreshold;
    }

    public void setMaxTreshold(float maxTreshold) {
        this.maxTreshold = maxTreshold;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public boolean isDefaultMessage() {
        return isDefaultMessage;
    }

    public void setDefaultMessage(boolean defaultMessage) {
        isDefaultMessage = defaultMessage;
    }

    public boolean isSendMessage() {
        return sendMessage;
    }

    public void setSendMessage(boolean sendMessage) {
        this.sendMessage = sendMessage;
    }


    @Override
    public void run() {

        float treshold = minTreshold;

        try {
            while (true) {
                if (isDefaultMessage) {
                    treshold = (float) (minTreshold + Math.random() * (maxTreshold - minTreshold));
                } else if (!isDefaultMessage) {
                    treshold = (float) (maxTreshold + Math.random() * (maxTreshold - minTreshold));
                }

                boolean messageSent = MockSignal.sendMessage(new Message(sensorId, treshold));
                if (!messageSent) {
                    throw new Exception("Failed reaching server");
                }
                Thread.sleep(interval);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
