package jpdgoncalves.iotdatasim.emitters;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import jpdgoncalves.iotdatasim.base.SensorSimulator;
import jpdgoncalves.iotdatasim.sensor.DefaultHumiditySensor;
import jpdgoncalves.iotdatasim.serializers.DoubleSerializer;
import jpdgoncalves.iotdatasim.serializers.KafkaSerializer;

public class KafkaEmitterTest {

    @Test
    @Timeout(20)
    public void testKafkaEmitter() throws InterruptedException {
        long period = 1000;
        long seed = 298587092859L;
        String topic = "humidity";

        SensorSimulator<Double> sensor = new DefaultHumiditySensor(seed);
        jpdgoncalves.iotdatasim.base.Serializer<Double> serializer = new DoubleSerializer();
        Serializer<Double> valueSerializer = new KafkaSerializer<>(serializer);
        MockProducer<String, Double> producer = new MockProducer<>(true, new StringSerializer(), valueSerializer);
        DeprecatedKafkaEmitter<Double> emitter = new DeprecatedKafkaEmitter<>(period, topic, sensor, producer);

        Thread.sleep(period * 10);
        emitter.stop();

        assertTrue(producer.history().size() >= 9);
    }
}
