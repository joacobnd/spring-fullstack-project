package com.joaquingrandiccelli.customer;

import com.joaquingrandiccelli.jwt.JWTUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//API Layer - Controller
@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {


    private final CustomerService customerService;
    private final JWTUtil jwtUtil;

    public CustomerController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<CustomerDTO> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("{customerId}")
    public CustomerDTO getCustomersById(@PathVariable("customerId") Integer customerId) {
        return customerService.getCustomer(customerId);
    }


    @PostMapping
    public ResponseEntity<?> registerCustomer(
            @RequestBody CustomerRegistrationRequest request) {
        customerService.addCustomer(request);
        String jwtToken = jwtUtil.issueToken(request.email(), "ROLE_USER");

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .build();
    }

    @DeleteMapping("{customerId}")
    public void deleteCustomer(@PathVariable("customerId") Integer customerId) {
        customerService.deletCustomerById(customerId);
    }

    @PutMapping("{customerId}")
    public void updateCustomerById(
            @PathVariable("customerId") Integer customerId,
            @RequestBody CustomerUpdateRequest request) {
        customerService.updateCustomer(customerId, request);
    }

    @PostMapping(
            value = "{customerId}/profile-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public void uploadCustomerProfileImage(
            @PathVariable("customerId") Integer customerId,
            @RequestParam("file") MultipartFile file) {
        customerService.uploadCustomerProfileImage(customerId, file);
    }


    @GetMapping("{customerId}/profile-image")
    public byte[] getCustomerProfileImage(
            @PathVariable("customerId") Integer customerId) {
        return customerService.getCustomerProfileImage(customerId);
    }


}
