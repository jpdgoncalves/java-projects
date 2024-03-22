package jpdgoncalves.iotdatasim.base;

import jpdgoncalves.iotdatasim.internals.Ticker;

public class DataProducer<T> {

    private final SensorSimulator<T> sensor;
    private final Emitter<T> emitter;
    private final long period;
    private Ticker tickerThread = null;

    public DataProducer(long period, SensorSimulator<T> sensor, Emitter<T> emitter) {
        this.period = period;
        this.sensor = sensor;
        this.emitter = emitter;
    }

    public boolean isRunning() {
        return this.tickerThread != null;
    }

    public void start() {
        tickerThread = new Ticker(period);
        tickerThread.addTickFn(this::tick);
        tickerThread.start();
    }

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
