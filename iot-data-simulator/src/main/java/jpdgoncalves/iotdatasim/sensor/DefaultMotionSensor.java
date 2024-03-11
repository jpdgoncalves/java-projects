package jpdgoncalves.iotdatasim.sensor;

import java.util.Random;

import jpdgoncalves.iotdatasim.base.SensorSimulator;

/**
 * Default simulator for a motion sensor. It outputs
 * true if movement was detected and false if it wasn't.
 */
public class DefaultMotionSensor implements SensorSimulator<Boolean> {

    private long minStillTicks = 1 * 60;
    private long maxStillTicks = 2 * 60;
    private long minMoveTicks = 10;
    private long maxMoveTicks = 30;

    private volatile boolean detected = false;
    private final Random random;
    private long changeCountdown = 0;

    /**
     * Create an instance of this simulator.
     * 
     * @param seed Controls the random
     *             component of the simulator.
     */
    public DefaultMotionSensor(long seed) {
        random = new Random(seed);
        tick();
    }

    /**
     * Set the minimum amount of ticks the
     * sensor doesn't detect movement.
     * 
     * @param minStillTicks The minimum amount of ticks the
     * sensor doesn't detect movement.
     */
    public void setMinStillTicks(long minStillTicks) {
        this.minStillTicks = minStillTicks;
    }

    /**
     * Sets the maximum amount of ticks
     * the sensor doesn't detect movement.
     * 
     * @param maxStillTicks The maximum amount of ticks
     * the sensor doesn't detect movement.
     */
    public void setMaxStillTicks(long maxStillTicks) {
        this.maxStillTicks = maxStillTicks;
    }

    /**
     * Set the minimum amount of ticks the
     * sensor detects movement.
     * 
     * @param minMoveTicks The minimum amount of ticks the
     * sensor detects movement.
     */
    public void setMinMoveTicks(long minMoveTicks) {
        this.minMoveTicks = minMoveTicks;
    }

    /**
     * Set the maximum amount of ticks the
     * sensor detects movement.
     * 
     * @param maxMoveTicks The maximum amount of ticks the
     * sensor detects movement.
     */
    public void setMaxMoveTicks(long maxMoveTicks) {
        this.maxMoveTicks = maxMoveTicks;
    }

    /**
     * Read the value on wether movement
     * was detected or not.
     */
    @Override
    public Boolean readValue() {
        return detected;
    }

    /**
     * Runs a single tick for this simulator.
     * Each tick represent one second.
     */
    @Override
    public void tick() {
        if (changeCountdown > 0) {
            changeCountdown -= 1;
        } else {
            changeCountdown = detected ? random.nextLong(minStillTicks, maxStillTicks + 1)
                    : random.nextLong(minMoveTicks, maxMoveTicks + 1);
            detected = !detected;
        }
    }
}
