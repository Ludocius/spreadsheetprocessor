package org.spreadsheet.spreadsheetprocessor.util;

import org.spreadsheet.spreadsheetprocessor.domain.Employee;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeRowMapper implements RowMapper<Employee> {

    @Override
    public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Employee.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .monthlyPayment(rs.getBigDecimal("monthlyPayment"))
                .active(rs.getBoolean("active"))
                .build();
    }
}