package jpdgoncalves.iotdatasim.serializers;

import org.apache.kafka.common.serialization.Deserializer;

/**
 * Adapter that transforms a {@link jpdgoncalves.iotdatasim.base.Serializer} instance
 * into a {@link org.apache.kafka.common.serialization.Deserializer}
 */
public class KafkaDeserializer<T> implements Deserializer<T> {

    private final jpdgoncalves.iotdatasim.base.Serializer<T> rSerializer;

    /**
     * Creates a {@link org.apache.kafka.common.serialization.Deserializer}
     * from a {@link jpdgoncalves.iotdatasim.base.Serializer}
     * @param rSerializer The serializer that requires the adapter
     */
    public KafkaDeserializer(jpdgoncalves.iotdatasim.base.Serializer<T> rSerializer) {
        this.rSerializer = rSerializer;
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        return this.rSerializer.deserialize(data);
    }

}
