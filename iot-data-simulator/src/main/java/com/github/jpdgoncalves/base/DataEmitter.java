package com.github.jpdgoncalves.base;

public abstract class DataEmitter<T> implements Runnable {

    private SensorSimulator<T> sensor;

    public void setSensor(SensorSimulator<T> sensor) throws NullPointerException {
        if (sensor == null) throw new NullPointerException("Sensor Simulator can't be null");
        this.sensor = sensor;
    }

    public SensorSimulator<T> getSensor() throws NullPointerException {
        if (this.sensor == null) throw new NullPointerException("The Sensor Simulator was not yet set");
        return this.sensor;
    }
}
