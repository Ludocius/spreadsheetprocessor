package org.spreadsheet.spreadsheetprocessor.domain;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@Builder(toBuilder = true)
public class Employee {
    private Long id;
    private String name;
    private BigDecimal monthlyPayment;
    private boolean active;
}
