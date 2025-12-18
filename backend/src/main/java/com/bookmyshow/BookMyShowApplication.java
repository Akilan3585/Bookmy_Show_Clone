package com.bookmyshow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BookMyShowApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BookMyShowApplication.class, args);
        System.out.println("=============================================");
        System.out.println("BookMyShow Application Started Successfully!");
        System.out.println("API Base URL: http://localhost:8080/api");
        System.out.println("H2 Console: http://localhost:8080/h2-console");
        System.out.println("=============================================");
    }
}
