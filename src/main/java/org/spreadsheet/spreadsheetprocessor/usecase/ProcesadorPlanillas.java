package org.spreadsheet.spreadsheetprocessor.usecase;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spreadsheet.spreadsheetprocessor.domain.Employee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        return getValidationsAlign(procesadorMiembrosPlanillas.query(SELECT_EMPLOYEE_QUERY, rowMapper));
    }

    public Employee mapRowToEmployee(ResultSet rs) throws SQLException {
        return Employee.builder()
                .id(Long.parseLong(rs.getString(1)))
                .name(rs.getString(2))
                .monthlyPayment(new BigDecimal(rs.getString(3)))
                .active(Boolean.parseBoolean(rs.getString(4)))
                .build();
    }

    private long getValidationsAlign(List<Employee> employees) {
        return employees.stream()
                .filter(Employee::isActive)
                .filter(employee -> employee.getId() != 0)
                .map(Employee::getMonthlyPayment)
                .filter(monthlyPayment -> monthlyPayment.compareTo(BigDecimal.ZERO) >= 0)
                .mapToLong(BigDecimal::longValue)
                .sum();
    }
}
