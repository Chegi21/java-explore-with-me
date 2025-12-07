package ru.practicum.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.format.DateTimeFormatter;

@Configuration
public class DateTimeConfig implements WebMvcConfigurer {
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Override
    public void addFormatters(FormatterRegistry formatterRegistry) {
        DateTimeFormatterRegistrar dateTimeFormatter = new DateTimeFormatterRegistrar();
        dateTimeFormatter.setDateTimeFormatter(DateTimeFormatter.ofPattern(PATTERN));
        dateTimeFormatter.registerFormatters(formatterRegistry);
    }

}
