package com.trashfuneral.funeral;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.trashfuneral")
@EntityScan("com.trashfuneral")
@EnableJpaRepositories("com.trashfuneral")
public class TrashFuneralApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrashFuneralApplication.class, args);
    }
}
