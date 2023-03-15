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
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                FAKER.number().numberBetween(10, 90)
        );
        underTest.insertCustomer(customer);

        // When
        List<Customer> actual = underTest.selectAllCustomer();

        // Then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                FAKER.number().numberBetween(10, 90)
        );
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
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                email,
                FAKER.number().numberBetween(10, 90)
        );
        underTest.insertCustomer(customer);

        // When
        boolean actual = underTest.existPersonWithEmail(email);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existPersonWithEmailReturnsFalseWhenDoesNotExist() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();

        // When
        boolean actual = underTest.existPersonWithEmail(email);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void existPersonWithId() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                FAKER.number().numberBetween(10, 90)
        );
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
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                FAKER.number().numberBetween(10, 90)
        );
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
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                FAKER.number().numberBetween(10, 90)
        );
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
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                FAKER.number().numberBetween(10, 90)
        );
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
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                FAKER.number().numberBetween(10, 90)
        );
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
    void updateCustomer() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                FAKER.number().numberBetween(10, 90)
        );
        underTest.insertCustomer(customer);


        Integer id = underTest.selectAllCustomer()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();


        // When

        Customer update = new Customer();
        update.setId(id);
        update.setName("Zac Efron");
        update.setEmail(FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID());
        update.setAge(28);

        underTest.updateCustomer(update);

        // Then

        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValue(update);
    }

    @Test
    void willNotUpdateWhenNothingToUpdate() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                FAKER.number().numberBetween(10, 90)
        );
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
}