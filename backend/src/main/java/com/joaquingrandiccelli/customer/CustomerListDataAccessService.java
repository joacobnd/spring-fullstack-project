package com.joaquingrandiccelli.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao{


    private static List<Customer> customers;
    private final CustomerRepository customerRepository;

    public CustomerListDataAccessService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    static {
        Customer alex = new Customer(1, "Alex", "alex@gmail.com", "password", 30, Gender.MALE);
        Customer julia = new Customer(2, "Julia", "julia@gmail.com", "password", 23, Gender.MALE);
        Customer peter = new Customer(3, "Peter", "peter@gmail.com", "password", 28, Gender.MALE);
        customers = new ArrayList<>();
        customers.add(alex);
        customers.add(julia);
        customers.add(peter);
    }

    @Override
    public List<Customer> selectAllCustomer() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer customerId) {
        return customers.stream()
                .filter(c -> c.getId().equals(customerId))
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public boolean existPersonWithEmail(String email) {
        return customers.stream()
                .anyMatch(c -> c.getEmail().equals(email));
    }

    @Override
    public boolean existPersonWithId(Integer customerId) {
        return customers.stream()
                .anyMatch(c -> c.getId().equals(customerId));
    }

    @Override
    public void deleteCustomerById(Integer customerId) {
        customers.stream()
                .filter(c -> c.getId().equals(customerId))
                .findFirst()
                .ifPresent(customers::remove);
    }

    @Override
    public void updateCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public Optional<Customer> selectUserByEmail(String email) {
        return customers.stream()
                .filter(c -> c.getUsername().equals(email))
                .findFirst();
    }
}
