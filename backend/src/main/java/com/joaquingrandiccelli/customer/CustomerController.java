package com.joaquingrandiccelli.customer;

import org.springframework.web.bind.annotation.*;

import java.util.List;

//API Layer - Controller
@CrossOrigin(origins = "https://joacoapi-env.eba-pjifwcsb.eu-west-3.elasticbeanstalk.com:8080")
@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("{customerId}")
    public Customer getCustomersById(@PathVariable("customerId") Integer customerId) {
        return customerService.getCustomer(customerId);
    }


    @PostMapping
    public void registerCustomer(
            @RequestBody CustomerRegistrationRequest request) {
        customerService.addCustomer(request);
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


}
