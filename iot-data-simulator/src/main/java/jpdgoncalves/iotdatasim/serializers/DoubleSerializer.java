package jpdgoncalves.iotdatasim.serializers;

import java.nio.ByteBuffer;

import jpdgoncalves.iotdatasim.base.Serializer;

/**
 * Class used to serialize and deserialize doubles.
 */
public class DoubleSerializer implements Serializer<Double> {

    /**
     * Creates an instance of a double serializer.
     */
    public DoubleSerializer() {}

    @Override
    public byte[] serialize(Double instance) {
        ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES);
        return buffer.putDouble(instance).array();
    }

    @Override
    public Double deserialize(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        return buffer.getDouble();
    }

}
