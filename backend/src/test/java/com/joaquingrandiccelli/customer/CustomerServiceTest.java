package com.joaquingrandiccelli.customer;

import com.joaquingrandiccelli.exception.DuplicateResourceException;
import com.joaquingrandiccelli.exception.RequestValidationException;
import com.joaquingrandiccelli.exception.ResourceNotFoundException;
import com.joaquingrandiccelli.s3.S3Buckets;
import com.joaquingrandiccelli.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private S3Service s3Service;

    @Mock
    private S3Buckets s3Buckets;

    private CustomerService underTest;

    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(
                customerDao,
                customerDTOMapper,
                passwordEncoder,
                s3Service,
                s3Buckets
        );
    }

    @Test
    void getAllCustomers() {
        // When
        underTest.getAllCustomers();

        // Then
        verify(customerDao).selectAllCustomer();
    }

    @Test
    void canGetCustomer() {
        // Given
        int id = 10;
        Customer customer = new Customer(
                id,
                "Joaquin",
                "joaco@gmail.com",
                "password", 31,
                Gender.MALE);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerDTO expected = customerDTOMapper.apply(customer);

        // When
        CustomerDTO actual = underTest.getCustomer(id);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void WillThrowWhenGetCustomerReturnsEmptyOptional() {
        // Given
        int id = 10;
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));
    }

    @Test
    void addCustomer() {
        // Given
        String email = "alex@gmail.com";
        when(customerDao.existPersonWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "alex", email, "password", 19, Gender.MALE
        );

        String hashPassword = "&%s785456865";

        when(passwordEncoder.encode(request.password())).thenReturn(hashPassword);

        // When
        underTest.addCustomer(request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();


        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getPassword()).isEqualTo(hashPassword);
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void WillThrowWhenEmailExistWhileAddingCustomer() {
        // Given
        String email = "alex@gmail.com";
        when(customerDao.existPersonWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "alex", email, "password", 19, Gender.MALE
        );

        // When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email [%s] already taken".formatted(email));
        // Then
        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deletCustomerById() {
        // Given
        int id = 10;
        when(customerDao.existPersonWithId(id)).thenReturn(true);

        // When
        underTest.deletCustomerById(id);

        // Then
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void WIllThorWhenIdNotExistWhileDeletingCustomerById() {
        // Given
        int id = 10;
        when(customerDao.existPersonWithId(id)).thenReturn(false);

        // When
        assertThatThrownBy(() -> underTest.deletCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));

        // Then
        verify(customerDao, never()).deleteCustomerById(id);
    }

    @Test
    void canUpdateAllCustomersProperties(){
        //Given
        int id = 10;
        Customer customer = new Customer(
                id,
                "Joaquin",
                "joaco@gmail.com",
                "password", 31,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String email = "joaco__@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "Alex",
                email,
                29
        );

        when(customerDao.existPersonWithEmail(email)).thenReturn(false);


        //When
        underTest.updateCustomer(id, updateRequest);

        //Then

        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer captureCustomer = customerArgumentCaptor.getValue();

        assertThat(captureCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(captureCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(captureCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        // Given
        int id = 10;
        Customer customer = new Customer(
                id,
                "Joaquin",
                "joaco@gmail.com",
                "password", 31,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "Alex",
                null,
                null
        );

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer captureCustomer = customerArgumentCaptor.getValue();

        assertThat(captureCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(captureCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(captureCustomer.getAge()).isEqualTo(customer.getAge());

    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        // Given
        int id = 10;
        Customer customer = new Customer(
                id,
                "Joaquin",
                "joaco@gmail.com",
                "password", 31,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alex@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null,
                newEmail,
                null
        );

        when(customerDao.existPersonWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer captureCustomer = customerArgumentCaptor.getValue();

        assertThat(captureCustomer.getName()).isEqualTo(customer.getName());
        assertThat(captureCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(captureCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canUpdateOnlyCustomerAge() {
        // Given
        int id = 10;
        Customer customer = new Customer(
                id,
                "Joaquin",
                "joaco@gmail.com",
                "password", 31,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null,
                null,
                35
        );

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer captureCustomer = customerArgumentCaptor.getValue();

        assertThat(captureCustomer.getName()).isEqualTo(customer.getName());
        assertThat(captureCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(captureCustomer.getAge()).isEqualTo(updateRequest.age());

    }

    @Test
    void willThrowWhenCustomerUpdateHasNoChanges() {
        // Given
        int id = 10;
        Customer customer = new Customer(
                id,
                "Joaquin",
                "joaco@gmail.com",
                "password", 31,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                customer.getName(),
                customer.getEmail(),
                customer.getAge()
        );

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No data changes found");

        // Then
        verify(customerDao, never()).updateCustomer(any());


    }

    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        // Given
        int id = 10;
        Customer customer = new Customer(
                id,
                "Joaquin",
                "joaco@gmail.com",
                "password", 31,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alex@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null,
                newEmail,
                null
        );

        when(customerDao.existPersonWithEmail(newEmail)).thenReturn(true);

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already taken");

        // Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void canUploadProfileImage() {

        // Given
        int id = 10;

        when(customerDao.existPersonWithId(id)).thenReturn(true);

        byte[] bytes = "Hello World".getBytes();

        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                bytes);

        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        // When
        underTest.uploadCustomerProfileImage(id, multipartFile);

        // Then

        ArgumentCaptor<String> profileImageIdArgumentCapture = ArgumentCaptor.forClass(String.class);
        verify(customerDao).updateCustomerProfileImageId(
                profileImageIdArgumentCapture.capture(),
                eq(id)
        );

        verify(s3Service).putObject(
                bucket,
                "profile-images/%s/%s".formatted(id, profileImageIdArgumentCapture.getValue()),
                bytes
        );

    }

    @Test
    void cannotUploadProfileImageWhenCustomerDoesNotExist() {

        // Given
        int id = 10;

        when(customerDao.existPersonWithId(id)).thenReturn(false);

        // When
        assertThatThrownBy(() -> underTest.uploadCustomerProfileImage(id, mock(MultipartFile.class)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id ["+ id +"] not found");

        // Then

        verify(customerDao).existPersonWithId(id);
        verifyNoMoreInteractions(customerDao);
        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);

    }


    @Test
    void cannotUploadProfileImageWhenExceptionIsThrown() throws IOException {

        // Given
        int id = 10;

        when(customerDao.existPersonWithId(id)).thenReturn(true);


        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getBytes()).thenThrow(IOException.class);

        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        // When
        assertThatThrownBy(() -> {
            underTest.uploadCustomerProfileImage(id, multipartFile);
        }).isInstanceOf(RuntimeException.class).hasRootCauseInstanceOf(IOException.class);

        // Then

        verify(customerDao, never()).updateCustomerProfileImageId(any(), any());

    }


    @Test
    void canDownloadProfileImage() {
        // Given
        int customerId = 10;
        String profileImageId = "2222";
        Customer customer = new Customer(
                customerId,
                "Joaquin",
                "joaco@gmail.com",
                "password", 31,
                Gender.MALE,
                profileImageId);
        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        byte[] expectedImage = "image".getBytes();
        when(s3Service.getObject(bucket, "profile-images/%s/%s".formatted(customerId, profileImageId))
        ).thenReturn(expectedImage);

        // When
        byte[] actualImage = underTest.getCustomerProfileImage(customerId);

        // Then
        assertThat(actualImage).isEqualTo(expectedImage);
    }

    @Test
    void cannotDownloadProfileImageWhenNoProfileImageId() {
        // Given
        int customerId = 10;
        Customer customer = new Customer(
                customerId,
                "Joaquin",
                "joaco@gmail.com",
                "password", 31,
                Gender.MALE);
        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));




        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomerProfileImage(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] profile image not found".formatted(customerId));

        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);
    }

    @Test
    void cannotDownloadProfileImageWhenCustomerDoestNotExist() {
        // Given
        int customerId = 10;
        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.empty());


        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomerProfileImage(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(customerId));

        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);
    }

}