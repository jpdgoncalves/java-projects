package com.github.jpdgoncalves.sensor;

import com.github.jpdgoncalves.base.SensorSimulator;

/**
 * Default sensor simulator that produces
 * humidity percentage measurements
 */
public class DefaultHumiditySensor implements SensorSimulator<Double> {

    /**
     * Instantiates the Default Humidity
     * sensor.
     */
    public DefaultHumiditySensor() {}

    @Override
    public Double generateNextValue() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateNextValue'");
    }

}
