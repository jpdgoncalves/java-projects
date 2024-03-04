package com.github.jpdgoncalves.base;

/**
 * A Serializer capable of encoding to bytes
 * and decoding from bytes some data type T.
 * @param <T> The type of data that this Serializer handles.
 */
public interface Serializer<T> {

    /**
     * Encodes a type T instance to bytes.
     * @param instance The instance to encode.
     * @return The bytes of the instance.
     */
    public byte[] serialize(T instance);

    /**
     * Decodes an array of bytes to a
     * instance of type T.
     * @param data The bytes of the encoded instance.
     * @return The decoded instance.
     */
    public T deserialize(byte[] data);
}
