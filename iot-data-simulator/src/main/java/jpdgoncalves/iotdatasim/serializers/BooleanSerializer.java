package jpdgoncalves.iotdatasim.serializers;

import jpdgoncalves.iotdatasim.base.Serializer;

/**
 * Boolean serializer for the data emitters.
 */
public class BooleanSerializer implements Serializer<Boolean> {

    /**
     * Insantiate a boolean deserializer.
     */
    public BooleanSerializer() {}

    @Override
    public byte[] serialize(Boolean instance) {
        byte[] data = new byte[1];
        data[0] = instance ? (byte) 1 : (byte) 0;
        return data;
    }

    @Override
    public Boolean deserialize(byte[] data) {
        return data[0] == 0;
    }

}
