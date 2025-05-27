package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"org.example", "org.example.persistance", "org.example.domain"})
@SpringBootApplication
public class Main {
   public static void main(String[] args){
       SpringApplication.run(Main.class, args);
   }
}