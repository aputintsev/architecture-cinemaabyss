package com.github.aputintsev.architecture_cinemaabyss.events;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableKafka
@EnableWebMvc
@ComponentScan(basePackages = "com.github.aputintsev.architecture_cinemaabyss")
public class EventsApplicationConfig implements WebMvcConfigurer {
  @Autowired
  private KafkaProperties kafkaProperties;

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    WebMvcConfigurer.super.extendMessageConverters(converters);
    converters.add(new MappingJackson2HttpMessageConverter(
        new Jackson2ObjectMapperBuilder()
            .dateFormat((new StdDateFormat()).withTimeZone(TimeZone.getDefault()))
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build()));
  }

  @Bean
  public Map<String, Object> producerConfigs() {
    Map<String, Object> props =
        new HashMap<>(kafkaProperties.buildProducerProperties());
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        JsonSerializer.class);

    return props;
  }

  @Bean
  public ProducerFactory<String, Object> producerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfigs());
  }

  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  @Bean
  public ConsumerFactory<String, Object> consumerFactory() {
    Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());

    StringDeserializer keyDeserializer = new StringDeserializer();
    JsonDeserializer<Object> valueDeserializer = new JsonDeserializer<>();
    valueDeserializer.addTrustedPackages("com.github.aputintsev.architecture_cinemaabyss.events.dto");

    return new DefaultKafkaConsumerFactory<>(
        props, keyDeserializer, valueDeserializer
    );
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
  }
}