package com.example.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point of our Spring Boot application.
 *
 * Notice the @SpringBootApplication annotation:
 * In LeetCode/basic Java, you manually instantiate objects and manage dependencies.
 * @SpringBootApplication activates three core features:
 * 1. @Configuration: Tags the class as a source of bean definitions for the application context.
 * 2. @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings.
 * 3. @ComponentScan: Tells Spring to look for other components, configurations, and services
 *    in the 'com.example.todolist' package and its sub-packages.
 */
@SpringBootApplication
public class TodoListApplication {

    public static void main(String[] args) {
        // Launches the embedded Tomcat web server and initializes the Spring IoC container
        SpringApplication.run(TodoListApplication.class, args);
    }
}
