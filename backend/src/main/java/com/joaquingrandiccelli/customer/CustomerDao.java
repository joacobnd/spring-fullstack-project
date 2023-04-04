package com.joaquingrandiccelli.customer;


import java.util.List;
import java.util.Optional;

// DAO Layer
public interface CustomerDao {
    List<Customer> selectAllCustomer();
    Optional<Customer> selectCustomerById(Integer customerId);
    
    void insertCustomer(Customer customer);
    boolean existPersonWithEmail(String email);
    boolean existPersonWithId(Integer customerId);
    void deleteCustomerById(Integer customerId);
    void updateCustomer(Customer customer);

    Optional<Customer> selectUserByEmail(String email);





}
