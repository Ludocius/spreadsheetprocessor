package org.spreadsheet.spreadsheetprocessor.configuration;

import org.spreadsheet.spreadsheetprocessor.domain.Employee;
import org.spreadsheet.spreadsheetprocessor.processor.EmployeeItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfiguration {

    private static final String EMPLOYEE_ITEM_READER = "employeeItemReader";

    private static final String INSERT_EMPLOYEE_SQL =
            "INSERT INTO employee (id, name, monthlyPayment, active) VALUES (:id, :name, :monthlyPayment, :active)";

    @Value("${file.input}")
    private String fileInput;

    @Bean
    public FlatFileItemReader<Employee> reader() {
        return new FlatFileItemReaderBuilder<Employee>()
                .name(EMPLOYEE_ITEM_READER)
                .resource(new ClassPathResource(fileInput))
                .delimited()
                .names("id", "name", "monthlyPayment", "active")
                .fieldSetMapper(beanWrapperFieldSetMapper())
                .build();
    }

    private BeanWrapperFieldSetMapper<Employee> beanWrapperFieldSetMapper() {
        BeanWrapperFieldSetMapper<Employee> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Employee.class);
        return fieldSetMapper;
    }

    @Bean
    public JdbcBatchItemWriter<Employee> writer(DataSource dataSource) {
        return createItemWriter(dataSource);
    }

    private JdbcBatchItemWriter<Employee> createItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Employee>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql(INSERT_EMPLOYEE_SQL)
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository, JobCompletionNotificationListener listener, Step step1) {
        return new JobBuilder("importUserJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                      JdbcBatchItemWriter<Employee> writer, FlatFileItemReader<Employee> reader, EmployeeItemProcessor processor) {
        return new StepBuilder("step1", jobRepository)
                .<Employee, Employee>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}
