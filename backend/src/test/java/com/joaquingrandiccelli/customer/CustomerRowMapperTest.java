package com.joaquingrandiccelli.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRowMapperTest {

    private CustomerRowMapper customerRowMapper;

    @BeforeEach
    void setUp() {
        customerRowMapper = new CustomerRowMapper();
    }

    @Test
    void mapRow() throws SQLException {
        //Given
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("name")).thenReturn("John");
        when(rs.getString("email")).thenReturn("john@gmail.com");
        when(rs.getString("password")).thenReturn("password");
        when(rs.getInt("age")).thenReturn(30);
        when(rs.getString("gender")).thenReturn(Gender.FEMALE.name());
        when(rs.getString("profile_image_id")).thenReturn("22222");

        //When
        Customer actual = customerRowMapper.mapRow(rs, 0);

        //Then
        Customer expected = new Customer(
                1,
                "John",
                "john@gmail.com",
                "password",
                30,
                Gender.FEMALE,
                "22222"
        );

        assertThat(actual).isEqualTo(expected);

    }
}