package demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Bean
    @Override
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(List.of(new StringToIntegerConverter()));
    }

    @Override
    protected String getDatabaseName() {
        return "starwar"; // replace with your DB name
    }
}