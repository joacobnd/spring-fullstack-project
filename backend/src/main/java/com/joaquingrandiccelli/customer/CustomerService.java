package com.joaquingrandiccelli.customer;

import com.joaquingrandiccelli.exception.DuplicateResourceException;
import com.joaquingrandiccelli.exception.RequestValidationException;
import com.joaquingrandiccelli.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

//Business Layer - Service
@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomer();
    }

    public Customer getCustomer(Integer id) {
        return customerDao.selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer with id [%s] not found".formatted(id))
                );
    }


    public void addCustomer(
            CustomerRegistrationRequest customerRegistrationRequest) {
        //check if email exist
        String email = customerRegistrationRequest.email();
        if (customerDao.existPersonWithEmail(email)) {
            throw new DuplicateResourceException("Email [%s] already taken".formatted(email));
        }

        //then add
        Customer customer = new Customer(customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age());
        customerDao.insertCustomer(customer);

    }


    public void deletCustomerById(Integer customerId) {
        if (!customerDao.existPersonWithId(customerId)) {
            throw new ResourceNotFoundException("Customer with id [%s] not found".formatted(customerId));
        }
        customerDao.deleteCustomerById(customerId);
    }

    public void updateCustomer(Integer customerId, CustomerUpdateRequest request) {
        Customer customer = getCustomer(customerId);
        boolean changes = false;
        if (request.name() != null && !request.name().equals(customer.getName())) {
            customer.setName(request.name());
            changes = true;
        }

        if (request.email() != null && !request.email().equals(customer.getEmail())) {
            if (customerDao.existPersonWithEmail(request.email())) {
                throw new DuplicateResourceException("Email already taken");
            }
            customer.setEmail(request.email());
            changes = true;
        }

        if (request.age() != null && !request.age().equals(customer.getAge())) {
            customer.setAge(request.age());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("No data changes found");
        }

        customerDao.updateCustomer(customer);
    }
}



