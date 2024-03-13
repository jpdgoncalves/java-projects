package jpdgoncalves.iotdatasim.emitters;

import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;

import jpdgoncalves.iotdatasim.base.SensorSimulator;
import jpdgoncalves.iotdatasim.sensor.DefaultHumiditySensor;
import jpdgoncalves.iotdatasim.serializers.DoubleSerializer;
import jpdgoncalves.iotdatasim.serializers.KafkaSerializer;

public class KafkaEmitterTest {

    @Test
    public void testEmitter() {

        final long period = 0;
        final String topic = "humidity";

        SensorSimulator<Double> sensor = new DefaultHumiditySensor(1289073459497L);
        Serializer<Double> valueSerializer = new KafkaSerializer<>(new DoubleSerializer());
        MockProducer<String, Double> mockProducer = new MockProducer<>(true, new StringSerializer(), valueSerializer);
        KafkaEmitter<Double> emitter = new KafkaEmitter<>(period, topic, sensor, mockProducer);
    }
}
