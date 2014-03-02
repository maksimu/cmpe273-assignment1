package edu.sjsu.cmpe273.assignment1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * User: maksim
 * Date: 2/22/14 - 2:21 PM
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class LibraryApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(LibraryApplication.class);
        app.setShowBanner(false);
        app.run(args);
    }
}
