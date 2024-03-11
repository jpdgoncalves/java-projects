package jpdgoncalves.iotdatasim.sensor;

import java.util.Random;

import jpdgoncalves.iotdatasim.base.SensorSimulator;

/**
 * Default sensor simulator that produces
 * humidity percentage measurements.
 */
public class DefaultHumiditySensor implements SensorSimulator<Double> {

    private double maxHumidity = 100.0;
    private double minHumidity = 10.0;
    private double variance = 1.0;
    private long minTransitionTicks = 1 * 60 * 60;
    private long maxTransitionTicks = 6 * 60 * 60;
    private long changeTargetTicks = 6 * 60 * 60 + 30 * 60;

    private volatile double realMeasure;
    private double internalMeasure = minHumidity;
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
    public DefaultHumiditySensor(long seed) {
        random = new Random(seed);
        tick();
    }

    /**
     * Set the max humidity the simulator
     * will generate.
     * 
     * @param maxHumidity The max humidity the simulator
     *                    may generate.
     */
    public void setMaxHumidity(double maxHumidity) {
        this.maxHumidity = maxHumidity;
    }

    /**
     * Set the minimum humidity the simulator
     * will generate.
     * 
     * @param minHumidity The minimum temperature the
     * simulator may generate.
     */
    public void setMinHumidity(double minHumidity) {
        this.minHumidity = minHumidity;
    }

    /**
     * Set by how much the humidity varies
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
     * from a starting humidity to the target.
     * 
     * @param minTransitionTicks The minimum amount of ticks that will take to go
     * from a starting humidity to the target.
     */
    public void setMinTransitionTicks(long minTransitionTicks) {
        this.minTransitionTicks = minTransitionTicks;
    }

    /**
     * Set the maximum amount of ticks that will take
     * to go from a starting humidity to the target.
     * 
     * @param maxTransitionTicks The maximum amount of ticks that will take
     * to go from a starting humidity to the target.
     */
    public void setMaxTransitionTicks(long maxTransitionTicks) {
        this.maxTransitionTicks = maxTransitionTicks;
    }

    /**
     * Set the number of ticks that takes to change the
     * target humidity.
     * 
     * @param changeTargetTicks The number of ticks that takes to change the
     * target humidity.
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
            target = random.nextDouble(minHumidity, maxHumidity);
            delta = (target - internalMeasure) / transitionCountdown;
            realMeasure = random.nextDouble(internalMeasure - variance, internalMeasure + variance);
            realMeasure = realMeasure > 100.0 ? 100.0 : realMeasure;
            realMeasure = realMeasure < 0.0 ? 0.0 : realMeasure;
        } else if (transitionCountdown > 0 && Math.signum(target - internalMeasure) == Math.signum(delta)) {
            changeCountdown -= 1;
            transitionCountdown -= 1;
            internalMeasure += delta;
            realMeasure = random.nextDouble(internalMeasure - variance, internalMeasure + variance);
            realMeasure = realMeasure > 100.0 ? 100.0 : realMeasure;
            realMeasure = realMeasure < 0.0 ? 0.0 : realMeasure;
        } else {
            changeCountdown -= 1;
            transitionCountdown = 0;
            internalMeasure = target;
            realMeasure = random.nextDouble(internalMeasure - variance, internalMeasure + variance);
            realMeasure = realMeasure > 100.0 ? 100.0 : realMeasure;
            realMeasure = realMeasure < 0.0 ? 0.0 : realMeasure;
        }
    }

}
