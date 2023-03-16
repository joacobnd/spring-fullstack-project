package com.joaquingrandiccelli;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.joaquingrandiccelli.customer.Customer;
import com.joaquingrandiccelli.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
            String firstName = name.firstName();
            String lastName = name.lastName();
            Customer customer1 = new Customer(
                    firstName + " " + lastName,
                    firstName + "." + lastName + "@gmail.com",
                    random.nextInt(16, 90)
            );


            customerRepository.save(customer1);



        };
    }


}
