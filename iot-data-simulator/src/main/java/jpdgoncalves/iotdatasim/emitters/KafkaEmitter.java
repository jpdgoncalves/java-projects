package jpdgoncalves.iotdatasim.emitters;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import jpdgoncalves.iotdatasim.base.Emitter;

public class KafkaEmitter<T> implements Emitter<T> {

    private final String topic;
    private final Producer<String, T> producer;

    public KafkaEmitter(String topic, Producer<String, T> producer) {
        this.topic = topic;
        this.producer = producer;
    }

    @Override
    public void emit(T data) {
        producer.send(new ProducerRecord<String, T>(topic, data));
    }

}
