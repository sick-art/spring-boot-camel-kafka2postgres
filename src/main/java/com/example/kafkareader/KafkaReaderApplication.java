package com.example.kafkareader;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.CompositeRegistry;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;

@SpringBootApplication
public class KafkaReaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaReaderApplication.class, args);
	}

}
