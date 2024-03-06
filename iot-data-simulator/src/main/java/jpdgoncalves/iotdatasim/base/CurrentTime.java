package jpdgoncalves.iotdatasim.base;

/**
 * Functional interface that defines
 * as single method to get the time
 * since epoch.
 */
@FunctionalInterface
public interface CurrentTime {

    /**
     * Get the current time since epoch in miliseconds.
     * @return The current time since epoch in miliseconds.
     */
    public long currentTimeMillis();
}
