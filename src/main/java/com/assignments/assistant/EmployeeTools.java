package com.assignments.assistant;

import com.assignments.common.Employee;
import com.assignments.common.EmployeeDirectory;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

/**
 * Java methods exposed to the LLM as AI Tools. The model decides when to
 * call them based on the user's natural-language request.
 */
public class EmployeeTools {

    @Tool("Get details of an employee (name, department, designation, skills) by employee ID such as E101")
    public String getEmployeeDetails(@P("employee ID, e.g. E101") String employeeId) {
        return EmployeeDirectory.findById(employeeId)
                .map(Employee::describe)
                .orElse("No employee found with ID " + employeeId);
    }

    @Tool("Calculate the annual salary from a monthly salary amount")
    public String calculateAnnualSalary(@P("monthly salary amount") double monthlySalary) {
        return String.format("Annual salary for a monthly salary of %.2f is %.2f",
                monthlySalary, monthlySalary * 12);
    }

    @Tool("Check the remaining leave balance of an employee by employee ID")
    public String checkLeaveBalance(@P("employee ID, e.g. E101") String employeeId) {
        return EmployeeDirectory.findById(employeeId)
                .map(e -> String.format("%s (%s) has %d of %d leaves remaining.",
                        e.name(), e.id(), e.leaveBalance(), e.totalLeaves()))
                .orElse("No employee found with ID " + employeeId);
    }

    @Tool("Calculate an employee's remaining leave balance after using a given number of leave days")
    public String calculateRemainingLeave(
            @P("employee ID, e.g. E101") String employeeId,
            @P("number of leave days the employee wants to use") int leaveDaysUsed) {
        return EmployeeDirectory.findById(employeeId)
                .map(e -> {
                    int remaining = e.leaveBalance() - leaveDaysUsed;
                    if (remaining < 0) {
                        return String.format(
                                "%s (%s) has only %d leaves remaining, so using %d days is not possible.",
                                e.name(), e.id(), e.leaveBalance(), leaveDaysUsed);
                    }
                    return String.format(
                            "%s (%s): after using %d leave days, %d of %d leaves will remain.",
                            e.name(), e.id(), leaveDaysUsed, remaining, e.totalLeaves());
                })
                .orElse("No employee found with ID " + employeeId);
    }
}
