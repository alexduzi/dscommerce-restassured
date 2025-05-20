package com.alexduzi.dscommerce;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DscommerceApplication implements CommandLineRunner {

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(DscommerceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
