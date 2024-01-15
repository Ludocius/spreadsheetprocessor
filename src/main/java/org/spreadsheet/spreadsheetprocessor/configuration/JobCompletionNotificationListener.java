package org.spreadsheet.spreadsheetprocessor.configuration;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spreadsheet.spreadsheetprocessor.domain.Employee;
import org.spreadsheet.spreadsheetprocessor.usecase.ProcesadorPlanillas;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@RequiredArgsConstructor
@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;
    private final ProcesadorPlanillas procesadorPlanillas;

    private static final String SELECT_EMPLOYEE_QUERY = "SELECT id, name, monthlyPayment, active  FROM employee";


    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            logger.info("!!! JOB FINISHED! Time to verify the results");
            jdbcTemplate.query(SELECT_EMPLOYEE_QUERY, (rs, row) -> Employee.builder()
                            .id(Long.parseLong(rs.getString(1)))
                            .name(rs.getString(2))
                            .monthlyPayment(new BigDecimal(rs.getString(3)))
                            .active(Boolean.parseBoolean(rs.getString(4)))
                            .build())
                    .forEach(employee -> logger.info("Found < {} > in the database.", employee));
            logger.info("TOTAL AMOUNT TO PAY: {} ", procesadorPlanillas.totalAmountToPay());
        }
    }
}
