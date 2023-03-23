package com.joaquingrandiccelli.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age,

        Gender gender
) {

}
