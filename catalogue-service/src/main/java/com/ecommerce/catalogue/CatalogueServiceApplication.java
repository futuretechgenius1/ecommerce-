package com.ecommerce.catalogue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class CatalogueServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogueServiceApplication.class, args);
    }
}
