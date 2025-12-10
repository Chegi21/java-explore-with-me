package ru.practicum.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Configuration(value = "mainJacksonConfig")
public class JacksonConfig {
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Bean("mainJsonCustomizer")
    public Jackson2ObjectMapperBuilderCustomizer mainJsonCustomizer() {
        return builder -> builder
                .serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(PATTERN)))
                .deserializers(new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(PATTERN)))
                .timeZone(TimeZone.getTimeZone("UTC"));
    }
}
