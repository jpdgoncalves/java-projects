package jpdgoncalves.iotdatasim.sensor;

import jpdgoncalves.iotdatasim.base.SensorSimulator;

/**
 * Simulator for a sensor atmospheric pressure.
 * Its measures come in Pa (Pascal) units.
 */
public class DefaultAtmPressSensor implements SensorSimulator<Integer> {

    /**
     * Creates an instance of a simulated
     * atmospheric pressure sensor.
     */
    public DefaultAtmPressSensor() {}

    /**
     * Generates an atmospheric pressure
     * measure.
     */
    @Override
    public Integer generateNextValue() {
        return 101325;
    }

}
