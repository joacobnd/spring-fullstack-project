package com.joaquingrandiccelli.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
) {
}
