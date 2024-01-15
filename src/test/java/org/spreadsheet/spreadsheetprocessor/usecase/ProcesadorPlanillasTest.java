package org.spreadsheet.spreadsheetprocessor.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spreadsheet.spreadsheetprocessor.domain.Employee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcesadorPlanillasTest {
    private static final String SELECT_EMPLOYEE_QUERY = "SELECT id, name, monthlyPayment, active  FROM employee";
    private ProcesadorPlanillas probarPlanillas;

    @Mock
    private JdbcTemplate jdbcTemplateMock;

    @Mock
    private RowMapper<Employee> rowMapper;


    Employee employee1 = Employee.builder()
            .id(1L)
            .name("John")
            .monthlyPayment(new BigDecimal(1000))
            .active(true)
            .build();
    Employee employee2 = Employee.builder()
            .id(2L)
            .name("Jane")
            .monthlyPayment(new BigDecimal(500))
            .active(false)
            .build();


    List<Employee> employees = new ArrayList<>(Arrays.asList(employee1, employee2));

    @BeforeEach
    void setUp() {
        probarPlanillas = new ProcesadorPlanillas(jdbcTemplateMock, rowMapper);
        employees = new ArrayList<>(Arrays.asList(employee1, employee2));
    }

    @Test
    void when_total_amount_has_positive_value() {
        // Set up mocks
        when(jdbcTemplateMock.query(SELECT_EMPLOYEE_QUERY, rowMapper)).thenReturn(employees);


        // Run the method under test
        Long totalAmount = probarPlanillas.totalAmountToPay();

        //assert
        assertThat(totalAmount).isEqualTo(1000L);
    }
}