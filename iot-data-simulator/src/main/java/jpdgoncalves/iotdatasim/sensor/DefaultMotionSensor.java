package jpdgoncalves.iotdatasim.sensor;

import java.util.Random;

import jpdgoncalves.iotdatasim.base.SensorSimulator;

/**
 * Default simulator for a motion sensor. It outputs
 * true if movement was detected and false if it wasn't.
 * The simulation occurs by having small periods of time
 * in which movement is detected and long periods of time
 * in which it isn't.
 */
public class DefaultMotionSensor implements SensorSimulator<Boolean> {

    private static final long MOVE_MIN_INTER = 1000 * 3;
    private static final long MOVE_MAX_INTER = 1000 * 5;
    private static final long STILL_MIN_INTER = 1000 * 15;
    private static final long STILL_MAX_INTER = 1000 * 30;
    private static final double MOVE_CHANCE = 0.2;

    private final Random generator;
    private boolean detected = false;
    private long lastChangeTimestamp;
    private long intervalUntilChange;

    /**
     * Creates an instance of a motion sensor simulator.
     * 
     * @param seed The seed that controls the randomness of this sensor.
     */
    public DefaultMotionSensor(long seed) {
        this.generator = new Random(seed);
        this.detected = this.generator.nextDouble() <= MOVE_CHANCE;
        this.lastChangeTimestamp = System.currentTimeMillis();
        this.intervalUntilChange = this.detected ? this.generator.nextLong(MOVE_MIN_INTER, MOVE_MAX_INTER + 1)
                : this.generator.nextLong(STILL_MIN_INTER, STILL_MAX_INTER + 1);
    }

    /**
     * Generates values that tell whether movement
     * was detected or wasn't.
     */
    @Override
    public Boolean generateNextValue() {
        long currentTimestamp = System.currentTimeMillis();
        if ((currentTimestamp - lastChangeTimestamp) < intervalUntilChange) {
            this.detected = this.generator.nextDouble() <= MOVE_CHANCE;
            this.lastChangeTimestamp = System.currentTimeMillis();
            this.intervalUntilChange = this.detected ? this.generator.nextLong(MOVE_MIN_INTER, MOVE_MAX_INTER + 1)
                    : this.generator.nextLong(STILL_MIN_INTER, STILL_MAX_INTER + 1);
        }

        return this.detected;
    }

}
