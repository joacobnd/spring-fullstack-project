package com.joaquingrandiccelli.customer;

import com.joaquingrandiccelli.exception.DuplicateResourceException;
import com.joaquingrandiccelli.exception.RequestValidationException;
import com.joaquingrandiccelli.exception.ResourceNotFoundException;
import com.joaquingrandiccelli.s3.S3Buckets;
import com.joaquingrandiccelli.s3.S3Service;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//Business Layer - Service
@Service
public class CustomerService {

    private final CustomerDao customerDao;
    private final CustomerDTOMapper customerDTOMapper;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3;
    private final S3Buckets s3Buckets;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao, CustomerDTOMapper customerDTOMapper, PasswordEncoder passwordEncoder, S3Service s3, S3Buckets s3Buckets) {
        this.customerDao = customerDao;
        this.customerDTOMapper = customerDTOMapper;
        this.passwordEncoder = passwordEncoder;
        this.s3 = s3;
        this.s3Buckets = s3Buckets;
    }


    //Metodo para checkear si existe un customerId
    private void checkIfCustomerExistOrThrow(Integer customerId) {
        if (!customerDao.existPersonWithId(customerId)) {
            throw new ResourceNotFoundException("Customer with id [%s] not found".formatted(customerId));
        }
    }


    public List<CustomerDTO> getAllCustomers() {
        return customerDao.selectAllCustomer()
                .stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomer(Integer id) {
        return customerDao.selectCustomerById(id)
                .map(customerDTOMapper)
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
                passwordEncoder.encode(customerRegistrationRequest.password()),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.gender());
        customerDao.insertCustomer(customer);

    }


    public void deletCustomerById(Integer customerId) {
        checkIfCustomerExistOrThrow(customerId);
        customerDao.deleteCustomerById(customerId);
    }


    public void updateCustomer(Integer customerId, CustomerUpdateRequest request) {

        Customer customer = customerDao.selectCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer with id [%s] not found".formatted(customerId))
                );

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

    public void uploadCustomerProfileImage(Integer customerId, MultipartFile file) {
        checkIfCustomerExistOrThrow(customerId);
        String profileImageId = UUID.randomUUID().toString();
        try {
            s3.putObject(
                    s3Buckets.getCustomer(),
                    "profile-images/%s/%s".formatted(customerId, profileImageId),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        customerDao.updateCustomerProfileImageId(profileImageId,customerId);
    }

    public byte[] getCustomerProfileImage(Integer customerId) {
        var customer = customerDao.selectCustomerById(customerId)
                .map(customerDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer with id [%s] not found".formatted(customerId))
                );

        // TODO: check if profileImageId is empty or null
        if (StringUtils.isBlank(customer.profileImageId())) {
            throw new ResourceNotFoundException("Customer with id [%s] profile image not found".formatted(customerId));
        }

        return s3.getObject(
                s3Buckets.getCustomer(),
                "profile-images/%s/%s".formatted(customerId, customer.profileImageId())
        );
    }

}



