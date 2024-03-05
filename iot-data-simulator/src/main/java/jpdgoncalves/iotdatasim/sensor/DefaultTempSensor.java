package jpdgoncalves.iotdatasim.sensor;

import jpdgoncalves.iotdatasim.base.SensorSimulator;

/**
 * This temperature sensor runs on the basic idea that at a certain 
 * hour of the day the sun can only heat up the environment
 * up to a certain temperature. 
 * The calculation for the next temperature value is based on the 
 * formula for heat transfer Q/t = kA(T1 - T2)/l.
 */
public class DefaultTempSensor implements SensorSimulator<Double> {

    /**
     * Temperatures to which the simulator will slowly move towards
     * as the day goes on.
     */
    private final double[] maxPossibleTemperatures = new double[] {10,10,10,10,10,10,13,16,19,22,25,29,32,32,32,32,31,31,29,27,22,18,15,13};
    private final double k = 0.1;
    private final double A = 1;
    private final double l = 20;

    private double temperature = 20;
    private long previousTimestamp = System.currentTimeMillis();

    /**
     * Instantiate the Default Temperature sensor.
     */
    public DefaultTempSensor() {}

    @Override
    public Double generateNextValue() {
        long currentTimestamp = System.currentTimeMillis();

        while ((currentTimestamp - previousTimestamp) > 1000) {
            int hour = (int) ((previousTimestamp / (1000 * 60 * 60)) % 24);
            double deltaTemp = k * A * ((maxPossibleTemperatures[hour] - temperature) / l);
            
            temperature += deltaTemp;
            previousTimestamp += 1000;
        }

        return temperature;
    }

    
}
