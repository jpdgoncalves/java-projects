package jpdgoncalves.iotdatasim.base;

import jpdgoncalves.iotdatasim.internals.Ticker;

/**
 * This is a class that implements a generic procedure
 * to produce data. It periodically ticks the sensor
 * simulator, read its current measure and emits the
 * measure to the emitter. The producer stops when
 * the stop method is called or when the sensor
 * simulator indicates the simulation has ended.
 * 
 * @param <T> The type of data produced.
 */
public class DataProducer<T> {

    private final SensorSimulator<T> sensor;
    private final Emitter<T> emitter;
    private final long period;
    private Ticker tickerThread = null;

    /**
     * Creates a new data producer.
     * @param period How often in miliseconds new data is sent to the emitter.
     * @param sensor The sensor from which the data is read.
     * @param emitter The emitter to which the data is sent.
     */
    public DataProducer(long period, SensorSimulator<T> sensor, Emitter<T> emitter) {
        this.period = period;
        this.sensor = sensor;
        this.emitter = emitter;
    }

    /**
     * Tells the state of the producer
     * @return True if it is running and false otherwise.
     */
    public boolean isRunning() {
        return this.tickerThread != null;
    }

    /**
     * Starts the producer.
     */
    public void start() {
        tickerThread = new Ticker(period);
        tickerThread.addTickFn(this::tick);
        tickerThread.start();
    }

    /**
     * Stops the producer.
     */
    public void stop() {
        if (tickerThread == null) return;

        tickerThread.interrupt();
        try {
            tickerThread.join();
        } catch (InterruptedException ignored) {
            /** Shouldn't happen but if it does it isn't a problem */
        }

        tickerThread = null;
    }

    private void tick() {
        try {
            sensor.tick();
        } catch (SimulationEndedException e) {
            /** 
             * Simulation of the sensor has ended. Stop the producer 
             * We can't use the stop method to not cause a deadlock.
             * */
            tickerThread.interrupt();
            tickerThread = null;
            return;
        }
        emitter.emit(sensor.readValue());
    }
}
