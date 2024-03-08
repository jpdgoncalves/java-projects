package jpdgoncalves.iotdatasim.sensor;

import java.util.Random;

import jpdgoncalves.iotdatasim.base.SensorSimulator;

/**
 * Simulator for a sensor atmospheric pressure.
 * Its measures come in hPa (Pascal) units.
 */
public class DefaultAtmPressSensor implements SensorSimulator<Double> {
    private double maxPress = 1085.0;
    private double minPress = 870.0;
    private double variance = 1.0;
    private long minTransitionTicks = 1 * 60 * 60;
    private long maxTransitionTicks = 6 * 60 * 60;
    private long changeTargetTicks = 6 * 60 * 60 + 30 * 60;

    private volatile double realMeasure;
    private double internalMeasure = minPress;
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
    public DefaultAtmPressSensor(long seed) {
        random = new Random(seed);
        tick();
    }

    /**
     * Set the max pressure the simulator
     * will generate.
     * 
     * @param maxPress The max temperature the simulator
     *                 may generate.
     */
    public void setMaxPress(double maxPress) {
        this.maxPress = maxPress;
    }

    /**
     * Set the minimum pressure the simulator
     * will generate.
     * 
     * @param minPress The minimum pressure the simulator
     *                may generate.
     */
    public void setMinPress(double minPress) {
        this.minPress = minPress;
    }

    /**
     * Set by how much the pressure varies
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
     * The minimum amount of ticks that will take to go
     * from a starting temperature to the target.
     * 
     * @param minTransitionTicks
     */
    public void setMinTransitionTicks(long minTransitionTicks) {
        this.minTransitionTicks = minTransitionTicks;
    }

    /**
     * The maximum amount of ticks that will take
     * to go from a starting temperature to the target.
     * 
     * @param maxTransitionTicks
     */
    public void setMaxTransitionTicks(long maxTransitionTicks) {
        this.maxTransitionTicks = maxTransitionTicks;
    }

    /**
     * The number of ticks that takes to change the
     * target temperature.
     * 
     * @param changeTargetTicks
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
            target = random.nextDouble(minPress, maxPress);
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
