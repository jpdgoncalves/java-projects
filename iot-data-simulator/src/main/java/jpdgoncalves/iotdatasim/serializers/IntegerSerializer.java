package jpdgoncalves.iotdatasim.serializers;

import java.nio.ByteBuffer;

import jpdgoncalves.iotdatasim.base.DeprecatedSerializer;

/**
 * A serializer that converts integers to bytes
 * and vice-versa.
 */
public class IntegerSerializer implements DeprecatedSerializer<Integer> {

    /**
     * Creates an instance of a integer serializer
     */
    public IntegerSerializer() {}

    @Override
    public byte[] serialize(Integer instance) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        return buffer.putDouble(instance).array();
    }

    @Override
    public Integer deserialize(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        return buffer.getInt();
    }

}
