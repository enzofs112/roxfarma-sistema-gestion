package com.roxfarma.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Configuración de Jackson para manejar proxies de Hibernate y fechas
 * @author grupo2
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
        
        // Registrar módulo de Hibernate para manejar proxies lazy
        Hibernate6Module hibernate6Module = new Hibernate6Module();
        hibernate6Module.configure(Hibernate6Module.Feature.FORCE_LAZY_LOADING, false);
        hibernate6Module.configure(Hibernate6Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS, true);
        
        mapper.registerModule(hibernate6Module);
        
        // Registrar módulo de Java 8 para manejar LocalDateTime, LocalDate, etc.
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return mapper;
    }
}
