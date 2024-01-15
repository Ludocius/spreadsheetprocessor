package org.spreadsheet.spreadsheetprocessor.usecase;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spreadsheet.spreadsheetprocessor.domain.Employee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProcesadorPlanillas {
    private static final Logger logger = LoggerFactory.getLogger(ProcesadorPlanillas.class);
    private static final String SELECT_EMPLOYEE_QUERY = "SELECT id, name, monthlyPayment, active  FROM employee";

    private final JdbcTemplate procesadorMiembrosPlanillas;
    private final RowMapper<Employee> rowMapper;


    public Long totalAmountToPay(){
        logger.info("INFORMATION GRABBED {} ", SELECT_EMPLOYEE_QUERY);
        return calculateTotalAmount(procesadorMiembrosPlanillas.query(SELECT_EMPLOYEE_QUERY, rowMapper));
    }

    private boolean isValidEmployee(Employee employee) {
        return employee.isActive()
                && !employee.getName().isEmpty()
                && employee.getId() != 0
                && employee.getMonthlyPayment().compareTo(BigDecimal.ZERO) >= 0;
    }

    private long calculateTotalAmount(List<Employee> employees) {
        return employees.stream()
                .filter(this::isValidEmployee)
                .mapToLong(payment -> payment.getMonthlyPayment().longValue())
                .sum();
    }
}
