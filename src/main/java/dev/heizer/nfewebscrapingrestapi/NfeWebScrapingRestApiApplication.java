package dev.heizer.nfewebscrapingrestapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class NfeWebScrapingRestApiApplication
{
    public static void main(String[] args) {SpringApplication.run(NfeWebScrapingRestApiApplication.class, args);}
}
