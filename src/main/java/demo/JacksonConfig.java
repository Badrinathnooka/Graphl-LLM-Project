package demo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import graphql.ExceptionWhileDataFetching;

@Configuration
public class JacksonConfig {

    @JsonIgnoreProperties({"exception"})
    private static abstract class ExceptionWhileDataFetchingMixIn {}

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(ExceptionWhileDataFetching.class, ExceptionWhileDataFetchingMixIn.class);
        return mapper;
    }
}
