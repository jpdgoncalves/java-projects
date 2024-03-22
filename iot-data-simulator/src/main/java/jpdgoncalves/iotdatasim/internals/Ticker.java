package jpdgoncalves.iotdatasim.internals;

import java.util.HashSet;

/**
 * A class which calls a list of functions periodically.
 * It is made in such a way that it will call it with
 * the specified period unless the tick functions take
 * more time to finish than the period.
 */
public class Ticker extends Thread {

    /**
     * The tick function
     */
    @FunctionalInterface
    public interface TickFunction {
        /**
         * The function that is called.
         */
        public void tick();
    }

    private final long period;
    private final HashSet<TickFunction> tickFunctions = new HashSet<>();

    /**
     * Create a default ticker that calls
     * the functions every second.
     */
    public Ticker() {
        this(1000L);
    }

    /**
     * Create a ticker that calls the functions
     * with the specified period.
     * @param period The period for the calls.
     */
    public Ticker(long period) {
        this.period = period;
        setDaemon(true);
    }

    /**
     * Add a function to be called by the ticker
     * if it wasn't added already.
     * @param fn The function to be called.
     * @return Whether it was added or not.
     */
    public boolean addTickFn(TickFunction fn) {
        return tickFunctions.add(fn);
    }

    /**
     * Remove a function to be called by the ticker
     * if it was added.
     * @param fn The function to be called.
     * @return Whether it was removed or not.
     */
    public boolean removeTickFn(TickFunction fn) {
        return tickFunctions.remove(fn);
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                long start = System.currentTimeMillis();
                for (TickFunction fn : tickFunctions) {
                    fn.tick();
                }
                long end = System.currentTimeMillis();
                long interval = period - (end - start);
                interval = interval > 0 ? interval : 1;
                Thread.sleep(interval);
            }
        } catch (InterruptedException e) {
            /** Ignore. This tells us to stop the ticker */
        }
    }
}
