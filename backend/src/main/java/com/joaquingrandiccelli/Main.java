package com.joaquingrandiccelli;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.joaquingrandiccelli.customer.Customer;
import com.joaquingrandiccelli.customer.CustomerRepository;
import com.joaquingrandiccelli.customer.Gender;
import com.joaquingrandiccelli.s3.S3Buckets;
import com.joaquingrandiccelli.s3.S3Service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;


@SpringBootApplication
public class Main {

    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);

    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository,
                             PasswordEncoder passwordEncoder) {
        return args -> {
            createRandomCustomer(customerRepository, passwordEncoder);
//          testBucketUploadAndDownload(s3Service, s3Buckets);


        };
    }

    private static void createRandomCustomer(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        var faker = new Faker();
        Random random = new Random();

        Name name = faker.name();
        String firstName = name.firstName();
        String lastName = name.lastName();
        int age = random.nextInt(16, 90);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
        String email = firstName + "." + lastName + "@gmail.com";
        Customer customer1 = new Customer(
                firstName + " " + lastName,
                email,
                passwordEncoder.encode("password"),
                age,
                gender);

        customerRepository.save(customer1);
        System.out.println(email);
    }

    private static void testBucketUploadAndDownload(S3Service s3Service, S3Buckets s3Buckets) {
        s3Service.putObject(
                s3Buckets.getCustomer(),
                "foo/bar",
                "Hello Work".getBytes()
        );

        byte[] object = s3Service.getObject(
                s3Buckets.getCustomer(),
                "foo/bar"
        );

        System.out.println("Hooray " + new String(object));
    }


}
