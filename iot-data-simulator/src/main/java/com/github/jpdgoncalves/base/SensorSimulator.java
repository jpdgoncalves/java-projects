package com.github.jpdgoncalves.base;

/**
 * Classes implementing this interface represent
 * a simulator for some particular kind of sensor
 * capable of making measurement of the real world
 * like temperature or humidity.
 * @param <T> The type of data that the sensor will produce
 */
public interface SensorSimulator<T> {

    /**
     * Method used to ask the sensor simulator
     * for a measurement.
     * @return The value of the measurement requested
     * at the time of the request.
     */
    public T generateNextValue();
}