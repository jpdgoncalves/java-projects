package jpdgoncalves.iotdatasim.sensor;

import java.util.Random;

import jpdgoncalves.iotdatasim.base.SensorSimulator;

/**
 * Default simulator for a temperature sensor.
 */
public class DefaultTempSensor implements SensorSimulator<Double> {

    private double maxTemp = 32.0;
    private double minTemp = 10.0;
    private double variance = 1.0;
    private long minTransitionTicks = 1 * 60 * 60;
    private long maxTransitionTicks = 6 * 60 * 60;
    private long changeTargetTicks = 6 * 60 * 60 + 30 * 60;

    private volatile double realMeasure;
    private double internalMeasure = minTemp;
    private double target;
    private double delta;
    private double transitionCountdown = 0;
    private long changeCountdown = 0;
    private final Random random;

    /**
     * Create an instance for this simulator.
     * 
     * @param seed The seed that controls its
     *             randomness.
     */
    public DefaultTempSensor(long seed) {
        random = new Random(seed);
        tick();
    }

    /**
     * Set the max temperature the simulator
     * will generate.
     * 
     * @param maxTemp The max temperature the simulator
     *                may generate.
     */
    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }

    /**
     * Set the minimum temperature the simulator
     * will generate.
     * 
     * @param minTemp The minimum temperature the simulator
     * will generate.
     */
    public void setMinTemp(double minTemp) {
        this.minTemp = minTemp;
    }

    /**
     * Set by how much the temperature varies
     * from the expected value. x values will cause
     * the variance between -x and x.
     * 
     * @param variance The variance applied to the
     *                 temperature after calculation.
     */
    public void setVariance(double variance) {
        this.variance = variance;
    }

    /**
     * Set the minimum amount of ticks that will take to go
     * from a starting temperature to the target.
     * 
     * @param minTransitionTicks The minimum amount of ticks that will take to go
     * from a starting temperature to the target.
     */
    public void setMinTransitionTicks(long minTransitionTicks) {
        this.minTransitionTicks = minTransitionTicks;
    }

    /**
     * Set the maximum amount of ticks that will take
     * to go from a starting temperature to the target.
     * 
     * @param maxTransitionTicks The maximum amount of ticks that will take
     * to go from a starting temperature to the target.
     */
    public void setMaxTransitionTicks(long maxTransitionTicks) {
        this.maxTransitionTicks = maxTransitionTicks;
    }

    /**
     * Set the number of ticks that takes to change the
     * target temperature.
     * 
     * @param changeTargetTicks The number of ticks that takes to change the
     * target temperature.
     */
    public void setChangeTargetTicks(long changeTargetTicks) {
        this.changeTargetTicks = changeTargetTicks;
    }

    /**
     * Read the latest measured temperature.
     */
    @Override
    public Double readValue() {
        return realMeasure;
    }

    /**
     * Ticks the sensor simulator. Each
     * tick represents one second.
     */
    @Override
    public void tick() {
        if (changeCountdown <= 0) {
            changeCountdown = changeTargetTicks;
            transitionCountdown = random.nextLong(minTransitionTicks, maxTransitionTicks + 1);
            target = random.nextDouble(minTemp, maxTemp);
            delta = (target - internalMeasure) / transitionCountdown;
            realMeasure = random.nextDouble(internalMeasure - variance, internalMeasure + variance);
        } else if (transitionCountdown > 0 && Math.signum(target - internalMeasure) == Math.signum(delta)) {
            changeCountdown -= 1;
            transitionCountdown -= 1;
            internalMeasure += delta;
            realMeasure = random.nextDouble(internalMeasure - variance, internalMeasure + variance);
        } else {
            changeCountdown -= 1;
            transitionCountdown = 0;
            internalMeasure = target;
            realMeasure = random.nextDouble(internalMeasure - variance, internalMeasure + variance);
        }
    }
}
