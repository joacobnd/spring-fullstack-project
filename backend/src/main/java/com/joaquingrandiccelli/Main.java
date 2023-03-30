package com.joaquingrandiccelli;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.joaquingrandiccelli.customer.Customer;
import com.joaquingrandiccelli.customer.CustomerRepository;
import com.joaquingrandiccelli.customer.Gender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Random;


@SpringBootApplication
public class Main {

    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);

    }


    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository) {
        return args -> {

            var faker = new Faker();
            Random random = new Random();

            Name name = faker.name();
            String firstName = "NEGRO"+Math.random();
            String lastName = name.lastName();
            int age = random.nextInt(16, 90);
            Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
            Customer customer1 = new Customer(
                    firstName + " " + lastName,
                    firstName + "." + lastName + "@gmail.com",
                    age,
                    gender);


            customerRepository.save(customer1);


        };
    }


}
