package jpdgoncalves.iotdatasim.sensor;

import java.util.Random;

import jpdgoncalves.iotdatasim.base.SensorSimulator;

/**
 * Default implementation of a simulator for a
 * luminance sensor. It produces values whose
 * unit is lux or lumen per meter squared.
 * <p>
 * The function used to generate the value is
 * a bit cumbersome. It makes use of three
 * types of functions in the following way.
 * <p>
 * w(x) = 2 * sin (x*pi/12 + 3*pi/2) gives
 * us a function that has a 24 hour period
 * and whose minimum starts at 0.
 * <p>
 * f(x) = 1 / (1 + 10 ^ (-w(x))) this function
 * has long periods where it stays close to the
 * maximum and minimum divided. The transitions
 * between maximum and minimum are smooth and not
 * to abrupt.
 * <p>
 * z(x) = 10 ^ (-3 + 8 * f(x)) the function that
 * actually provides us with the luminance
 * measurement. It's values go between 0.001 and
 * 100_000 lumen. The transition between these
 * values follows f(x).
 * <p>
 * We use variance to make the values have a
 * bit of randomness compared to the actual
 * measurement.
 */
public class DefaultLumSensor implements SensorSimulator<Double> {

    private static final double VARIANCE = 0.01;

    private Random generator;

    /**
     * Creates an instance of the default
     * simulator for a luminance sensor.
     * @param seed Parameter that controls
     * the internal random generator.
     */
    public DefaultLumSensor(long seed) {
        this.generator = new Random(seed);
    }

    /**
     * Produces a single simulated measurement
     * of the current luminosity.
     */
    @Override
    public Double generateNextValue() {
        double hour = ((System.currentTimeMillis() / (1000.0 * 60.0 * 60.0)) % 24.0);
        double wx = 2 * Math.sin((hour * Math.PI)/12 + (3 * Math.PI)/12);
        double fx = 1 / (1 + Math.pow(10, wx));
        double measurement = Math.pow(10, (-3 + 8 * fx));
        

        return measurement + this.generator.nextDouble(measurement * -VARIANCE, measurement * VARIANCE);
    }

}
