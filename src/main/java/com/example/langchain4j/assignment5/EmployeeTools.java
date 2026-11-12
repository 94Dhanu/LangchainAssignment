package com.example.langchain4j.assignment5;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import dev.langchain4j.agent.tool.Tool;

/**
 * Java methods exposed to the LLM as AI Tools. The model inspects the tool
 * names/descriptions and invokes the right method to answer user requests.
 */
public class EmployeeTools {

    private static final List<Employee> EMPLOYEES = List.of(
            new Employee("E101", "Alice Johnson", "Engineering", "Senior Software Engineer", 7500, 18),
            new Employee("E102", "Bob Smith", "Engineering", "DevOps Engineer", 6800, 12),
            new Employee("E103", "Carol White", "Human Resources", "HR Manager", 8200, 20),
            new Employee("E104", "David Brown", "Finance", "Financial Analyst", 6100, 15),
            new Employee("E105", "Emma Davis", "Engineering", "Data Scientist", 7900, 9));

    private static final Map<String, Employee> BY_ID = EMPLOYEES.stream()
            .collect(Collectors.toMap(Employee::id, Function.identity()));

    @Tool("Retrieve full details of an employee by their employee ID, e.g. E101")
    public String getEmployeeDetails(String employeeId) {
        Employee employee = BY_ID.get(normalize(employeeId));
        return employee != null
                ? employee.toString()
                : "No employee found with ID " + employeeId;
    }

    @Tool("Calculate the annual salary from a given monthly salary")
    public double calculateAnnualSalary(double monthlySalary) {
        return monthlySalary * 12;
    }

    @Tool("Check the remaining paid leave balance in days for an employee by their employee ID")
    public String getLeaveBalance(String employeeId) {
        Employee employee = BY_ID.get(normalize(employeeId));
        return employee != null
                ? employee.name() + " (" + employee.id() + ") has " + employee.leaveBalance()
                        + " days of leave remaining."
                : "No employee found with ID " + employeeId;
    }

    @Tool("Calculate the remaining leave balance after an employee uses a given number of leave days")
    public String calculateRemainingLeave(String employeeId, int daysUsed) {
        Employee employee = BY_ID.get(normalize(employeeId));
        if (employee == null) {
            return "No employee found with ID " + employeeId;
        }
        int remaining = employee.leaveBalance() - daysUsed;
        if (remaining < 0) {
            return employee.name() + " cannot use " + daysUsed + " days; only "
                    + employee.leaveBalance() + " days are available.";
        }
        return "After using " + daysUsed + " days, " + employee.name() + " will have "
                + remaining + " days of leave remaining.";
    }

    private static String normalize(String employeeId) {
        return employeeId == null ? "" : employeeId.trim().toUpperCase();
    }
}
