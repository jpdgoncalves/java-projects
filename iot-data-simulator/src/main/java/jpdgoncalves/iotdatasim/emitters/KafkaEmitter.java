package jpdgoncalves.iotdatasim.emitters;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import jpdgoncalves.iotdatasim.base.Emitter;

/**
 * An emitter that sends data to a kafka server
 * on a particular topic.
 * 
 * @param <T> The type of data that is sent.
 */
public class KafkaEmitter<T> implements Emitter<T> {

    private final String topic;
    private final Producer<String, T> producer;

    /**
     * Creates a new kafka emitter.
     * @param topic Topic to where the data is sent.
     * @param producer Implementation of a kafka client used
     * to send the data.
     */
    public KafkaEmitter(String topic, Producer<String, T> producer) {
        this.topic = topic;
        this.producer = producer;
    }

    @Override
    public void emit(T data) {
        producer.send(new ProducerRecord<String, T>(topic, data));
    }

}
