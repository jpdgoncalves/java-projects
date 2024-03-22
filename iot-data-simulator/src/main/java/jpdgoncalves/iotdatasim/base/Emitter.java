package jpdgoncalves.iotdatasim.base;

/**
 * Interface that an Emitter class implements.
 * @param <T> The type of data this emitter accepts.
 */
public interface Emitter<T> {

    /**
     * Method called to emit some form of data.
     * @param data The data to emit.
     */
    public void emit(T data);
}
