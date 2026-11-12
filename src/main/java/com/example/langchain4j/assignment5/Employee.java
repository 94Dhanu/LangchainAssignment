package com.example.langchain4j.assignment5;

/**
 * Employee record used by the AI tools in Assignment 5.
 */
public record Employee(
        String id,
        String name,
        String department,
        String designation,
        double monthlySalary,
        int leaveBalance) {

    @Override
    public String toString() {
        return "Employee ID: " + id
                + ", Name: " + name
                + ", Department: " + department
                + ", Designation: " + designation
                + ", Monthly Salary: " + monthlySalary
                + ", Leave Balance: " + leaveBalance + " days";
    }
}
