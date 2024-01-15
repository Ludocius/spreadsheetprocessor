package org.spreadsheet.spreadsheetprocessor.configuration;

import org.spreadsheet.spreadsheetprocessor.domain.Employee;
import org.spreadsheet.spreadsheetprocessor.processor.EmployeeItemProcessor;
import org.spreadsheet.spreadsheetprocessor.util.EmployeeRowMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

@Configuration
public class BeanConfiguration {
    @Bean
    public EmployeeItemProcessor processor() {
        return new EmployeeItemProcessor();
    }

    @Bean
    public RowMapper<Employee> employeeRowMapper() {
        return new EmployeeRowMapper();
    }

}
