package org.spreadsheet.spreadsheetprocessor.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spreadsheet.spreadsheetprocessor.domain.Employee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcesadorPlanillasTest {
    private ProcesadorPlanillas probarPlanillas;

    @Mock
    private JdbcTemplate jdbcTemplateMock;

    @Mock
    private RowMapper<Employee> rowMapper;

    private static final String SELECT_EMPLOYEE_QUERY = "SELECT id, name, monthlyPayment, active  FROM employee";


    @BeforeEach
    void setUp() {
        probarPlanillas = new ProcesadorPlanillas(jdbcTemplateMock, rowMapper);
    }

    @ParameterizedTest
    @MethodSource("provideEmployees")
    void should_return_correct_payroll(List<Employee> employees, Long expectedAmount) {
        // Set up
        when(jdbcTemplateMock.query(SELECT_EMPLOYEE_QUERY, rowMapper)).thenReturn(employees);

        // Run
        Long totalAmount = probarPlanillas.totalAmountToPay();

        // Assert
        assertThat(totalAmount).isEqualTo(expectedAmount);
    }

    static Stream<Arguments> provideEmployees() {
        Employee validEmployeeOne = Employee.builder()
                .id(1L)
                .name("Andres Quiroga")
                .monthlyPayment(new BigDecimal(1000))
                .active(Boolean.TRUE)
                .build();

        Employee validEmployeeTwo = Employee.builder()
                .id(6L)
                .name("Maria Jimenez")
                .monthlyPayment(new BigDecimal(2000))
                .active(Boolean.TRUE)
                .build();

        Employee employeeInactive = Employee.builder()
                .id(2L)
                .name("Julio Iglesias")
                .monthlyPayment(new BigDecimal(500))
                .active(Boolean.FALSE)
                .build();

        Employee employeeWithNegativeSalary = Employee.builder()
                .id(3L)
                .name("Alberto Pedraza")
                .monthlyPayment(new BigDecimal(-500))
                .active(Boolean.TRUE)
                .build();

        Employee employeeWithoutName = Employee.builder()
                .id(4L)
                .name("")
                .monthlyPayment(new BigDecimal(-500))
                .active(Boolean.TRUE)
                .build();

        Employee employeeWithZeroId = Employee.builder()
                .id(0L)
                .name("Pedro Sanchez")
                .monthlyPayment(new BigDecimal(2000))
                .active(Boolean.TRUE)
                .build();

        return Stream.of(
                Arguments.of(List.of(validEmployeeOne,
                        validEmployeeTwo,
                        employeeInactive,
                        employeeWithNegativeSalary,
                        employeeWithoutName,
                        employeeWithZeroId),
                        3000L)
        );
    }
}