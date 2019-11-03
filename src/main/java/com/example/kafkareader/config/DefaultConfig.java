package com.example.kafkareader.config;

import com.example.kafkareader.routes.KafkaRouter;
import com.example.kafkareader.services.DefaultKafkaReaderService;
import com.example.kafkareader.services.ReaderService;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.CompositeRegistry;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DefaultConfig extends CamelConfiguration {

    @Autowired
    CamelContext context;

    @Bean
    ReaderService readerService(){
        return new DefaultKafkaReaderService();
    }

    /**
     * Camel routes
     */
    @Override
    public List<RouteBuilder> routes() {
        return Arrays.asList(kafkaConsumerRouteBuilder());
    }

    /**
     * This method configures camel routes
     *
     * @return the Camel route builder
     */
    @Bean
    public RouteBuilder kafkaConsumerRouteBuilder() {
        KafkaRouter kafkaRouteBuilder = new KafkaRouter();
        return kafkaRouteBuilder;
    }

    @Bean
    public DataSource psqlDatasource(){
        BasicDataSource ds = new BasicDataSource();
        ds.setUsername("postgres");
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setPassword("postgres");
        ds.setUrl("jdbc:postgresql://localhost:5432/kafka_test");
        return ds;
    }

}
