package com.joaquingrandiccelli.customer;

import com.joaquingrandiccelli.AbstractTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class CustomerJDBCDataAccessServiceTest extends AbstractTestContainers {

    private CustomerJDBCDataAccessService underTest;

    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJbdcTemplate(),
                customerRowMapper);
    }

    @Test
    void selectAllCustomer() {
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress(),
                "password", FAKER.number().numberBetween(10, 90),
                Gender.MALE);
        underTest.insertCustomer(customer);

        // When
        List<Customer> actual = underTest.selectAllCustomer();

        // Then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        String email = FAKER.internet().safeEmailAddress();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", FAKER.number().numberBetween(10, 90),
                Gender.MALE);
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomer()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        //When

        Optional<Customer> actual = underTest.selectCustomerById(id);

        //Then

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId().equals(id));
            assertThat(c.getName().equals(customer.getName()));
            assertThat(c.getEmail().equals(customer.getEmail()));
            assertThat(c.getAge().equals(customer.getAge()));
        });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        // Given
        int id = -1;

        // When
        var actual = underTest.selectCustomerById(id);

        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {
        // Given


        // When

        // Then
    }

    @Test
    void existPersonWithEmail() {
        // Given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress();
        Customer customer = new Customer(
                name,
                email,
                "password", FAKER.number().numberBetween(10, 90),
                Gender.MALE);
        underTest.insertCustomer(customer);

        // When
        boolean actual = underTest.existPersonWithEmail(email);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existPersonWithEmailReturnsFalseWhenDoesNotExist() {
        // Given
        String email = FAKER.internet().safeEmailAddress();

        // When
        boolean actual = underTest.existPersonWithEmail(email);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void existPersonWithId() {
        // Given
        String email = FAKER.internet().safeEmailAddress();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", FAKER.number().numberBetween(10, 90),
                Gender.MALE);
        underTest.insertCustomer(customer);


        Integer id = underTest.selectAllCustomer()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        boolean actual = underTest.existPersonWithId(id);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existCustomerWithIdWillReturnFalseWhenIdNotPresent() {
        // Given
        int id = -1;

        // When
        boolean actual = underTest.existPersonWithId(id);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomerById() {
        // Given
        String email = FAKER.internet().safeEmailAddress();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", FAKER.number().numberBetween(10, 90),
                Gender.MALE);
        underTest.insertCustomer(customer);


        Integer id = underTest.selectAllCustomer()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        underTest.deleteCustomerById(id);

        // Then

        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isNotPresent();
    }


    @Test
    void updateCustomerName() {
        // Given
        String email = FAKER.internet().safeEmailAddress();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", FAKER.number().numberBetween(10, 90),
                Gender.MALE);
        underTest.insertCustomer(customer);


        Integer id = underTest.selectAllCustomer()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newName = "emi";

        // When

        Customer update = new Customer();
        update.setId(id);
        update.setName(newName);

        underTest.updateCustomer(update);

        // Then

        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });

    }

    @Test
    void updateCustomerEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", FAKER.number().numberBetween(10, 90),
                Gender.MALE);
        underTest.insertCustomer(customer);


        Integer id = underTest.selectAllCustomer()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newEmail = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();

        // When

        Customer update = new Customer();
        update.setId(id);
        update.setEmail(newEmail);

        underTest.updateCustomer(update);

        // Then

        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerAge() {
        // Given
        String email = FAKER.internet().safeEmailAddress();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", FAKER.number().numberBetween(10, 90),
                Gender.MALE);
        underTest.insertCustomer(customer);


        Integer id = underTest.selectAllCustomer()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        int newAge = FAKER.number().numberBetween(10, 90);

        // When

        Customer update = new Customer();
        update.setId(id);
        update.setAge(newAge);

        underTest.updateCustomer(update);

        // Then

        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(newAge);
        });
    }

    @Test
    void willUpdateAllPropertiesCustomer() {
        // Given
        String email = FAKER.internet().safeEmailAddress();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", FAKER.number().numberBetween(10, 90),
                Gender.MALE);

        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomer()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When update with new name, age and email
        Customer update = new Customer();
        update.setId(id);
        update.setName("foo");
        String newEmail = UUID.randomUUID().toString();
        update.setEmail(newEmail);
        update.setAge(22);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(updated -> {
            assertThat(updated.getId()).isEqualTo(id);
            assertThat(updated.getGender()).isEqualTo(Gender.MALE);
            assertThat(updated.getName()).isEqualTo("foo");
            assertThat(updated.getEmail()).isEqualTo(newEmail);
            assertThat(updated.getAge()).isEqualTo(22);
        });
    }

    @Test
    void willNotUpdateWhenNothingToUpdate() {
        // Given
        String email = FAKER.internet().safeEmailAddress();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", FAKER.number().numberBetween(10, 90),
                Gender.MALE);
        underTest.insertCustomer(customer);


        Integer id = underTest.selectAllCustomer()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newName = "emi";

        // When

        Customer update = new Customer();
        update.setId(id);

        underTest.updateCustomer(update);

        // Then

        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void canUpdateProfileImageId() {
        // Given
        String email = "email";
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", FAKER.number().numberBetween(10, 90),
                Gender.MALE);
        underTest.insertCustomer(customer);

        Integer customerId = underTest.selectAllCustomer()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        underTest.updateCustomerProfileImageId("222", customerId);;

        // Then
        Optional<Customer> customerOp = underTest.selectCustomerById(customerId);
        assertThat(customerOp).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getProfileImageId()).isEqualTo("222");
        });
    }

}