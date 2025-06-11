package com.jpmc.midascore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MidasCoreApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(MidasCoreApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MidasCoreApplication.class, args);
        
        // Log which beans are being created
        String[] beanNames = context.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            if (beanName.contains("transaction") || beanName.contains("kafka")) {
                logger.info("Found bean: {}", beanName);
            }
        }
    }
}