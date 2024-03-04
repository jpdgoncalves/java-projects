package com.github.jpdgoncalves.base;

/**
 * Abstract class used to implement a Data Emitter.
 * A Data Emitter is an Emitter of type T.
 * @param <T> The type of data the emitter will produce.
 */
public abstract class DataEmitter<T> implements Runnable {

    private SensorSimulator<T> sensor;
    private Serializer<T> serializer;

    /**
     * Creates a new data emitter for data produced
     * by the given sensor.
     * @param sensor The sensor used to get data.
     * @param serializer The serializer that encodes the data of this sensor.
     */
    public DataEmitter(SensorSimulator<T> sensor, Serializer<T> serializer) {
        this.sensor = sensor;
        this.serializer = serializer;
    }

    /**
     * Gets the sensor simulator
     * @return The sensor simulator that will produce the data.
     */
    public SensorSimulator<T> getSensor() {
        return this.sensor;
    }

    /**
     * Gets the serializer
     * @return The serializer capable of encoding the data from the sensor.
     */
    public Serializer<T> getSerializer() {
        return this.serializer;
    }
}
