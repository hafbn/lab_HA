package training;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

public class CiceService {
    private static final BigDecimal MINIMUM_WAGE = new BigDecimal(1014.00);
    private static final BigDecimal CICE_RATE = new BigDecimal(0.06);
    public BigDecimal compute(final List<Employee> employees) {
        // Step 1: Eliminate interns from the employees list
        List<Employee> filteredEmployees = employees.stream()
                .filter(employee -> !employee.getIntern()) // Delete interns from list of employees
                .collect(Collectors.toList()); // collect into list filtered

        // Step 2: Calculate the total income and the basis
        BigDecimal max = MINIMUM_WAGE.multiply(new BigDecimal("2.5"));
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal basis = BigDecimal.ZERO;

        for (Employee employee : filteredEmployees) {
            BigDecimal employeeIncome = employee.getEarnings().stream()
                    // filter declared earnings
                    .filter(earning -> isDeclaredEarning(earning.getType()))
                    // get value of each earning
                    .map(Earning::getAmount)
                    // calculate sum of incomes
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // if sum of incomes is below the minimum wage * 2.5, we add in the basis calculation
            if (employeeIncome.compareTo(max) < 0) {
                basis = basis.add(employeeIncome);
            }

            // calculate total income
            income = income.add(employeeIncome);
        }

        // Step 3: Calculate CICE
        // rounding result
        BigDecimal cice = basis.multiply(CICE_RATE).setScale(0, RoundingMode.HALF_UP);

        return cice;
    }

    // Check if an earning type is declared
    private boolean isDeclaredEarning(EarningType earningType) {
        return earningType == EarningType.SALARY ||
                earningType == EarningType.OVERTIME ||
                earningType == EarningType.BONUS ||
                earningType == EarningType.VACATION_PAY;
    }
}
