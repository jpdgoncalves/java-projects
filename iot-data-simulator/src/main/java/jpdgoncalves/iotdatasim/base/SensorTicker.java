package jpdgoncalves.iotdatasim.base;

import java.util.ArrayList;
import java.util.List;

/**
 * A ticker for sensors. It runs their tick
 * method approximately at the specified
 * period. It may take longer if the tick
 * sensor are particularly expensive but
 * never less.
 */
public class SensorTicker implements Runnable {

    private final long period;
    private List<SensorSimulator<?>> sensors = new ArrayList<>();

    /**
     * Default constructor. Sets the period
     * to 1000 milliseconds.
     */
    public SensorTicker() {
        this(1000);
    };

    /**
     * Creates a ticker that runs the tick
     * methods of the sensors with a period
     * approximately equal to the provided.
     * @param period The desired period to call
     * the tick method of the sensors.
     */
    public SensorTicker(long period) {
        this.period = period;
    }

    /**
     * Adds a sensor to the ticker.
     * @param sensor The sensor whose tick
     * method is to be called periodically.
     */
    public void addSensor(SensorSimulator<?> sensor) {
        sensors.add(sensor);
    }
    
    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                long start = System.currentTimeMillis();
                for (SensorSimulator<?> sensor : sensors) {
                    sensor.tick();
                }
                long end = System.currentTimeMillis();
                long interval = period - (end - start);
                interval = interval > 0 ? interval : 1;
                Thread.sleep(interval);
            }
        } catch (InterruptedException e) {/** Ignore. This tells us to stop the ticker */}
    }    
}
