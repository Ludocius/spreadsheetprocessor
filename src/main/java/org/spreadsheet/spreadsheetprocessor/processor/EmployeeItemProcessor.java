package org.spreadsheet.spreadsheetprocessor.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spreadsheet.spreadsheetprocessor.domain.Employee;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;


public class EmployeeItemProcessor implements ItemProcessor<Employee, Employee> {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeItemProcessor.class);

    @Override
    public Employee process(Employee employee) {
        Long id = employee.getId();
        String name = employee.getName();
        BigDecimal monthlyPayment = employee.getMonthlyPayment();
        boolean active = employee.isActive();

        Employee transformedEmployee = Employee.builder()
                .id(id)
                .name(name)
                .monthlyPayment(monthlyPayment)
                .active(active)
                .build();
        logger.info("Converting ( {} ) into ( {} )", employee, transformedEmployee);

        return transformedEmployee;
    }
}