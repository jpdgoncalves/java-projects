package com.github.jpdgoncalves.base;

/**
 * Abstract class used to implement a Data Emitter.
 * A Data Emitter is an Emitter of type T.
 * @param <T> The type of data the emitter will produce.
 */
public abstract class DataEmitter<T> implements Runnable {

    private SensorSimulator<T> sensor;

    /**
     * Should not be invoked as this is an abstract class.
     */
    public DataEmitter() {}

    /**
     * Method to sensor simulator that will produce the
     * data for the emitter.
     * @param sensor The sensor that will produce the data
     * @throws NullPointerException When the user attempts to
     * pass null to the method.
     */
    public void setSensor(SensorSimulator<T> sensor) throws NullPointerException {
        if (sensor == null) throw new NullPointerException("Sensor Simulator can't be null");
        this.sensor = sensor;
    }

    /**
     * Gets the sensor simulator
     * @return The sensor simulator that will produce the data.
     * @throws NullPointerException When the sensor was not
     * yet set.
     */
    public SensorSimulator<T> getSensor() throws NullPointerException {
        if (this.sensor == null) throw new NullPointerException("The Sensor Simulator was not yet set");
        return this.sensor;
    }
}
