package com.example.kafkareader.routes;

import com.example.kafkareader.process.SimpleJDBCInsertProcessor;
import org.apache.camel.*;
import org.apache.camel.builder.ErrorHandlerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.kafka.KafkaManualCommit;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KafkaRouter extends RouteBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaRouter.class);

    @Autowired
    CamelContext context;

    @Override
    public void configure() throws Exception {
        onException(Exception.class)
                .handled(false)
                .log(LoggingLevel.WARN, "${exception.message}");

        onException(PSQLException.class).handled(true).redeliveryDelay(2000).maximumRedeliveries(3)
                .log("Exception Encountered while inserting messages to DB");

        from("kafka:Test?brokers=localhost:9092&maxPollRecords=3&consumersCount=1&autoOffsetReset=earliest&autoCommitEnable=false&allowManualCommit=true&breakOnFirstError=true&seekTo=")
                .routeId("fromKafka")
                .log("${body}")
                .process(exchange -> {
                    // manually commit offset if it is last message in batch
                    Boolean lastOne = exchange.getIn().getHeader(KafkaConstants.LAST_RECORD_BEFORE_COMMIT, Boolean.class);

                    if (lastOne) {
                        KafkaManualCommit manual =
                                exchange.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class);
                        if (manual != null) {
                            LOGGER.info("manually committing the offset for batch");
                            manual.commitSync();
                        }
                    } else {
                        LOGGER.info("NOT time to commit the offset yet");
                    }
                })
                .process(new SimpleJDBCInsertProcessor())
                .to("jdbc:psqlDatasource")
//                .to("sql:select * from messages?dataSource=psqlDatasource")
                .to("log:?level=INFO&showBody=true");
    }
}
