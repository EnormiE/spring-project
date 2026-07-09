package org.example.carrent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Fake GPS
public class CarRentApplication {
    public static void main(String[] args) {
        SpringApplication.run(
                CarRentApplication.class,
                args
        );
    }
}
