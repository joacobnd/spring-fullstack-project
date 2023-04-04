package com.joaquingrandiccelli.auth;

import com.joaquingrandiccelli.customer.CustomerDTO;

public record AuthenticationResponse (
        String token,
        CustomerDTO customerDTO) {

}
