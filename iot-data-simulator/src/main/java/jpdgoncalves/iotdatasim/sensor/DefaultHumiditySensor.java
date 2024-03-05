package jpdgoncalves.iotdatasim.sensor;

import java.util.Random;

import jpdgoncalves.iotdatasim.base.SensorSimulator;

/**
 * Default sensor simulator that produces
 * humidity percentage measurements. Every six
 * hours, it will generate a random number between
 * a minimum and maximum humidity percentage. The sensor will
 * then slowly converge to each target with each
 * call to the generateNextValue Method.
 */
public class DefaultHumiditySensor implements SensorSimulator<Double> {

    private static final long INTERVAL = 1000L * 60L * 60L * 6L;
    private static final double STEP = 1.0 / 1000;
    private static final double MIN_DELTA = 1.0 / 3600;

    private final Random generator;
    private final double minHumidity;
    private final double maxHumidity;
    
    private double target;
    private double current;
    private long targetTimestamp;

    /**
     * Create an instance of the default humidity sensor.
     * @param minHumidity Minimum humidity percentage.
     * @param maxHumidity Maximum humidity percentage.
     * @param seed Seed random number generator that generates
     * a humidity percentage target.
     */
    public DefaultHumiditySensor(double minHumidity, double maxHumidity, long seed) {
        this.generator = new Random(seed);
        this.minHumidity = minHumidity;
        this.maxHumidity = maxHumidity;
        target = minHumidity + generator.nextDouble() * (maxHumidity - minHumidity);
        current = target;
        targetTimestamp = System.currentTimeMillis();
    }

    /**
     * Generate a humidity percentage data point
     * for this humidity sensor.
     */
    @Override
    public Double generateNextValue() {
        long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp - targetTimestamp >= INTERVAL) {
            targetTimestamp = currentTimestamp;
            target = minHumidity + generator.nextDouble() * (maxHumidity - minHumidity);
        }

        double delta = (target - current) * STEP;
        delta = Math.abs(delta) < MIN_DELTA ? Math.signum(delta) * MIN_DELTA : delta;
        current += delta;
        return current;
    }

}
