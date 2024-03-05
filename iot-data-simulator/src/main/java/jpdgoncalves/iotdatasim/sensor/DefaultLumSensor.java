package jpdgoncalves.iotdatasim.sensor;

import jpdgoncalves.iotdatasim.base.SensorSimulator;

/**
 * Default implementation of a simulator for a
 * luminance sensor. It produces values whose
 * unit is lux or lumen per meter squared
 */
public class DefaultLumSensor implements SensorSimulator<Integer> {

    /**
     * Produces a single simulated measurement
     * of the current luminosity.
     */
    @Override
    public Integer generateNextValue() {
        return 1000;
    }

}
