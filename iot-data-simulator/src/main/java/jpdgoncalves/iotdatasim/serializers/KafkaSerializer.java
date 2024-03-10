package jpdgoncalves.iotdatasim.serializers;

import org.apache.kafka.common.serialization.Serializer;

/**
 * Adapter that transforms a {@link jpdgoncalves.iotdatasim.base.Serializer} instance
 * into a {@link org.apache.kafka.common.serialization.Serializer}
 */
public class KafkaSerializer<T> implements Serializer<T> {

    private final jpdgoncalves.iotdatasim.base.Serializer<T> rSerializer;

    /**
     * Creates a {@link org.apache.kafka.common.serialization.Seserializer}
     * from a {@link jpdgoncalves.iotdatasim.base.Serializer}
     * @param rSerializer The serializer that requires the adapter
     */
    public KafkaSerializer(jpdgoncalves.iotdatasim.base.Serializer<T> rSerializer) {
        this.rSerializer = rSerializer;
    }

    @Override
    public byte[] serialize(String topic, T data) {
        return rSerializer.serialize(data);
    }

}
