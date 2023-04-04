package com.joaquingrandiccelli.journey;

import com.github.javafaker.Faker;
import com.joaquingrandiccelli.auth.AuthenticationRequest;
import com.joaquingrandiccelli.auth.AuthenticationResponse;
import com.joaquingrandiccelli.customer.CustomerDTO;
import com.joaquingrandiccelli.customer.CustomerRegistrationRequest;
import com.joaquingrandiccelli.customer.Gender;
import com.joaquingrandiccelli.jwt.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JWTUtil jwtUtil;

    private static final String AUTHENTICATION_PATH = "/api/v1/auth";
    private static final String CUSTOMER_PATH = "/api/v1/customers";


    @Test
    void canLogin() {
        // Given
        // create registration customerRegistrationRequest
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.internet().safeEmailAddress();
        int age = faker.number().numberBetween(10, 90);

        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        String password = "password";
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                name, email, password, age, gender
        );

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(email, password);

        webTestClient.post()
                .uri(AUTHENTICATION_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        // send a post customerRegistrationRequest
        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegistrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //login
        EntityExchangeResult<AuthenticationResponse> result = webTestClient.post()
                .uri(AUTHENTICATION_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {
                })
                .returnResult();

        String jwtToken = result.getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);


        AuthenticationResponse authenticationResponse = result.getResponseBody();
        CustomerDTO customerDTO = authenticationResponse.customerDTO();


        assertThat(jwtUtil.isTokenValid(jwtToken, customerDTO.username())).isTrue();

        assertThat(customerDTO.email()).isEqualTo(email);
        assertThat(customerDTO.age()).isEqualTo(age);
        assertThat(customerDTO.name()).isEqualTo(name);
        assertThat(customerDTO.username()).isEqualTo(email);
        assertThat(customerDTO.gender()).isEqualTo(gender);
        assertThat(customerDTO.roles()).isEqualTo(List.of("ROLE_USER"));

        

    }
}
